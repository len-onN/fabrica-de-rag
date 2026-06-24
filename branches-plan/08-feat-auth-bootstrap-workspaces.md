# feat/auth-bootstrap-workspaces

Status: candidata.

## Objetivo

Criar a primeira fatia vertical real: bootstrap local, usuario/admin, workspace padrao, membership e roles basicas.

## Tags

CODE, DATA, CONTRACT, UX, TEST, SEC, OBS

## Escopo

- Criar tabelas de usuarios, workspaces e memberships.
- Criar bootstrap do primeiro admin.
- Criar roles basicas por workspace.
- Criar endpoints minimos de auth/bootstrap/workspace.
- Criar UI minima de setup e workspace ativo.
- Registrar eventos basicos de bootstrap.

## Fora de escopo

- Convites.
- SSO/OAuth externo.
- MFA.
- Auditoria enterprise.

## Definido

- Roles por workspace.
- Backend valida permissao.
- Workspace scope e fronteira de dados.
- Login local por sessao server-side opaca.
- CSRF obrigatorio para mutacoes autenticadas por cookie.
- Bootstrap cria primeiro owner apenas quando nao existe usuario ativo.
- Matriz role x acao definida em `docs/seguranca-permissoes-mvp.md`.

## Falta definir

- Detalhes de configuracao Spring Security conforme dependencias reais da branch.
- Parametros finais de tuning do `Argon2id` se a performance local exigir ajuste acima do minimo definido.

## Estrategia

1. Modelar schema.
2. Criar use cases de bootstrap.
3. Criar endpoints.
4. Criar UI minima.
5. Aplicar sessao, CSRF e matriz de permissoes.
6. Testar isolamento com dois workspaces.

## Testabilidade

- Migration.
- Teste de autorizacao.
- Teste de workspace scope.
- UI cobre estado inicial.

## Fechamento

- UI + API + banco + permissao conversam em uma fatia vertical.
