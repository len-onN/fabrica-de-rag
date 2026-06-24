# docs/plano-tecnico-mvp

Status: incorporada em `planejamento/documentacao`.

## Objetivo

Fechar as decisoes tecnicas minimas antes da primeira branch de codigo, mantendo o registro dentro da branch atual de planejamento.

## Tags

DOC, CODE, RUNTIME, CONTRACT, TEST, SEC, PERF

## Escopo

- Confirmar monorepo e estrutura `apps`, `infra`, `tests`, `scripts`.
- Definir versoes iniciais de Angular, Spring Boot, Java, Python, Postgres e Qdrant.
- Definir migrations, padrao REST, erros, paginacao e versionamento.
- Definir estrategia de teste por stack e comando unico de verificacao.
- Definir como os arquivos Compose serao organizados.
- Definir runtime inicial para MCP/tools de agentes.
- Transformar decisoes duradouras em ADRs.

## Fora de escopo

- Criar codigo de produto.
- Criar apps reais.
- Implementar migrations reais.

## Definido

- Monorepo como direcao inicial.
- Estrutura alvo: `apps/api`, `apps/web`, `apps/worker`, `apps/mcp`, `infra/compose`, `tests`, `scripts`, `docs`.
- Angular 22.x com Node 24 LTS.
- Spring Boot 4.1.x com Java 21 LTS.
- Python 3.13.x para worker.
- PostgreSQL 18.x, imagem inicial `postgres:18.4`.
- Qdrant 1.18.x, imagem inicial `qdrant/qdrant:v1.18.2`.
- Spring Boot modular monolith.
- Angular feature-first.
- Python worker com FastAPI/Pydantic.
- MCP server em TypeScript/Node 24 como adaptador fino sobre contratos do backend.
- Componentes proprios + Angular CDK como direcao de UI.
- Flyway como decisao inicial, salvo bloqueio.
- Compose base/dev/e2e.
- Scripts PowerShell como primeira interface operacional local.

## Definicoes encaminhadas para branch dona

- Modelo relacional, contratos, OpenAPI e payloads: `docs/modelo-dados-contratos-mvp`.
- Pipeline de ingestao, retry/cancel, storage e reindexacao: `docs/arquitetura-ingestao-rag`.
- Auth, roles, workspace scope e guardrails MCP/API: `docs/seguranca-permissoes-mvp`.
- Embeddings, LLM/mock, budgets e algoritmos: `docs/algoritmos-rag-mvp`.
- Analytics, retencao, export/delete e logs: `docs/analytics-observabilidade-mvp`.
- Ferramenta de lock do worker Python: `chore/worker-python-base`.
- Implementacao real de `scripts/check.ps1`: `chore/workspace-fundacao`.

## Estrategia

1. Ativar docs oficiais por stack.
2. Atualizar `docs/plano-tecnico-mvp.md`.
3. Atualizar ADRs quando decisoes ficarem aceitas.
4. Atualizar este arquivo com decisoes resolvidas.
5. Avancar para `docs/modelo-dados-contratos-mvp`.
6. Antes do codigo de produto, fechar os demais portoes `docs/*` de planejamento do MVP.

## Testabilidade

- Conferir links e consistencia documental.
- Validar que `chore/workspace-fundacao` nao tem bloqueios tecnicos.

## Fechamento

- Plano tecnico incorporado na branch atual.
- Registro de bordo atualizado.
- Proximo passo operacional: `docs/modelo-dados-contratos-mvp`.
