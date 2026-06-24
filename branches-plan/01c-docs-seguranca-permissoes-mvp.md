# docs/seguranca-permissoes-mvp

Status: concluida.

## Objetivo

Fechar seguranca, autenticacao, autorizacao e workspace scope do MVP antes da primeira fatia vertical.

## Tags

DOC, SEC, CONTRACT, AGENT, TEST

## Escopo

- Definir bootstrap local de usuario/admin.
- Definir estrategia de login, sessao/token e logout.
- Definir hash de senha e armazenamento de credenciais locais.
- Definir matriz inicial de roles e permissoes.
- Definir workspace scope obrigatorio por recurso.
- Definir regras para API local e futuras chaves de API.
- Definir identidade e limites iniciais para agentes/MCP.
- Definir CORS/CSRF para o modo local.
- Definir auditoria minima de acoes sensiveis.

## Fora de escopo

- OIDC/OAuth externo.
- MFA.
- Convites.
- Row Level Security.
- Service accounts completas.

## Definido

- Backend valida autorizacao.
- UI nao e fronteira de seguranca.
- Roles iniciais existem desde o modelo.
- MCP nao acessa banco diretamente.
- Browser usa sessao server-side opaca em cookie `HttpOnly`.
- Mutacoes autenticadas por cookie exigem CSRF para SPA.
- CORS e same-origin por padrao, com allowlist explicita no perfil de desenvolvimento.
- Senha local usa `Argon2id` como algoritmo alvo.
- Bootstrap so funciona antes de existir usuario ativo.
- Matriz role x acao do MVP foi fechada.
- API keys futuras usam hash persistido, role `agent` e capabilities explicitas.
- MCP/agentes nao usam token passthrough de usuario humano.
- Auditoria minima usa eventos locais seguros ate existir trilha dedicada.

## Falta definir

- Valores finais de budgets numericos para busca/contexto/tools, dono: `docs/algoritmos-rag-mvp`.
- Taxonomia completa, retencao e dashboard de analytics/auditoria, dono: `docs/analytics-observabilidade-mvp`.
- Implementacao concreta de Spring Security, migrations, UI e testes, dono: `feat/auth-bootstrap-workspaces`.
- API keys reais para clientes externos, dono: `feat/api-rag-publica`.
- Tool schemas completos e transporte MCP, dono: `feat/mcp-tools-base`.
- OIDC/OAuth, MFA, convites, service accounts completas e RLS ficam fora do MVP.

## Fontes/skills ativadas

- Documentacao local do projeto.
- OWASP Password Storage, Authorization, CSRF Prevention e MCP Security Cheat Sheets.
- Spring Security CSRF para SPA.
- Nenhuma skill de implementacao de stack foi ativada porque esta branch fecha contrato documental, nao codigo Spring/Angular/MCP.

## Detalhes que devem ficar explicitos

- Fluxos de bootstrap, login, logout, refresh/expiracao e recuperacao/reset local quando aplicavel.
- Escolha entre cookie de sessao, token local ou abordagem hibrida, com impacto em CSRF/CORS.
- Matriz role x acao para owner, admin, curator, member, viewer e agent.
- Regra de workspace scope por recurso: colecao, documento, pagina, chunk, run, analytics, settings, API key e tool MCP.
- Padrao de enforcement no backend: guards/interceptors/services, erro quando workspace nao pertence ao usuario e filtro obrigatorio em queries.
- Politica de segredos: onde ficam chaves locais, como mascarar logs e como evitar analytics com credenciais.
- Identidade MCP/API: capability, workspace, role, limite, correlation id e auditoria.
- Guardrails minimos: limites de resultados/contexto, bloqueio cross-workspace, confirmacao humana para acoes destrutivas e sem acesso direto a banco/storage.
- Testes obrigatorios de autorizacao, incluindo dois workspaces e usuario sem permissao.
- Pontos OCP/LSP/IoC: novas roles, novas capabilities, novos recursos escopados e novas tools sem alterar validadores centrais de forma lateral; validadores e identity providers devem ser substituiveis sem relaxar permissao, workspace scope ou auditoria.

## Estrategia

1. Criar matriz de permissoes do MVP. Concluido em `docs/seguranca-permissoes-mvp.md`.
2. Definir contratos de login/bootstrap. Concluido com fixtures em `tests/contracts/rest`.
3. Mapear recursos escopados por workspace. Concluido em `docs/seguranca-permissoes-mvp.md`.
4. Definir testes obrigatorios de autorizacao. Concluido em `docs/seguranca-permissoes-mvp.md`.
5. Registrar ADR quando necessario. Concluido em ADR-024.

## Testabilidade

- Cada recurso do MVP tem regra de workspace scope.
- Cada endpoint sensivel tem role minima.
- Testes de isolamento entre workspaces sao obrigatorios.
- Tools MCP tem identidade, permissao e limite.
- Fixtures de contrato cobrem bootstrap, login, `me`, matriz de permissoes e contexto de tool agentica.
- Links internos e consistencia documental devem ser verificados antes do PR.

## Fechamento

- Branches de auth, API, analytics e MCP implementam regras ja decididas.
- Registro de bordo e diario de bordo atualizados.
- Proximo portao: `docs/algoritmos-rag-mvp`.
