# Registro de Bordo

## Como usar

Este documento e a sessao viva do desenvolvimento. Ele deve responder rapidamente:

- em qual fase estamos;
- qual branch esta ativa;
- qual escopo esta em andamento;
- o que ja foi decidido;
- o que falta fazer;
- quais riscos ou colateralidades estao abertos;
- qual e o proximo passo concreto.

Atualizar:

- ao abrir uma branch;
- ao concluir uma etapa importante;
- antes de abrir pull request;
- depois de merge em `develop`;
- antes de uma pausa longa;
- antes de perder contexto operacional no meio de uma etapa.

## Estado atual

| Campo | Valor |
| --- | --- |
| Data do registro | 2026-06-24 |
| Fase | Concepcao em transicao para planejamento tecnico do MVP |
| Branch atual | `planejamento/documentacao` |
| Linha de integracao | `develop` |
| Objetivo atual | Consolidar documentacao inicial, formalizar camada operacional e fechar plano tecnico do MVP |
| Status | Camada operacional registrada, plano tecnico incorporado e cobertura do MVP revisada |
| Proximo marco | Fechar portoes de planejamento antes de `chore/workspace-fundacao` |

## Sessao viva

Estamos consolidando a documentacao do MVP e a camada operacional que guiara a implementacao por branches. A lacuna identificada foi que o projeto ja tinha boa documentacao de produto, arquitetura e escopo, mas ainda precisava de um rito explicito para transformar isso em desenvolvimento rastreavel: branch com escopo, PR para `develop`, registro de status e diario narrativo.

O escopo `docs/plano-tecnico-mvp`, antes planejado como proxima branch, foi absorvido nesta branch por decisao operacional. Com isso, a branch atual tambem fecha a stack baseline: Angular 22/Node 24, Spring Boot 4.1/Java 21, Python 3.13, PostgreSQL 18, Qdrant 1.18, Flyway, Compose em camadas e runtime MCP TypeScript/Node 24 em `apps/mcp`.

A revisao de cobertura do MVP mostrou que o plano precisava explicitar alguns escopos: settings do workspace, status operacional de runs, render/OCR basico, pipeline completo de ingestao/indexacao, navegador de chunks e preview de fonte a partir de citacao. Tambem foram criados portoes de planejamento para que modelo de dados, contratos, seguranca, algoritmos e analytics nao sejam decididos durante a implementacao.

Proximo passo concreto:

- revisar consistencia dos documentos vivos;
- abrir PR da branch `planejamento/documentacao` para `develop`;
- iniciar `docs/modelo-dados-contratos-mvp` como primeiro portao de planejamento;
- seguir com os demais portoes antes de `chore/workspace-fundacao`.

## Quadro de branches

| Branch | Status | Objetivo | PR | Observacoes |
| --- | --- | --- | --- | --- |
| `planejamento/documentacao` | Em andamento | Registrar proposta, MVP, ADRs, padroes iniciais e camada operacional | Pendente | Branch atual de planejamento. |
| `docs/plano-tecnico-mvp` | Incorporada na branch atual | Fechar decisoes tecnicas minimas antes da primeira branch de codigo | Nao aplicavel | Escopo executado dentro de `planejamento/documentacao`. |
| `docs/modelo-dados-contratos-mvp` | Candidata | Fechar modelo relacional, IDs, REST, Pydantic, Qdrant, eventos e OpenAPI | Pendente | Primeiro portao de planejamento. |
| `docs/arquitetura-ingestao-rag` | Candidata | Fechar state machine, pipeline, retry/cancel, idempotencia, storage e reindexacao | Pendente | Evita improviso no fluxo de ingestao. |
| `docs/seguranca-permissoes-mvp` | Candidata | Fechar auth local, roles, workspace scope, API/MCP e guardrails | Pendente | Deve anteceder `feat/auth-bootstrap-workspaces`. |
| `docs/algoritmos-rag-mvp` | Candidata | Especificar numeracao, chunking, busca, contexto, citacoes e budgets | Pendente | Deve anteceder branches de algoritmo. |
| `docs/analytics-observabilidade-mvp` | Candidata | Fechar eventos, retencao, export/delete, logs, metricas e privacidade | Pendente | Deve anteceder branches de analytics. |
| `chore/workspace-fundacao` | Candidata | Criar estrutura raiz, diretorios e convencoes do repositorio | Pendente | Depende do plano tecnico MVP. |
| `build/dev-runtime-compose` | Candidata | Criar compose local com Postgres, Qdrant e servicos preparados | Pendente | Depende da estrutura inicial. |
| `chore/backend-spring-base` | Candidata | Criar base Spring Boot testavel | Pendente | Health, profiles, testes, Postgres e baseline de migrations. |
| `chore/frontend-angular-base` | Candidata | Criar base Angular testavel | Pendente | Layout shell, roteamento, tema e testes. |
| `chore/worker-python-base` | Candidata | Criar base Python worker testavel | Pendente | FastAPI/Pydantic, health, testes e container. |
| `test/smoke-stack-local` | Candidata | Validar a stack local com testes de fumaca | Pendente | Confirma que os servicos sobem e respondem. |
| `feat/auth-bootstrap-workspaces` | Candidata | Implementar usuario local, workspace padrao e roles basicas | Pendente | Exige modelo inicial de autorizacao. |
| `feat/workspace-dashboard-minimo` | Candidata | Criar dashboard inicial com estado vazio e dados autenticados | Pendente | Primeira tela real conectada ao backend. |
| `feat/workspace-settings-mvp` | Candidata | Criar configuracoes do workspace para ingestao, analytics, acesso e API | Pendente | Fecha rota `/settings` do MVP. |
| `feat/colecoes-documentos-core` | Candidata | Criar entidades centrais de colecoes, documentos e metadados | Pendente | Exige schema relacional inicial. |
| `feat/ingestao-upload-pdf` | Candidata | Implementar upload e run inicial de ingestao de PDF | Pendente | Exige storage local e contratos de job. |
| `feat/ingestao-runs-operacao` | Candidata | Criar status, etapas, cancelamento, retry e logs de runs | Pendente | Fecha operacao visivel de ingestao. |
| `feat/worker-pdf-inspect` | Candidata | Integrar worker Python para inspecao/extracao inicial de PDF | Pendente | Exige contrato interno Spring -> worker. |
| `feat/worker-pdf-render-ocr-base` | Candidata | Adicionar render de paginas e OCR opcional basico | Pendente | Necessario para preview, mapa de paginas e PDFs sem texto nativo. |
| `feat/paginas-numeracao` | Candidata | Implementar mapa de paginas e numeracao impressa por ancoras | Pendente | Exige especificacao do algoritmo. |
| `feat/chunking-semantico` | Candidata | Implementar chunking, heading path e overlap | Pendente | Exige especificacao do algoritmo e testes. |
| `feat/embeddings-base` | Candidata | Definir contrato inicial de embeddings | Pendente | Exige modelo/provider ou adapter mockado. |
| `feat/qdrant-indexacao` | Candidata | Indexar embeddings no Qdrant com payload minimo | Pendente | Exige contrato de payload vetorial. |
| `feat/ingestao-pipeline-indexacao` | Candidata | Orquestrar ingestao completa ate chunks, embeddings e Qdrant | Pendente | Fecha o fluxo real de upload a indexado. |
| `feat/busca-vetorial-base` | Candidata | Buscar chunks por pergunta com filtros e citacoes minimas | Pendente | Exige Qdrant e embeddings. |
| `feat/chunks-navegador` | Candidata | Criar navegador de chunks com filtros, vizinhos, origem e feedback | Pendente | Fecha tela de inspecao de chunks. |
| `feat/context-builder-base` | Candidata | Montar contexto expandido por vizinhos, budget e citacoes | Pendente | Exige busca vetorial base. |
| `feat/laboratorio-recuperacao` | Candidata | Criar laboratorio com chunks, contexto e citacoes | Pendente | Exige busca e context builder inicial. |
| `feat/resposta-rag-base` | Candidata | Gerar resposta RAG com grounding quando provider estiver definido | Pendente | Pode usar adapter mockado inicialmente. |
| `feat/citacoes-preview-fonte` | Candidata | Abrir fonte original de citacoes com preview e fallback textual | Pendente | Fecha criterio de validacao de evidencia. |
| `feat/analytics-eventos-base` | Candidata | Registrar eventos locais versionados | Pendente | Exige taxonomia de eventos. |
| `feat/analytics-dashboard-base` | Candidata | Criar dashboard local minimo de qualidade e falhas | Pendente | Exige eventos base. |
| `feat/api-rag-publica` | Candidata | Expor API HTTP de consulta RAG | Pendente | Exige permissoes e limites. |
| `feat/mcp-tools-base` | Candidata | Expor primeiras ferramentas MCP controladas | Pendente | Exige schemas, permissoes e analytics. |
| `test/e2e-mvp-ingestao-recuperacao` | Candidata | Criar e2e do fluxo principal do MVP | Pendente | Exige compose e2e e fixtures pequenas. |
| `perf/ingestao-e-contexto` | Candidata | Medir e otimizar gargalos reais | Pendente | Exige fluxo implementado para medir. |
| `docs/execucao-local-mvp` | Candidata | Documentar execucao local do MVP | Pendente | Exige comandos reais validados. |

## Pendencias vivas

| Tema | Status | Proxima acao |
| --- | --- | --- |
| Estrutura de repositorio/pacotes | Definida inicialmente | Criar `apps/api`, `apps/web`, `apps/worker`, `apps/mcp`, `infra/compose`, `tests`, `scripts` em `chore/workspace-fundacao`. |
| Migracoes de banco | Definida inicialmente | Usar Flyway e aplicar convencoes na primeira branch backend. |
| Modelo documental inicial | Com branch dona | Resolver em `docs/modelo-dados-contratos-mvp`. |
| Contrato Spring -> Python worker | Com branch dona | Resolver em `docs/modelo-dados-contratos-mvp` e detalhar pipeline em `docs/arquitetura-ingestao-rag`. |
| Contrato Qdrant | Com branch dona | Resolver payload/filtros em `docs/modelo-dados-contratos-mvp` e reindexacao em `docs/arquitetura-ingestao-rag`. Imagem inicial: `qdrant/qdrant:v1.18.2`. |
| Estrategia de testes | Registrada inicialmente | Implementar base em `chore/workspace-fundacao`, Compose em `build/dev-runtime-compose` e smoke em `test/smoke-stack-local`. |
| Algoritmos iniciais | Com branch dona | Especificar em `docs/algoritmos-rag-mvp` antes das branches de algoritmo. |
| Colateralidades por branch | Pendente | Aplicar checklist operacional antes de cada branch. |
| Skills e fontes oficiais | Registrado inicialmente | Aplicar politica na proxima branch tecnica e registrar fontes ativadas. |
| Plano de branches | Registrado inicialmente | Validar sequencia, ajustar granularidade e usar como base do proximo PR. |
| Branches plan | Registrado inicialmente | Usar `branches-plan` como fonte operacional por branch antes de iniciar implementacao. |
| Stack baseline | Definida inicialmente | Confirmar patches/lockfiles nas branches que criarem apps reais. |
| MCP runtime | Definido inicialmente | Criar `apps/mcp` em fundacao e implementar tools em `feat/mcp-tools-base`. |

## Modelo de checkpoint

Usar este modelo quando uma etapa for concluida ou interrompida:

```text
Data:
Branch:
Status:
Objetivo da etapa:
Skills/fontes ativadas:
O que foi feito:
Arquivos tocados:
Decisoes tomadas:
Riscos/colateralidades:
Testes/verificacoes:
Pendencias:
Proximo passo:
```
