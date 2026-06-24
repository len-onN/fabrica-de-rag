# Seguranca e Permissoes do MVP

## Objetivo

Este documento fecha o portao `docs/seguranca-permissoes-mvp`.

Ele define autenticacao local, sessao, autorizacao, workspace scope, API keys futuras, identidade de agentes/MCP, CORS/CSRF, auditoria minima e testes obrigatorios antes da primeira fatia vertical.

Ele nao implementa Spring Security, migrations, UI de login ou MCP real. As branches de codigo devem transformar estas regras em configuracao, use cases, migrations, DTOs, tests e fixtures.

## Decisoes principais

| Tema | Decisao |
| --- | --- |
| Auth do browser | Sessao server-side opaca em cookie `HttpOnly`, com segredo aleatorio e hash persistido no backend. |
| CSRF | Mutacoes autenticadas por cookie exigem token CSRF para SPA, enviado em header `X-XSRF-TOKEN`. |
| CORS | Same-origin por padrao; origens de desenvolvimento entram por allowlist explicita. |
| Senha local | `Argon2id` como algoritmo alvo, com parametros minimos OWASP e rehash quando parametros subirem. |
| Bootstrap | `POST /api/v1/bootstrap` so funciona quando nao ha usuario ativo. Cria user, workspace e membership `owner`. |
| Sessao | TTL idle de 8 horas e TTL absoluto de 7 dias. Logout revoga a sessao no servidor. |
| Reset local | Recuperacao de senha por email fica fora do MVP. Reset local deve ser acao operacional explicita, nunca endpoint publico sem autenticacao. |
| Autorizacao | RBAC por workspace combinado com atributos do recurso. Deny-by-default e permissao validada a cada request. |
| Workspace scope | Toda query de recurso de negocio filtra por `workspace_id` direto ou por relacao obrigatoria antes de retornar dados. |
| API local | Browser usa sessao. Clientes externos/API futura usam API key escopada ao workspace, hash persistido e role/capabilities restritas. |
| MCP/agentes | Agentes usam identidade propria `agent`, capability allowlist, limites e auditoria. Nao ha token passthrough de usuario humano. |
| Auditoria MVP | Acoes sensiveis geram eventos locais seguros, sem segredo, header de auth, texto integral, prompt completo ou PDF bruto. |

## Fontes ativadas

- Documentacao local: `docs/usuarios-e-acesso.md`, `docs/modelo-dados-contratos-mvp.md`, `docs/arquitetura-ingestao-rag.md`, `docs/skills-e-boas-praticas-para-agentes.md` e ADRs.
- OWASP Password Storage Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
- OWASP Authorization Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html
- OWASP CSRF Prevention Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
- OWASP MCP Security Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/MCP_Security_Cheat_Sheet.html
- Spring Security CSRF for SPA: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html

## Identidades

| Identidade | Uso | Autenticacao | Observacoes |
| --- | --- | --- | --- |
| `user` | Pessoa usando UI/API autenticada | Sessao de browser no MVP | Participa de workspaces por membership. |
| `api_key` | Cliente HTTP externo futuro | Bearer token com segredo exibido uma vez | Sempre escopada a workspace, role e capabilities. |
| `agent` | MCP/API agentica | API key/capability dedicada | Nao herda credencial pessoal por passthrough. |
| `system` | Jobs internos | Contexto interno criado pelo backend | Nao inicia acoes de usuario sem run/ator/correlation id. |

Roles continuam por workspace: `owner`, `admin`, `curator`, `member`, `viewer` e `agent`.

`agent` e uma role restrita: alem da role, cada chamada precisa de capability explicita, limites e recurso permitido. A role `agent` nunca recebe permissoes de administracao do workspace.

## Fluxos de autenticacao

### Bootstrap

Endpoint:

```text
POST /api/v1/bootstrap
```

Pre-condicoes:

- nao existe usuario ativo;
- request tem CSRF valido quando o frontend ja estiver sob a mesma origem, ou usa fluxo inicial explicitamente liberado antes de haver sessao;
- payload contem nome, email, senha, workspace e finalidade.

Resultado:

- cria `users`, `auth_identities`, `workspaces` e `workspace_memberships`;
- role inicial: `owner`;
- cria sessao autenticada para o primeiro usuario;
- registra evento `security.bootstrap_completed`;
- chamadas posteriores retornam `409 bootstrap_already_completed`.

### Login

Endpoint:

```text
POST /api/v1/auth/login
```

Regras:

- resposta de credencial invalida nao revela se email existe;
- rate limiting por origem e identificador normalizado deve entrar na branch backend;
- login bem-sucedido revoga sessoes marcadas como comprometidas quando houver sinal futuro;
- login cria nova sessao, rotaciona cookie e emite novo token CSRF.

### Logout

Endpoint:

```text
POST /api/v1/auth/logout
```

Regras:

- exige sessao valida e CSRF;
- revoga o registro de sessao no servidor;
- limpa cookie de sessao e CSRF;
- deve ser idempotente para a experiencia do usuario.

### Sessao atual

Endpoint:

```text
GET /api/v1/me
GET /api/v1/auth/csrf
```

`GET /me` retorna usuario, workspaces visiveis, workspace ativo sugerido, role e capabilities derivadas.

`GET /auth/csrf` existe para SPA obter ou renovar o cookie de CSRF depois de login, logout ou expiracao.

## Sessao e cookies

Cookie de sessao:

```text
__Host-rag_session=<secret>
Path=/
HttpOnly
Secure
SameSite=Lax
```

Regras:

- usar prefixo `__Host-` quando HTTPS estiver ativo;
- nao definir atributo `Domain`;
- perfil local `http://localhost` pode usar cookie sem `Secure` apenas em desenvolvimento;
- o valor do cookie e segredo aleatorio de pelo menos 256 bits;
- persistir apenas hash do segredo em `auth_sessions.session_hash`;
- rotacionar sessao em login e quando houver elevacao futura de privilegio;
- atualizar `last_seen_at` com throttling para evitar escrita em toda request;
- rejeitar sessoes expiradas, revogadas ou de usuario desativado.

TTL inicial:

| Tipo | Valor inicial | Regra |
| --- | --- | --- |
| Idle timeout | 8 horas | Se nao houver atividade, exigir novo login. |
| Absolute timeout | 7 dias | Mesmo com atividade, exigir novo login. |
| CSRF token | Vinculado a sessao | Novo token apos login/logout e quando a sessao muda. |

## CSRF e CORS

Como a UI usa cookie, toda request insegura precisa de defesa CSRF.

Requests inseguras:

```text
POST
PUT
PATCH
DELETE
```

Padrao para SPA:

- backend emite cookie `XSRF-TOKEN` nao `HttpOnly`;
- Angular envia o valor no header `X-XSRF-TOKEN`;
- Spring Security valida o token antes do controller;
- token ausente, invalido ou expirado retorna `403 csrf_invalid`;
- Origin/Referer deve ser validado como defesa em profundidade quando presente.

CORS:

- producao/self-hosted: mesma origem por padrao;
- desenvolvimento: allowlist explicita para `http://localhost:4200` e `http://127.0.0.1:4200`;
- nunca usar `Access-Control-Allow-Origin: *` com credenciais;
- metodos, headers e credentials devem ser declarados por profile;
- API key para cliente externo nao habilita CORS amplo automaticamente.

## Senhas e credenciais locais

Hash de senha:

- algoritmo alvo: `Argon2id`;
- parametros minimos: memoria 19 MiB, iteracoes 2 e paralelismo 1;
- a branch backend pode elevar parametros se o tempo medio de hash ficar abaixo do alvo operacional;
- hash deve carregar algoritmo e parametros para permitir rehash no proximo login;
- se `Argon2id` estiver bloqueado por dependencia real da stack, a branch backend deve registrar ADR ou decisao operacional antes de usar fallback.

Politica de senha do MVP:

- minimo de 12 caracteres;
- maximo de 256 caracteres;
- aceitar espacos e passphrases;
- nao exigir composicao artificial de simbolos;
- rejeitar senha vazia, senha igual ao email e senha comum quando uma lista local simples existir;
- nunca registrar senha, hash, token, cookie ou header de auth em logs, eventos ou analytics.

Reset:

- recuperacao por email fica fora do MVP;
- reset local deve ser comando operacional ou fluxo autenticado futuro;
- endpoint publico de reset sem autenticacao fica proibido.

## Autorizacao

O modelo combina role por workspace com atributos do recurso:

```text
ator autenticado
-> workspace ativo ou inferido do recurso
-> membership ativa
-> role
-> permissao requerida
-> atributos do recurso
-> decisao allow/deny
```

Regras:

- negar por padrao;
- validar permissao em toda request;
- UI pode esconder acoes, mas nunca decide acesso;
- IDs publicos nao bastam para autorizacao;
- carregar recurso sempre com filtro de workspace ou checar workspace antes de enriquecer resultado;
- para recursos por path sem `workspaceId`, inferir workspace pelo recurso e validar membership antes de retornar qualquer campo;
- respostas `404` podem ser usadas para recurso inexistente ou fora do workspace quando isso reduzir enumeracao;
- `403` deve ser usado quando o usuario autenticado sabe que o workspace/recurso existe, mas nao tem a permissao.

## Matriz de permissoes do MVP

Legenda:

- `Y`: permitido pela role.
- `C`: permitido somente com capability explicita e limites.
- `-`: negado.

| Permissao | owner | admin | curator | member | viewer | agent |
| --- | --- | --- | --- | --- | --- | --- |
| `workspace.read` | Y | Y | Y | Y | Y | C |
| `workspace.update` | Y | Y | - | - | - | - |
| `workspace.delete` | Y | - | - | - | - | - |
| `members.read` | Y | Y | - | - | - | - |
| `members.manage` | Y | Y | - | - | - | - |
| `settings.read` | Y | Y | Y | Y | Y | - |
| `settings.update` | Y | Y | - | - | - | - |
| `collection.create` | Y | Y | Y | - | - | - |
| `collection.read` | Y | Y | Y | Y | Y | C |
| `collection.update` | Y | Y | Y | - | - | - |
| `collection.delete` | Y | Y | - | - | - | - |
| `document.upload` | Y | Y | Y | - | - | - |
| `document.read` | Y | Y | Y | Y | Y | C |
| `document.archive` | Y | Y | Y | - | - | - |
| `document.file.read` | Y | Y | Y | Y | Y | C |
| `ingest_run.start` | Y | Y | Y | - | - | - |
| `ingest_run.read` | Y | Y | Y | Y | Y | C |
| `ingest_run.cancel` | Y | Y | Y | - | - | - |
| `ingest_run.retry` | Y | Y | Y | - | - | - |
| `page_map.read` | Y | Y | Y | Y | Y | C |
| `page_map.edit` | Y | Y | Y | - | - | - |
| `chunk.read` | Y | Y | Y | Y | Y | C |
| `chunk.curate` | Y | Y | Y | - | - | - |
| `rag.search` | Y | Y | Y | Y | Y | C |
| `rag.context` | Y | Y | Y | Y | Y | C |
| `rag.ask` | Y | Y | Y | Y | Y | C |
| `feedback.create` | Y | Y | Y | Y | Y | - |
| `analytics.view` | Y | Y | Y | - | - | - |
| `analytics.export` | Y | Y | - | - | - | - |
| `analytics.delete` | Y | Y | - | - | - | - |
| `api_key.create` | Y | Y | - | - | - | - |
| `api_key.revoke` | Y | Y | - | - | - | - |
| `vector_connection.create` | Y | Y | - | - | - | - |
| `vector_connection.use` | Y | Y | Y | - | - | - |
| `vector_connection.manage` | Y | Y | - | - | - | - |
| `vector_binding.create` | Y | Y | Y | - | - | - |
| `mcp.use` | - | - | - | - | - | C |
| `mcp.manage` | Y | Y | - | - | - | - |

Roles `member` e `viewer` sao iguais para consulta no MVP; a diferenca inicial e de produto/UX e permite endurecer `member` depois sem mudar o modelo. `viewer` nao cria feedback mutante se a branch de laboratorio decidir tratar feedback como dado de avaliacao sensivel; ate la, feedback do viewer fica permitido por esta matriz para consultas autenticadas.

## Endpoint x permissao

| Endpoint | Permissao minima |
| --- | --- |
| `POST /api/v1/bootstrap` | publico apenas antes do primeiro usuario |
| `POST /api/v1/auth/login` | publico com rate limit |
| `POST /api/v1/auth/logout` | sessao autenticada |
| `GET /api/v1/me` | sessao autenticada |
| `GET /api/v1/workspaces` | membership ativa |
| `GET /api/v1/workspaces/{workspaceId}` | `workspace.read` |
| `PATCH /api/v1/workspaces/{workspaceId}` | `workspace.update` |
| `GET /api/v1/workspaces/{workspaceId}/members` | `members.read` |
| `GET /api/v1/workspaces/{workspaceId}/settings` | `settings.read` |
| `PATCH /api/v1/workspaces/{workspaceId}/settings` | `settings.update` |
| `GET /api/v1/workspaces/{workspaceId}/collections` | `collection.read` |
| `POST /api/v1/workspaces/{workspaceId}/collections` | `collection.create` |
| `GET /api/v1/collections/{collectionId}` | `collection.read` |
| `PATCH /api/v1/collections/{collectionId}` | `collection.update` |
| `DELETE /api/v1/collections/{collectionId}` | `collection.delete` |
| `GET /api/v1/collections/{collectionId}/documents` | `document.read` |
| `POST /api/v1/collections/{collectionId}/documents` | `document.upload` |
| `GET /api/v1/documents/{documentId}` | `document.read` |
| `GET /api/v1/documents/{documentId}/file` | `document.file.read` |
| `DELETE /api/v1/documents/{documentId}` | `document.archive` |
| `POST /api/v1/documents/{documentId}/ingest-runs` | `ingest_run.start` |
| `GET /api/v1/ingest-runs/{runId}` | `ingest_run.read` |
| `POST /api/v1/ingest-runs/{runId}/cancel` | `ingest_run.cancel` |
| `POST /api/v1/ingest-runs/{runId}/retry` | `ingest_run.retry` |
| `GET /api/v1/documents/{documentId}/pages` | `page_map.read` |
| `PATCH /api/v1/documents/{documentId}/pages/{pageId}` | `page_map.edit` |
| `POST /api/v1/documents/{documentId}/pages/bulk-update` | `page_map.edit` |
| `POST /api/v1/documents/{documentId}/page-numbering/anchors` | `page_map.edit` |
| `POST /api/v1/documents/{documentId}/page-numbering/recalculate` | `page_map.edit` |
| `GET /api/v1/collections/{collectionId}/chunks` | `chunk.read` |
| `GET /api/v1/chunks/{chunkId}` | `chunk.read` |
| `GET /api/v1/chunks/{chunkId}/neighbors` | `chunk.read` |
| `POST /api/v1/collections/{collectionId}/search` | `rag.search` |
| `POST /api/v1/collections/{collectionId}/context/assemble` | `rag.context` |
| `POST /api/v1/collections/{collectionId}/ask` | `rag.ask` |
| `POST /api/v1/retrieval-feedback` | `feedback.create` |
| `GET /api/v1/workspaces/{workspaceId}/analytics/summary` | `analytics.view` |
| `GET /api/v1/collections/{collectionId}/analytics/summary` | `analytics.view` |
| `GET /api/v1/analytics/events` | `analytics.view` |
| `POST /api/v1/analytics/events` | internal event writer or controlled client contract |
| `PATCH /api/v1/workspaces/{workspaceId}/analytics/settings` | `settings.update` |
| `DELETE /api/v1/workspaces/{workspaceId}/analytics/events` | `analytics.delete` |

## Workspace scope por recurso

| Recurso | Regra de escopo |
| --- | --- |
| `workspaces` | usuario precisa de membership ativa ou ser owner. |
| `workspace_memberships` | sempre por `workspace_id`; convites ficam fora do MVP. |
| `knowledge_collections` | `workspace_id` direto e filtro obrigatorio. |
| `documents` | `workspace_id` direto e `knowledge_collection_id` do mesmo workspace. |
| `document_pages` | `workspace_id` direto e `document_id` do mesmo workspace. |
| `document_elements` | `workspace_id` direto e pagina/documento do mesmo workspace. |
| `assets` | `workspace_id` direto; backend resolve storage URI, worker nao recebe path livre. |
| `chunks` | `workspace_id` e `knowledge_collection_id` obrigatorios. |
| `chunk_embeddings` | `workspace_id`; Qdrant sempre recebe filtro `workspaceId` e colecao autorizada. |
| `ingest_runs` | `workspace_id`, documento e colecao do mesmo workspace. |
| `analytics_events` | `workspace_id`, retencao e export/delete por permissao. |
| `api_keys` | `workspace_id`, hash do segredo e capabilities escopadas. |
| `vector_connections` | `owner_scope` pessoal ou workspace; uso exige permissao separada de gerenciamento. |
| `mcp_sessions` futuro | workspace, agent identity, capabilities, limites e correlation id. |

## API keys e API publica futura

API keys ficam planejadas para `feat/api-rag-publica` e `feat/mcp-tools-base`, mas o contrato de seguranca e definido aqui.

Formato conceitual:

```text
rag_key_<publicId>.<secret>
```

Regras:

- mostrar segredo apenas uma vez;
- persistir hash do segredo, nunca o token bruto;
- prefixo/public id identifica o registro sem autorizar a chamada;
- toda key tem `workspace_id`, `role`, `capabilities`, `expires_at`, `revoked_at`, `created_by_user_id` e limites;
- role maxima de API key no MVP: `agent`;
- owner/admin podem criar e revogar;
- key expirada, revogada ou sem capability retorna `401` ou `403` com erro seguro;
- logs mostram apenas public id e ultimos caracteres mascarados;
- perguntas, respostas, prompts e documentos nao entram no evento de key por padrao.

Capabilities iniciais:

```text
rag.search
rag.context
rag.ask
chunk.read
document.read
page_map.read
mcp.use
```

Capabilities mutantes ficam fora do MVP para agentes.

## MCP e guardrails de agentes

O MCP server em `apps/mcp` sera adaptador fino sobre o backend.

Regras:

- nenhuma tool acessa Postgres, Qdrant, storage local ou filesystem livre diretamente;
- toda tool chama backend com identidade `agent` e capability explicita;
- tool input usa schema fechado, sem parametros extras;
- tool output e tratado como dado nao confiavel pelo agente;
- documento recuperado, chunk, interpretacao visual e texto de PDF nunca podem virar instrucao de sistema;
- todas as chamadas carregam `workspaceId`, `correlationId`, `toolName`, `capability` e limites;
- ferramentas destrutivas, exportacoes sensiveis e mudancas de configuracao exigem confirmacao humana e ficam fora do MVP inicial;
- tools de leitura devem aplicar limites de quantidade, tamanho de texto, tempo e budget;
- o MCP local deve preferir binding em loopback ou stdio; HTTP remoto exige TLS e decisao propria futura;
- token passthrough de usuario humano e proibido.

Tools iniciais permitidas quando a branch MCP chegar:

| Tool | Capability | Mutante? |
| --- | --- | --- |
| `search_chunks` | `rag.search` | Nao |
| `get_chunk` | `chunk.read` | Nao |
| `expand_context` | `rag.context` | Nao |
| `ask_rag` | `rag.ask` | Nao |

## Auditoria minima

No MVP, auditoria minima usa eventos locais versionados ate que uma trilha de audit dedicada exista.

Eventos obrigatorios:

| Evento | Quando |
| --- | --- |
| `security.bootstrap_completed` | Primeiro ambiente local criado. |
| `security.login_succeeded` | Login valido. |
| `security.login_failed` | Login invalido, sem revelar existencia de email. |
| `security.logout_succeeded` | Sessao revogada. |
| `security.permission_denied` | Request autenticada negada por permissao. |
| `security.csrf_rejected` | Mutacao recusada por CSRF. |
| `security.api_key_created` | API key criada. |
| `security.api_key_revoked` | API key revogada. |
| `security.agent_tool_denied` | Tool MCP/API negada por capability, limite ou workspace. |
| `workspace.settings_updated` | Configuracao sensivel alterada. |
| `workspace.analytics_deleted` | Historico local limpo. |
| `document.archived` | Documento removido da experiencia ativa. |

Campos minimos:

```json
{
  "eventVersion": "analytics.event.v1",
  "eventName": "security.permission_denied",
  "origin": "api",
  "workspaceId": "wsp_fixture_alpha",
  "actor": {
    "type": "user",
    "id": "usr_fixture_owner"
  },
  "correlationId": "req_fixture_001",
  "resource": {
    "type": "collection",
    "id": "col_fixture_rag"
  },
  "properties": {
    "permission": "collection.delete",
    "decision": "deny",
    "reason": "role_not_allowed"
  }
}
```

Campos proibidos:

- senha, hash de senha, token, API key, session id bruto;
- header `Authorization`, cookie, CSRF token;
- connection string e segredo de provider;
- PDF bruto, embedding vector, texto integral de documento;
- prompt completo ou resposta completa quando historico completo estiver desligado.

## Erros padronizados

| Codigo | HTTP | Uso |
| --- | --- | --- |
| `unauthenticated` | 401 | Sessao/API key ausente ou invalida. |
| `session_expired` | 401 | Sessao expirada. |
| `csrf_invalid` | 403 | Token CSRF ausente ou invalido. |
| `permission_denied` | 403 | Ator autenticado sem permissao. |
| `workspace_not_found` | 404 | Workspace inexistente ou invisivel. |
| `resource_not_found` | 404 | Recurso inexistente ou fora do workspace. |
| `bootstrap_already_completed` | 409 | Setup inicial ja foi feito. |
| `api_key_revoked` | 401 | Key revogada. |
| `api_key_expired` | 401 | Key expirada. |
| `capability_denied` | 403 | Agent/API key sem capability. |
| `rate_limited` | 429 | Limite de auth/tool/API atingido. |

## Pontos OCP/LSP/IoC

Pontos de extensao:

- `IdentityProvider` para local agora e OIDC/OAuth futuro;
- `PasswordHasher` para Argon2id e rehash progressivo;
- `SessionStore` para banco no MVP e cache distribuido futuro;
- `CsrfPolicy` para SPA local e futuros clientes;
- `AuthorizationPolicy` para role/capability/resource attributes;
- `WorkspaceScopeResolver` para recursos por path sem `workspaceId`;
- `ApiCredentialVerifier` para API keys e service accounts futuras;
- `McpCapabilityPolicy` para tools e limites;
- `AuditPublisher` para eventos locais e audit trail futuro.

Invariantes LSP:

- providers de identidade preservam `actor`, `workspace`, `role`, `capabilities` e estado de autenticacao;
- `PasswordHasher` nunca retorna senha bruta e sempre indica necessidade de rehash quando parametros envelhecem;
- stores de sessao preservam expiracao, revogacao, hash do segredo e lookup seguro;
- policies de autorizacao negam por padrao e nunca relaxam workspace scope;
- verificadores de API key preservam expiracao, revogacao, capability e auditabilidade;
- MCP local/remoto preserva identity, workspace, limits e schemas fechados.

IoC:

- Spring Security filter chain autentica e cria contexto;
- use cases recebem `CurrentActor` e ports de autorizacao, nao leem cookie/header diretamente;
- controllers apenas traduzem request/response;
- repositories recebem workspace scope como parametro obrigatorio;
- setup MCP liga tools a cliente backend, nao a infraestrutura direta.

## Testabilidade obrigatoria

Testes de contrato:

- fixtures de bootstrap, login, sessao atual e contexto de tool agentica;
- erro Problem Details para `unauthenticated`, `csrf_invalid`, `permission_denied` e `capability_denied`;
- role matrix como fixture regressiva.

Testes backend:

- bootstrap cria primeiro owner e bloqueia segundo bootstrap;
- login invalido nao revela email existente;
- login valido cria cookie `HttpOnly` e CSRF;
- logout revoga sessao;
- sessao expirada nega request;
- CSRF ausente nega `POST/PATCH/DELETE` autenticado por cookie;
- CORS permite somente origens configuradas;
- cada permissao da matriz tem teste allow/deny por role;
- dois workspaces com recurso de mesmo tipo nao vazam dados;
- recurso por public id fora do workspace retorna `404` ou `403` conforme regra;
- Qdrant/search sempre recebe filtro de workspace e colecao autorizada;
- API key expirada/revogada/sem capability e negada;
- agent nao consegue chamar ferramenta mutante nem recurso fora do workspace;
- eventos de auditoria nao carregam segredo, token, cookie ou texto integral.

Testes frontend:

- setup mostra erro de bootstrap ja concluido;
- login mostra erro generico para credencial invalida;
- rotas protegidas redirecionam quando nao autenticado;
- usuario sem permissao ve estado de permissao negada ou acao oculta conforme UX;
- requests mutantes enviam header CSRF.

Testes MCP:

- schema de tool rejeita campos extras;
- tool sem capability retorna erro seguro;
- tool respeita limite de resultados/contexto;
- tool output e estruturado e nao inclui instrucoes internas.

## Pendencias encaminhadas

- valores finais de budget de contexto, top-k e tokens: `docs/algoritmos-rag-mvp`;
- taxonomia completa de analytics, retencao e export/delete: `docs/analytics-observabilidade-mvp`;
- service accounts completas, convites, OIDC/OAuth, MFA e RLS: fora do MVP;
- implementacao real de Spring Security, migrations e UI: `feat/auth-bootstrap-workspaces`;
- API keys reais: `feat/api-rag-publica`;
- MCP server e tool schemas completos: `feat/mcp-tools-base`.
