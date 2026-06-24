# docs/seguranca-permissoes-mvp

Status: candidata.

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

## Falta definir

- Mecanismo exato de sessao/token local.
- Matriz acao x role.
- Politica de expiracao de sessao.
- Como representar usuario unico sem bloquear deploy multiusuario futuro.
- Guardrails minimos para tools MCP.
- Hash de senha, armazenamento de credenciais locais e politica de bootstrap/reset do primeiro admin.
- Politica de CORS/CSRF/cookies para modo local e deploy futuro.
- Modelo de identidade para agentes/API local sem token passthrough.
- Eventos de auditoria minima para acoes sensiveis.

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

1. Criar matriz de permissoes do MVP.
2. Definir contratos de login/bootstrap.
3. Mapear recursos escopados por workspace.
4. Definir testes obrigatorios de autorizacao.
5. Registrar ADR quando necessario.

## Testabilidade

- Cada recurso do MVP tem regra de workspace scope.
- Cada endpoint sensivel tem role minima.
- Testes de isolamento entre workspaces sao obrigatorios.
- Tools MCP tem identidade, permissao e limite.

## Fechamento

- Branches de auth, API, analytics e MCP implementam regras ja decididas.
