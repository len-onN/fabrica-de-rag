# chore/workspace-fundacao

Status: candidata.

## Objetivo

Criar a estrutura raiz do monorepo e a interface operacional inicial.

## Tags

CODE, RUNTIME, TEST, DOC

## Escopo

- Criar `apps/api`, `apps/web`, `apps/worker`, `apps/mcp`.
- Criar `infra/compose`, `tests/contracts`, `tests/e2e`, `tests/fixtures`, `scripts`.
- Criar arquivos base de convencao quando necessario.
- Criar scripts placeholder que falham com mensagem clara quando a stack ainda nao existe.
- Documentar a estrutura.

## Fora de escopo

- Criar projetos Spring/Angular/Python completos.
- Subir Docker Compose real.
- Implementar feature de negocio.

## Definido

- Monorepo.
- Separacao por app.
- Scripts na raiz como interface operacional.
- Node 24 LTS como runtime comum para web e mcp.
- Java 21 LTS, Python 3.13.x, PostgreSQL 18.x e Qdrant 1.18.x como linhas tecnicas iniciais.
- Scripts PowerShell como primeira interface local.

## Falta definir

- Scripts POSIX equivalentes, se forem necessarios antes de CI/Linux.
- Implementacao real dos comandos de verificacao por stack.

## Estrategia

1. Criar diretorios.
2. Criar README operacional curto, se necessario.
3. Criar scripts vazios/placeholder.
4. Garantir que nada sensivel foi versionado.

## Testabilidade

- Verificacao de estrutura.
- Scripts placeholder retornam exit code e mensagem esperada.

## Fechamento

- Repositorio pronto para receber apps reais.
- Estrutura ja contempla o runtime MCP, mesmo que o codigo de tools entre so em branch futura.
