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

## Falta definir

- Estrategia de login local.
- Forma de armazenar senha/credencial no MVP.
- Granularidade inicial de permissoes.

## Estrategia

1. Modelar schema.
2. Criar use cases de bootstrap.
3. Criar endpoints.
4. Criar UI minima.
5. Testar isolamento com dois workspaces.

## Testabilidade

- Migration.
- Teste de autorizacao.
- Teste de workspace scope.
- UI cobre estado inicial.

## Fechamento

- UI + API + banco + permissao conversam em uma fatia vertical.

