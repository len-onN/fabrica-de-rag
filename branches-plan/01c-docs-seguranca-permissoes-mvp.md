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
