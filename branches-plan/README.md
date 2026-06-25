# Branches Plan

## Objetivo

Esta pasta contem o planejamento operacional por branch. Cada branch tem seu proprio arquivo `.md`, com escopo, fora de escopo, elementos afetados, decisoes ja tomadas, pendencias, estrategia de implementacao e testabilidade.

Os nomes dos arquivos usam a ordem planejada e substituem `/` por `-`, porque `/` e separador de diretorio no Windows. Quando uma branch precisa ser inserida entre duas ja mapeadas, usamos sufixos como `01a`, `11a` e `20a` para preservar historico sem renomear todos os arquivos.

Exemplo:

```text
Branch: docs/plano-tecnico-mvp
Arquivo: 01-docs-plano-tecnico-mvp.md

Branch: docs/modelo-dados-contratos-mvp
Arquivo: 01a-docs-modelo-dados-contratos-mvp.md
```

## Linguagem operacional

Usaremos estes nomes para identificar elementos de codigo e runtime.

| Termo | Tipo | Significado |
| --- | --- | --- |
| App | CODE | Aplicacao versionada no monorepo: `apps/web`, `apps/api`, `apps/worker`, `apps/mcp`. |
| Module | CODE | Agrupamento interno por dominio ou responsabilidade. |
| Boundary | CODE | Fronteira entre dominios, stacks ou sistemas externos. |
| Use case | CODE | Caso de uso de aplicacao, normalmente transacional no backend. |
| Port | CODE | Contrato interno consumido por application/domain para depender de abstracao, nao de adapter concreto. |
| Adapter | CODE | Implementacao concreta para banco, worker, Qdrant, LLM, storage ou API externa. |
| Policy/Strategy | CODE | Regra substituivel para algoritmo, contexto, chunking, permissao, budget ou retencao. |
| Extension point | CODE | Lugar explicito onde uma nova capacidade entra sem alterar fluxo central estavel. |
| Contract | CONTRACT | DTO, schema, evento, tool schema ou request/response versionado. |
| Migration | DATA | Mudanca versionada de schema relacional. |
| Fixture | TEST | Dado pequeno e deterministico para testes. |
| Seed | TEST | Dado carregado para ambiente dev/e2e. |
| Profile | RUNTIME | Configuracao de execucao: `dev`, `test`, `e2e`. |
| Compose layer | RUNTIME | Arquivo Compose base, dev ou e2e. |
| Runtime dependency | RUNTIME | Postgres, Qdrant, worker, MCP server, navegador e servicos auxiliares. |
| Job | RUNTIME | Trabalho executado em etapas, como ingestao. |
| Run | DATA | Registro persistido de uma execucao de job. |
| Event | OBS | Evento de analytics local ou auditoria. |
| Health check | OBS | Verificacao de vida/prontidao de app ou dependencia. |
| Smoke | TEST | Teste curto que prova que a stack sobe e responde. |
| E2E | TEST | Fluxo ponta a ponta de alto valor. |
| Tool | AGENT | Ferramenta exposta para agente ou MCP. |
| Guardrail | AGENT | Regra de limite, permissao, confirmacao ou bloqueio. |
| Workspace scope | SEC | Garantia de que dado/acao pertence ao workspace correto. |

## Tags de planejamento

```text
CODE       codigo da aplicacao
RUNTIME    compose, profile, container, health, env
DATA       banco, migration, storage, seed
CONTRACT   REST, Pydantic, OpenAPI, MCP tool schema, evento
UX         tela, rota, estado visual, interacao
TEST       unit, contract, integration, smoke, e2e
SEC        auth, authorization, workspace, secrets
OBS        logs, analytics, tracing, audit
PERF       tempo, memoria, payload, N+1, index, batch
DOC        README, ADR, diario, registro
AGENT      MCP, tools, skills, guardrails
```

## O que ja esta definido

- Stacks macro: Angular 22, Node 24 LTS, Spring Boot 4.1, Java 21 LTS, Python 3.13, PostgreSQL 18, Qdrant 1.18, Docker Compose e MCP TypeScript/Node.
- MVP focado em PDF.
- Monorepo como direcao inicial.
- Desenvolvimento por `develop -> branch -> PR`.
- Branches pequenas, com escopo revisavel.
- Fundacao minima testavel antes das fatias verticais.
- Testes em camadas: unit, contract, integration, smoke e e2e curto.
- Compose e2e canonico em `infra/compose/compose.e2e.yml`.
- Agentes por ferramentas guiadas, menor privilegio, guardrails e analytics local.
- `docs/plano-tecnico-mvp` incorporada em `planejamento/documentacao`.
- Cobertura do MVP revisada em `docs/cobertura-mvp-branches.md`.
- Decisoes estruturais complexas devem ter branch `docs/*` antes da implementacao.
- Gate SOLID/OCP-LSP-IoC: branches devem expandir comportamento por contratos, ports, adapters, policies, strategies ou schemas, garantir substituibilidade das implementacoes e ligar concretos por injecao/composicao, evitando mudanca lateral em fluxos ja estabilizados.
- Seguranca do MVP definida em `docs/seguranca-permissoes-mvp.md`: sessao opaca, CSRF, CORS, Argon2id, matriz de permissoes, workspace scope, API keys futuras e guardrails MCP/API.
- Algoritmos RAG do MVP definidos em `docs/algoritmos-rag-mvp.md`: numeracao por ancoras/segmentos, chunking `semantic_block_v1`, mock de embeddings, busca vetorial, context builder, citacoes e budgets iniciais.

## O que falta definir

Estas decisoes continuam abertas, mas agora tem branch dona:

- modelo relacional, IDs e OpenAPI: `docs/modelo-dados-contratos-mvp`;
- orquestracao de ingestao, retry/cancel e fila futura: `docs/arquitetura-ingestao-rag`;
- provider real de embeddings e LLM, mantendo adapter mockado definido em `docs/algoritmos-rag-mvp`: `feat/embeddings-base` e `feat/resposta-rag-base`;
- taxonomia de analytics, retencao, export/delete e logs: `docs/analytics-observabilidade-mvp`;
- interpretacao de imagens/tabelas em PDFs, assets, elementos visuais e vision interpreter: `docs/interpretacao-imagens-tabelas-pdf`;
- comando unico de verificacao local: `chore/workspace-fundacao` e branches base de cada stack.

## Sequencia de branches

| Ordem | Branch | Arquivo |
| --- | --- | --- |
| 0 | `planejamento/documentacao` | [00-planejamento-documentacao.md](00-planejamento-documentacao.md) |
| 1 | `docs/plano-tecnico-mvp` | [01-docs-plano-tecnico-mvp.md](01-docs-plano-tecnico-mvp.md) - incorporada em `planejamento/documentacao` |
| 1.1 | `docs/modelo-dados-contratos-mvp` | [01a-docs-modelo-dados-contratos-mvp.md](01a-docs-modelo-dados-contratos-mvp.md) |
| 1.2 | `docs/arquitetura-ingestao-rag` | [01b-docs-arquitetura-ingestao-rag.md](01b-docs-arquitetura-ingestao-rag.md) |
| 1.3 | `docs/seguranca-permissoes-mvp` | [01c-docs-seguranca-permissoes-mvp.md](01c-docs-seguranca-permissoes-mvp.md) |
| 1.4 | `docs/algoritmos-rag-mvp` | [01d-docs-algoritmos-rag-mvp.md](01d-docs-algoritmos-rag-mvp.md) |
| 1.5 | `docs/analytics-observabilidade-mvp` | [01e-docs-analytics-observabilidade-mvp.md](01e-docs-analytics-observabilidade-mvp.md) |
| 1.6 | `docs/interpretacao-imagens-tabelas-pdf` | [01f-docs-interpretacao-imagens-tabelas-pdf.md](01f-docs-interpretacao-imagens-tabelas-pdf.md) |
| 2 | `chore/workspace-fundacao` | [02-chore-workspace-fundacao.md](02-chore-workspace-fundacao.md) |
| 3 | `build/dev-runtime-compose` | [03-build-dev-runtime-compose.md](03-build-dev-runtime-compose.md) |
| 4 | `chore/backend-spring-base` | [04-chore-backend-spring-base.md](04-chore-backend-spring-base.md) |
| 5 | `chore/frontend-angular-base` | [05-chore-frontend-angular-base.md](05-chore-frontend-angular-base.md) |
| 6 | `chore/worker-python-base` | [06-chore-worker-python-base.md](06-chore-worker-python-base.md) |
| 7 | `test/smoke-stack-local` | [07-test-smoke-stack-local.md](07-test-smoke-stack-local.md) |
| 8 | `feat/auth-bootstrap-workspaces` | [08-feat-auth-bootstrap-workspaces.md](08-feat-auth-bootstrap-workspaces.md) |
| 9 | `feat/workspace-dashboard-minimo` | [09-feat-workspace-dashboard-minimo.md](09-feat-workspace-dashboard-minimo.md) |
| 9.1 | `feat/workspace-settings-mvp` | [09a-feat-workspace-settings-mvp.md](09a-feat-workspace-settings-mvp.md) |
| 10 | `feat/colecoes-documentos-core` | [10-feat-colecoes-documentos-core.md](10-feat-colecoes-documentos-core.md) |
| 11 | `feat/ingestao-upload-pdf` | [11-feat-ingestao-upload-pdf.md](11-feat-ingestao-upload-pdf.md) |
| 11.1 | `feat/ingestao-runs-operacao` | [11a-feat-ingestao-runs-operacao.md](11a-feat-ingestao-runs-operacao.md) |
| 12 | `feat/worker-pdf-inspect` | [12-feat-worker-pdf-inspect.md](12-feat-worker-pdf-inspect.md) |
| 12.1 | `feat/worker-pdf-render-ocr-base` | [12a-feat-worker-pdf-render-ocr-base.md](12a-feat-worker-pdf-render-ocr-base.md) |
| 12.2 | `feat/worker-pdf-visual-tables-base` | [12b-feat-worker-pdf-visual-tables-base.md](12b-feat-worker-pdf-visual-tables-base.md) |
| 13 | `feat/paginas-numeracao` | [13-feat-paginas-numeracao.md](13-feat-paginas-numeracao.md) |
| 14 | `feat/chunking-semantico` | [14-feat-chunking-semantico.md](14-feat-chunking-semantico.md) |
| 15 | `feat/embeddings-base` | [15-feat-embeddings-base.md](15-feat-embeddings-base.md) |
| 16 | `feat/qdrant-indexacao` | [16-feat-qdrant-indexacao.md](16-feat-qdrant-indexacao.md) |
| 16.1 | `feat/ingestao-pipeline-indexacao` | [16a-feat-ingestao-pipeline-indexacao.md](16a-feat-ingestao-pipeline-indexacao.md) |
| 17 | `feat/busca-vetorial-base` | [17-feat-busca-vetorial-base.md](17-feat-busca-vetorial-base.md) |
| 17.1 | `feat/chunks-navegador` | [17a-feat-chunks-navegador.md](17a-feat-chunks-navegador.md) |
| 18 | `feat/context-builder-base` | [18-feat-context-builder-base.md](18-feat-context-builder-base.md) |
| 19 | `feat/laboratorio-recuperacao` | [19-feat-laboratorio-recuperacao.md](19-feat-laboratorio-recuperacao.md) |
| 20 | `feat/resposta-rag-base` | [20-feat-resposta-rag-base.md](20-feat-resposta-rag-base.md) |
| 20.1 | `feat/citacoes-preview-fonte` | [20a-feat-citacoes-preview-fonte.md](20a-feat-citacoes-preview-fonte.md) |
| 21 | `feat/analytics-eventos-base` | [21-feat-analytics-eventos-base.md](21-feat-analytics-eventos-base.md) |
| 22 | `feat/analytics-dashboard-base` | [22-feat-analytics-dashboard-base.md](22-feat-analytics-dashboard-base.md) |
| 23 | `feat/api-rag-publica` | [23-feat-api-rag-publica.md](23-feat-api-rag-publica.md) |
| 24 | `feat/mcp-tools-base` | [24-feat-mcp-tools-base.md](24-feat-mcp-tools-base.md) |
| 25 | `test/e2e-mvp-ingestao-recuperacao` | [25-test-e2e-mvp-ingestao-recuperacao.md](25-test-e2e-mvp-ingestao-recuperacao.md) |
| 26 | `perf/ingestao-e-contexto` | [26-perf-ingestao-e-contexto.md](26-perf-ingestao-e-contexto.md) |
| 27 | `docs/execucao-local-mvp` | [27-docs-execucao-local-mvp.md](27-docs-execucao-local-mvp.md) |

## Regra de atualizacao

Antes de abrir uma branch:

- atualizar o arquivo da branch;
- mover decisoes de "falta definir" para "definido" quando resolvidas;
- explicitar pontos de extensao, invariantes preservadas e contratos que nao podem quebrar;
- explicitar substituibilidade esperada para adapters/providers/policies e onde ocorre IoC/DI;
- registrar skills/fontes ativadas;
- atualizar registro de bordo.

Ao fechar a branch:

- registrar testes executados;
- registrar riscos remanescentes;
- registrar mudancas laterais inevitaveis e testes de regressao correspondentes;
- atualizar diario de bordo;
- abrir PR para `develop`.
