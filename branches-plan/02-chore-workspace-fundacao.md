# chore/workspace-fundacao

Status: em fechamento.

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
- `scripts/check.ps1` como verificacao inicial da estrutura raiz.
- Scripts de testes, smoke, e2e e Compose como placeholders explicitos ate as branches donas implementarem comportamento real.

## Falta definir

- Scripts POSIX equivalentes, se forem necessarios antes de CI/Linux.
- Implementacao real dos comandos de verificacao por stack.
- Conteudo executavel de `apps/api`, `apps/web`, `apps/worker` e `apps/mcp`, cada um em sua branch propria.

## Estrategia

1. Criar diretorios.
2. Criar README operacional curto, se necessario.
3. Criar scripts vazios/placeholder.
4. Garantir que nada sensivel foi versionado.

## Testabilidade

- Verificacao de estrutura.
- Scripts placeholder retornam exit code e mensagem esperada.
- `scripts/check.ps1` deve retornar `0` quando a estrutura esperada existir.

## Gates aplicaveis

- OCP: a fundacao cria pontos de entrada por pasta e script, sem escolher detalhes internos das stacks futuras.
- LSP: placeholders devem ter semantica previsivel: comandos inexistentes retornam `2` com mensagem clara ate serem substituidos por implementacoes reais.
- IoC/DI: nao aplicavel em codigo de dominio nesta branch; composition roots reais ficam para as branches das apps.
- Compatibilidade: preservar `tests/contracts` ja versionado pelos portoes documentais.

## Fechamento

- Repositorio pronto para receber apps reais.
- Estrutura ja contempla o runtime MCP, mesmo que o codigo de tools entre so em branch futura.
- Registro de bordo e diario atualizados com comandos executados.
