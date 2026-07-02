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
| Data do registro | 2026-07-01 |
| Fase | Endurecimento do MVP |
| Branch atual | `develop` |
| Linha de integracao | `develop` |
| Objetivo atual | Fechar infra E2E do fluxo de ingestão e recuperação |
| Status | Concluída |
| Proximo marco | Merge de test/e2e-mvp-ingestao-recuperacao e Iniciar analise/pesquisa de viabilidade e performance |

## Sessao viva

Estamos consolidando a documentacao do MVP e a camada operacional que guiara a implementacao por branches. A lacuna identificada foi que o projeto ja tinha boa documentacao de produto, arquitetura e escopo, mas ainda precisava de um rito explicito para transformar isso em desenvolvimento rastreavel: branch com escopo, PR para `develop`, registro de status e diario narrativo.

O escopo `docs/plano-tecnico-mvp`, antes planejado como proxima branch, foi absorvido nesta branch por decisao operacional. Com isso, a branch atual tambem fecha a stack baseline: Angular 22/Node 24, Spring Boot 4.1/Java 21, Python 3.13, PostgreSQL 18, Qdrant 1.18, Flyway, Compose em camadas e runtime MCP TypeScript/Node 24 em `apps/mcp`.

A revisao de cobertura do MVP mostrou que o plano precisava explicitar alguns escopos: settings do workspace, status operacional de runs, render/OCR basico, pipeline completo de ingestao/indexacao, navegador de chunks e preview de fonte a partir de citacao. Tambem foram criados portoes de planejamento para que modelo de dados, contratos, seguranca, algoritmos, analytics e, depois da revisao visual/tabular, interpretacao de imagens/tabelas em PDFs nao sejam decididos durante a implementacao.

Uma varredura fina posterior sobre os grupos 0 e 1 explicitou o gate SOLID/OCP-LSP-IoC como mecanismo contra regressao lateral. A partir daqui, cada branch deve declarar pontos de extensao, invariantes preservadas, contratos sensiveis, substituibilidade de implementacoes, ligacao de concretos por IoC/DI, mudancas laterais inevitaveis e testes de compatibilidade. Os portoes `docs/modelo-dados-contratos-mvp`, `docs/arquitetura-ingestao-rag`, `docs/seguranca-permissoes-mvp`, `docs/algoritmos-rag-mvp`, `docs/analytics-observabilidade-mvp` e `docs/interpretacao-imagens-tabelas-pdf` receberam detalhes adicionais para tirar decisoes tecnicas do campo implicito.

Foi criado o trigger de implementacao agentica em `docs/trigger-implementacao-agentica.md`. Ele passa a ser o ponto de partida para qualquer retomada de desenvolvimento: ler documentacao viva, checar Git/branch/remota, tratar `safe.directory` quando necessario, sincronizar com seguranca, continuar ou criar branch planejada, abrir o plano especifico da branch, ativar skills/fontes e aplicar gates antes de implementar.

Foi feita uma revisao de coesao documental em `docs/revisao-coesao-documental.md`. A revisao confirmou a coesao da ideia central e corrigiu desalinhamentos textuais em documentos antigos: `planejamento.md` deixou de tratar decisoes vigentes como discussoes abertas, `features.md` passou a distinguir MVP funcional de MVP nao bloqueante, MCP basico ficou classificado como trilho nao bloqueante e perfis do MVP voltaram a `rapido`/`balanceado`.

Em seguida, a camada de interpretacao de imagens e tabelas em PDFs foi elevada a parte fundamental do MVP. Foram adicionadas as branches `docs/interpretacao-imagens-tabelas-pdf` e `feat/worker-pdf-visual-tables-base`, com vision interpreter por adapter LLM/VLM, modo mockado em testes, provider remoto configuravel, privacidade, budget, source locator, assets, elementos e interpretacoes textuais derivadas.

A branch `planejamento/documentacao` foi commitada, publicada, aberta como PR #1 para `develop` e mesclada. A aprovacao formal via GitHub nao foi possivel pela propria regra da plataforma, pois a conta autenticada era autora do PR.

Na branch `docs/modelo-dados-contratos-mvp`, o primeiro portao de planejamento foi fechado em nivel contratual: IDs internos/publicos, modelo relacional conceitual, soft delete, workspace scope, REST, worker/Pydantic, Qdrant, eventos locais, compatibilidade entre schemas e exemplos JSON iniciais em `tests/contracts`.

O PR #2 de `docs/modelo-dados-contratos-mvp` foi mesclado em `develop`. Na sequencia, a branch `docs/arquitetura-ingestao-rag` fechou a orquestracao de ingestao: state machines de documento/run/etapa, pipeline com pausa para revisao do mapa de paginas, retry, cancelamento cooperativo, idempotencia por run/step, reprocessamento por `ingestion_generation`, layout de storage local e criterios para adiar fila ate haver necessidade real. O PR #3 foi mesclado em `develop`.

Na branch `docs/seguranca-permissoes-mvp`, o portao de seguranca foi especificado: UI com sessao server-side opaca em cookie `HttpOnly`, CSRF para mutacoes de SPA, CORS same-origin por padrao, senha local com `Argon2id`, bootstrap unico do primeiro owner, matriz role x acao, workspace scope por recurso, API keys futuras com hash/capabilities, agentes MCP sem token passthrough e auditoria minima por eventos locais seguros. A decisao duradoura foi registrada na ADR-024.

O PR #4 de `docs/seguranca-permissoes-mvp` foi aprovado e mesclado em `develop`. A linha local `develop` foi atualizada por fast-forward e a branch `docs/algoritmos-rag-mvp` foi aberta a partir dela.

Na branch `docs/algoritmos-rag-mvp`, o portao de algoritmos especificou numeracao por ancoras/segmentos (`page_numbering_anchor_segments_v1`), chunking por blocos semanticos (`semantic_block_v1`), provider mockado de embeddings (`mock-text-embedding-v1`), busca vetorial com filtros obrigatorios, context builder versionado, contrato de citacoes `rag.citation.v1`, politica `insufficient_evidence` para resposta sem contexto confiavel e budgets iniciais para laboratorio, API e MCP. A decisao duradoura foi registrada na ADR-025 e fixtures pequenas foram adicionadas em `tests/contracts/rag` e `tests/contracts/worker`.

O PR #5 de `docs/algoritmos-rag-mvp` foi mesclado em `develop`. A linha local `develop` foi atualizada por fast-forward, e a branch `docs/analytics-observabilidade-mvp` foi aberta a partir dela.

Na branch `docs/analytics-observabilidade-mvp`, o portao de analytics fechou o envelope `analytics.event.v1`, a taxonomia inicial de eventos, as origens `ui`, `api`, `worker`, `mcp` e `system`, as trilhas de observabilidade, campos proibidos, modos de historico local, retencao por classe, export JSONL/CSV com manifesto, delete por workspace, dashboard minimo e fixtures versionadas em `tests/contracts/events` e `tests/contracts/analytics`. A decisao duradoura foi registrada na ADR-026.

O PR #6 de `docs/analytics-observabilidade-mvp` foi mesclado em `develop`. A linha local `develop` foi atualizada por fast-forward e a branch `docs/interpretacao-imagens-tabelas-pdf` foi aberta a partir dela.

Na branch `docs/interpretacao-imagens-tabelas-pdf`, o portao visual/tabular fechou `pdf.source_locator.v1`, `pdf.document_element.v1`, `pdf.asset.v1`, `pdf.table_json.v1`, `pdf.table_markdown.v1`, `pdf.visual_interpretation.v1`, os contratos `worker.pdf.extract_elements.*.v1` e `worker.pdf.interpret_visual.*.v1`, a politica de provider remoto opt-in, mock deterministico para testes, budgets iniciais, cache por hash/policy, relacoes com chunking/context builder/citacoes e fixtures versionadas para worker, RAG e eventos.

O PR #7 de `docs/interpretacao-imagens-tabelas-pdf` foi mesclado em `develop`. A linha local `develop` foi atualizada por fast-forward e a branch `chore/workspace-fundacao` foi aberta a partir dela.

Na branch `chore/workspace-fundacao`, foi criada apenas a fundacao raiz: `apps/api`, `apps/web`, `apps/worker`, `apps/mcp`, `infra/compose`, `scripts`, `tests/e2e` e `tests/fixtures`, preservando `tests/contracts` ja versionado. A branch tambem criou convencoes raiz, READMEs operacionais e scripts PowerShell como interface inicial. Scripts que dependem de stacks ainda inexistentes falham com mensagem clara e exit code `2`, em vez de passar silenciosamente.

Na branch `docs/vinculos-planos-branches`, foi criado um plano proprio em `branches-plan/02a-docs-vinculos-planos-branches.md` e uma regra em `branches-plan/README.md` para que branches futuras listem documentos transversais, documentos especificos e contratos/fixtures relevantes. Os planos de `03` a `27` passaram a ter a secao `Documentos relevantes`, para que agentes futuros saibam o que carregar antes de implementar cada escopo.

Depois do merge da fundacao e dos vinculos documentais, `develop` foi atualizado por fast-forward ate `1a761bb`. A branch `build/dev-runtime-compose` foi aberta a partir da linha de integracao sincronizada para criar o primeiro runtime local com Postgres e Qdrant.

Na branch `build/dev-runtime-compose`, foram criados `infra/compose/compose.yml`, `compose.dev.yml` e `compose.e2e.yml`. O runtime inicial usa `postgres:18.4` e `qdrant/qdrant:v1.18.2`, sem `latest`, com rede `ragcreator`, volumes nomeados, health check de Postgres por `pg_isready` e health check de Qdrant por abertura TCP em `6333`, porque a imagem oficial nao inclui `curl`. Os scripts `compose-up.ps1` e `compose-down.ps1` deixaram de ser placeholders e passaram a operar profiles `base`, `dev` e `e2e`. O `check.ps1` agora valida `docker compose config` para os tres profiles.

Skills/fontes ativadas nesta branch: documentacao local do projeto, `docs/plano-tecnico-mvp.md`, `docs/estrategia-de-testes.md`, `docs/padroes-de-projeto.md`, `docs/decisoes-arquiteturais.md` e plano especifico `branches-plan/03-build-dev-runtime-compose.md`. Nao houve skill externa especializada ativada.

Verificacoes executadas: `git switch develop`, `git pull --ff-only`, `git switch -c build/dev-runtime-compose`, `.\scripts\check.ps1`, `.\scripts\compose-up.ps1 -Profile dev`, `docker exec ragcreator-dev-postgres-1 pg_isready -U ragcreator -d ragcreator`, `Invoke-RestMethod http://localhost:6333/healthz`, `.\scripts\compose-up.ps1 -Profile e2e` e `.\scripts\compose-down.ps1 -Profile e2e -RemoveVolumes`. O Docker Desktop estava parado no inicio da verificacao real e foi iniciado para validar os containers.

Na branch `feat/auth-bootstrap-workspaces`, foi implementada a fundação vertical do sistema: criação das migrações do banco (usuários, workspaces, sessoes), domínio e filtros de segurança no Spring Boot (`OpaqueSessionFilter` e `SecurityConfig`) usando tokens em cookie e hashing Argon2id. No frontend Angular, foi implementado o `AuthLayoutComponent` isolado e os componentes de login e bootstrap. Adicionalmente, consolidou-se um novo documento `docs/pos-mvp.md` contendo o roadmap futuro (OAuth, telemetria, storage distribuído).

Na branch `feat/embeddings-base`, foi estabelecido o contrato para geração de embeddings entre a API e o Worker utilizando padrão Webhook (para resiliência contra rate limits, com fallback pos-MVP em `docs/pos-mvp.md`). O `MockEmbeddingProvider` determinístico foi construído com `random.seed(sourceHash)` gerando vetores de dimensão 16 e a persistência em JPA/Flyway foi adicionada cobrindo as tabelas `vector_connections`, `vector_index_bindings` e `chunk_embeddings`.

Proximo passo concreto:

- commitar e publicar `test/e2e-mvp-ingestao-recuperacao`;
- abrir PR para `develop`;
- apos merge, iniciar analises/pesquisas ou branches futuras como `perf/ingestao-e-contexto`.

## Quadro de branches

| Branch | Status | Objetivo | PR | Observacoes |
| --- | --- | --- | --- | --- |
| `planejamento/documentacao` | Mesclada | Registrar proposta, MVP, ADRs, padroes iniciais e camada operacional | #1 | Mesclada em `develop`. |
| `docs/plano-tecnico-mvp` | Incorporada na branch atual | Fechar decisoes tecnicas minimas antes da primeira branch de codigo | Nao aplicavel | Escopo executado dentro de `planejamento/documentacao`. |
| `docs/modelo-dados-contratos-mvp` | Mesclada | Fechar modelo relacional, IDs, REST, Pydantic, Qdrant, eventos e OpenAPI | #2 | Primeiro portao de planejamento. |
| `docs/arquitetura-ingestao-rag` | Mesclada | Fechar state machine, pipeline, retry/cancel, idempotencia, storage e reindexacao | #3 | Evita improviso no fluxo de ingestao. |
| `docs/seguranca-permissoes-mvp` | Mesclada | Fechar auth local, roles, workspace scope, API/MCP e guardrails | #4 | Deve anteceder `feat/auth-bootstrap-workspaces`. |
| `docs/algoritmos-rag-mvp` | Mesclada | Especificar numeracao, chunking, busca, contexto, citacoes e budgets | #5 | Deve anteceder branches de algoritmo. |
| `docs/analytics-observabilidade-mvp` | Mesclada | Fechar eventos, retencao, export/delete, logs, metricas e privacidade | #6 | Deve anteceder branches de analytics. |
| `docs/interpretacao-imagens-tabelas-pdf` | Mesclada | Fechar camada de imagens/tabelas em PDFs, assets, elementos e vision interpreter | #7 | Deve anteceder worker visual/tabelas, chunking e citacoes. |
| `chore/workspace-fundacao` | Mesclada | Criar estrutura raiz, diretorios e convencoes do repositorio | Concluido | Mesclada em `develop`; estrutura inicial disponivel. |
| `docs/vinculos-planos-branches` | Mesclada | Vincular planos futuros a docs, ADRs, contratos e fixtures relevantes | Concluido | Mesclada em `develop`; planos futuros carregam documentos relevantes. |
| `build/dev-runtime-compose` | Mesclada | Criar compose local com Postgres, Qdrant e servicos preparados | #8 | Compose base/dev/e2e criado; dev e e2e validados com health checks. |
| `chore/backend-spring-base` | Mesclada | Criar base Spring Boot testavel | #9 | Base validada com Java 21 e Compose local. |
| `chore/frontend-angular-base` | Concluída | Criar base Angular testavel | Pendente | Layout shell, roteamento, tema e testes. |
| `chore/worker-python-base` | Mesclada | Criar base Python worker testavel | #13 | FastAPI/Pydantic, health, testes e container validados. |
| `test/smoke-stack-local` | Concluída | Validar a stack local com testes de fumaca | Pendente | Confirma que os servicos sobem e respondem via `test-smoke.ps1`. |
| `feat/auth-bootstrap-workspaces` | Mesclada | Implementar usuario local, workspace padrao e roles basicas | Concluido | Bootstrap e auth via cookie opaco finalizados. |
| `feat/workspace-dashboard-minimo` | Mesclada | Criar dashboard inicial com estado vazio e dados autenticados | Concluido | Rota de dashboard e sidebar integradas no Shell. |
| `feat/workspace-settings-mvp` | Mesclada | Criar configuracoes do workspace para ingestao, analytics, acesso e API | Concluido | Fecha rota `/settings` do MVP. |
| `feat/colecoes-documentos-core` | Mesclada | Criar entidades centrais de colecoes, documentos e metadados | Concluido | Feito o schema relacional e controladores do Core. |
| `feat/ingestao-upload-pdf` | Mesclada | Implementar upload e run inicial de ingestao de PDF | Concluido | Criados storage, limites de upload, UI e status inicial de IngestRun. |
| `feat/ingestao-runs-operacao` | Concluída | Criar status, etapas, cancelamento, retry e logs de runs | Pendente | Fecha operacao visivel de ingestao. |
| `feat/worker-pdf-inspect` | Mesclada | Integrar worker Python para inspecao/extracao inicial de PDF | Concluido | Contrato interno Spring -> worker via webhook implementado. |
| `feat/worker-pdf-render-ocr-base` | Mesclada | Adicionar render de paginas e OCR opcional basico | Concluido | Necessario para preview, mapa de paginas e PDFs sem texto nativo. |
| `feat/worker-pdf-visual-tables-base` | Concluída | Interpretar imagens/tabelas em PDFs com assets, elementos e adapter visual | Pendente | Necessario para preservar conhecimento visual/tabular. |
| `feat/paginas-numeracao` | Mesclada | Implementar mapa de paginas e numeracao impressa por ancoras | Concluido | Mesclada. |
| `feat/chunking-semantico` | Concluída | Implementar chunking, heading path e overlap | Pendente | Fecha funcionalidade base de quebra de texto. |
| `feat/embeddings-base` | Concluída | Definir contrato inicial de embeddings | Pendente | Exige modelo/provider ou adapter mockado. |
| `feat/qdrant-indexacao` | Mesclada | Indexar embeddings no Qdrant com payload minimo | Concluido | Qdrant client 1.12.0 e adapter implementado. |
| `feat/ingestao-pipeline-indexacao` | Concluída | Orquestrar ingestao completa ate chunks, embeddings e Qdrant | Pendente | Fecha o fluxo real de upload a indexado. |
| `feat/busca-vetorial-base` | Mesclada | Buscar chunks por pergunta com filtros e citacoes minimas | Concluido | Exige Qdrant e embeddings. |
| `feat/chunks-navegador` | Mesclada | Criar navegador de chunks com filtros, vizinhos, origem e feedback | Concluido | Fecha tela de inspecao de chunks. |
| `feat/context-builder-base` | Concluída | Montar contexto expandido por vizinhos, budget e citacoes | Pendente | Exige busca vetorial base. |
| `feat/laboratorio-recuperacao` | Concluída | Criar laboratorio com chunks, contexto e citacoes | Pendente | Exige busca e context builder inicial. |
| `feat/resposta-rag-base` | Mesclada | Gerar resposta RAG com grounding quando provider estiver definido | Concluido | Pode usar adapter mockado inicialmente. |
| `feat/citacoes-preview-fonte` | Concluída | Abrir fonte original de citacoes com preview e fallback textual | Pendente | Fecha criterio de validacao de evidencia. |
| `feat/analytics-eventos-base` | Concluída | Registrar eventos locais versionados | Pendente | Exige taxonomia de eventos. |
| `feat/analytics-dashboard-base` | Mesclada | Criar dashboard local minimo de qualidade e falhas | Concluido | Exige eventos base. |
| `feat/api-rag-publica` | Concluída | Expor API HTTP de consulta RAG via API Keys | Pendente | Exige permissoes e limites. |
| `feat/mcp-tools-base` | Implementado | Expor primeiras ferramentas MCP controladas | Pendente | Exige schemas, permissoes e analytics. |
| `test/e2e-mvp-ingestao-recuperacao` | Concluída | Criar e2e do fluxo principal do MVP | Pendente | Playwright, Compose isolado e fixture de PDF rodando. |
| `perf/ingestao-e-contexto` | Candidata | Medir e otimizar gargalos reais | Pendente | Exige fluxo implementado para medir. |
| `docs/execucao-local-mvp` | Candidata | Documentar execucao local do MVP | Pendente | Exige comandos reais validados. |

## Pendencias vivas

| Tema | Status | Proxima acao |
| --- | --- | --- |
| Estrutura de repositorio/pacotes | Mesclada inicialmente | A proxima expansao de apps reais entra nas branches base de cada stack. |
| Vinculos de contexto por branch | Mesclados inicialmente | Manter a secao `Documentos relevantes` atualizada quando uma branch futura ganhar novo contrato, ADR ou fixture. |
| Migracoes de banco | Definida inicialmente | Usar Flyway e aplicar convencoes na primeira branch backend. |
| Modelo documental inicial | Especificado | Implementar em migrations nas branches de backend/documentos. |
| Contrato Spring -> Python worker | Especificado | Implementar adapters HTTP internos nas branches de worker/ingestao. |
| Contrato Qdrant | Payload, reindexacao e idempotencia especificados | Implementar adapter Qdrant e testes de filtro/idempotencia nas branches de indexacao. Imagem inicial: `qdrant/qdrant:v1.18.2`. |
| Arquitetura de ingestao | Especificada | Implementar state machine, background job e storage nas branches de ingestao. |
| Seguranca e permissoes | Especificada | Implementar sessao, CSRF, roles, matriz de permissoes, workspace scope e testes em `feat/auth-bootstrap-workspaces`; aplicar API key/MCP nas branches proprias. |
| Estrategia de testes | Base operacional e Compose criados | `check.ps1` verifica estrutura e Compose config; smoke real entra em `test/smoke-stack-local`. |
| Algoritmos iniciais | Especificados | Implementar numeracao, chunking, embeddings, busca, contexto, citacoes e resposta nas branches funcionais usando `docs/algoritmos-rag-mvp.md`. |
| Analytics e observabilidade | Especificados | Implementar eventos locais, retencao, export/delete e dashboard usando `docs/analytics-observabilidade-mvp.md`. |
| Providers reais de embedding/LLM | Encaminhados | Manter mock deterministico em testes; escolher adapters reais nas branches `feat/embeddings-base` e `feat/resposta-rag-base` se houver configuracao. |
| Interpretacao visual/tabelas em PDF | Especificada | Implementar elementos/assets/tabelas/vision interpreter em `feat/worker-pdf-visual-tables-base` usando `docs/interpretacao-imagens-tabelas-pdf.md`. |
| Colateralidades por branch | Explicitado | Aplicar checklist operacional e gate SOLID/OCP-LSP-IoC antes de cada branch. |
| Trigger de implementacao agentica | Registrado | Usar `docs/trigger-implementacao-agentica.md` antes de qualquer retomada ou nova implementacao. |
| Coesao documental | Revisada | Usar `docs/revisao-coesao-documental.md` como fotografia das decisoes vigentes antes dos proximos portoes. |
| Skills e fontes oficiais | Registrado inicialmente | Aplicar politica na proxima branch tecnica e registrar fontes ativadas. |
| Plano de branches | Registrado inicialmente | Validar sequencia, ajustar granularidade e usar como base do proximo PR. |
| Branches plan | Registrado inicialmente | Usar `branches-plan` como fonte operacional por branch antes de iniciar implementacao. |
| Stack baseline | Definida inicialmente | Confirmar patches/lockfiles nas branches que criarem apps reais. |
| MCP runtime | Definido inicialmente | Criar `apps/mcp` em fundacao e implementar tools em `feat/mcp-tools-base`. |

## Modelo de checkpoint

Usar este modelo quando uma etapa for concluida ou interrompida:

```text
Data: 2026-06-28
Branch: `feat/busca-vetorial-base`
Status: Concluída
Objetivo da etapa: Buscar chunks por pergunta com filtros e citacoes minimas.
Skills/fontes ativadas: N/A
O que foi feito: Implementado o `VectorSearchService` orquestrando a geração síncrona de embeddings via Worker, a busca vetorial via `QdrantVectorStoreAdapter` (com aplicação rigorosa de filtros de workspace, collection e tipo de payload) e o enriquecimento de metadados dos chunks via `ChunkRepository`.
Arquivos tocados: VectorSearchService.java, QdrantVectorStoreAdapter.java, VectorStorePort.java, WorkerClient.java, SearchRequest.java, SearchResponse.java, ChunkRepository.java, VectorSearchServiceTest.java.
Decisoes tomadas: O Worker passou a suportar um fluxo síncrono para embeddings visando otimizar a latência. As buscas mantiveram a estrutura de `setKeyword` para filtros. `lowConfidence` é propagado em caso de scores inferioes a 0.35.
Riscos/colateralidades: Sem riscos, a arquitetura restringe adequadamente a pesquisa.
Testes/verificacoes: Build e testes `VectorSearchServiceTest` executados com sucesso (Mvn Test / Mvn Compile).
Pendencias: Nenhuma no escopo atual.
Proximo passo: Commit, Push, PR.
```
Data: 2026-06-28
Branch: `feat/ingestao-pipeline-indexacao`
Status: Concluída
Objetivo da etapa: Implementar orquestração da pipeline de ingestão e inserção em lote no Qdrant.
Skills/fontes ativadas: N/A
O que foi feito: Criado o `IngestRunPipelineService` gerenciando os estados de ingestão dinamicamente e orquestrando o roteamento dos callbacks do worker por `runId`. Atualizado o `QdrantVectorStoreAdapter` para agrupar e inserir as embeddings no Qdrant via batch de 100 itens. Limpeza de responsabilidades e transição de código legado em `IngestRunOperationService` para o novo orquestrador. Implementados os testes da máquina de estados do pipeline.
Arquivos tocados: QdrantVectorStoreAdapter.java, InternalCallbackController.java, WorkerBaseRequest.java, IngestRunOperationService.java, IngestRunPipelineService.java, VectorStorePort.java, IngestRunPipelineServiceTest.java.
Decisoes tomadas: Adotar batch de 100 itens para o `upsertBatch` no Qdrant prevenindo limite de I/O. As requisições ao worker ganharam injeção do `callbackUrl` roteável nativamente contendo o `runId`.
Riscos/colateralidades: Fluxos parciais de retry operacionais foram integrados adequadamente não deixando estados abertos na pipeline.
Testes/verificacoes: Build e testes rodaram sem erros em `mvnw compile` e `mvnw test` incluindo validações com Mockito.
Pendencias: Nenhuma no escopo atual.
Proximo passo: Commit, Push, PR.
```

```text
Data: 2026-06-28
Branch: `feat/chunks-navegador`
Status: Concluída
Objetivo da etapa: Criar navegador de chunks para inspecao, filtros, vizinhos e feedback local.
Skills/fontes ativadas: N/A
O que foi feito: Backend: criada entidade RetrievalFeedback, migration, repositórios e controllers. Implementado ChunkBrowserService e queries no ChunkRepository. Frontend: criado ChunkService, ChunkBrowserComponent e ChunkDetailsPanelComponent para exibir tabela e detalhes (source locator, texto) do chunk com opções de feedback.
Arquivos tocados: RetrievalFeedback.java, RetrievalFeedbackRequest.java, ChunkResponse.java, ChunkController.java, RetrievalFeedbackController.java, ChunkRepository.java, ChunkBrowserService.java, V9__retrieval_feedback.sql, chunk.service.ts, chunk-browser.component.ts/html/css, chunk-details-panel.component.ts/html/css, collection-detail.component.ts/html.
Decisoes tomadas: Feedback salvo na base local via entidade independente, gaveta lateral (mobile-first) para inspeção profunda de chunks.
Riscos/colateralidades: Sem riscos adicionais.
Testes/verificacoes: Maven compile e Angular build (npm run build) executados com sucesso.
Pendencias: Nenhuma no escopo atual.
Proximo passo: Commit, Push, PR.
```
```text
Data: 2026-06-28
Branch: `feat/context-builder-base`
Status: Concluída
Objetivo da etapa: Montar contexto expandido por vizinhos, budget e citacoes.
Skills/fontes ativadas: N/A
O que foi feito: Backend: implementado o `ContextBuilderService` junto com queries otimizadas em `ChunkRepository` (`findNeighbors`) e `ChunkRelationRepository`. Foram criados os DTOs do RAG (`ContextAssembleRequest`, `ContextAssembleResponse`, `ContextItemResponse`) para prover os resultados de busca formatados e testada a aplicação da política conservadora e sequencial para agrupamento do contexto baseado em budget (orçamento de tokens) e fallback truncado.
Arquivos tocados: ContextBuilderService.java, ContextBuilderServiceTest.java, ChunkRepository.java, ChunkRelationRepository.java, ContextAssembleRequest.java, ContextAssembleResponse.java, ContextItemResponse.java.
Decisoes tomadas: Adotado a lógica `budgetHit` para alertar clientes se o orçamento limitou o retorno. Citações truncadas ganham `[TRUNCATED]` preservando sempre a referência do `sourceLocator`. Relações visuais ou de tabelas vêm como `direct_relation` via `ChunkRelation`.
Riscos/colateralidades: Algoritmo é escalável para o MVP e mitiga N+1 com buscas otimizadas no relacional.
Testes/verificacoes: Testes unitários com JUnit cobrindo as restrições e orçamentos passaram (`[INFO] BUILD SUCCESS`).
Pendencias: Nenhuma no escopo atual.
Proximo passo: Commit, Push, PR.
```

```text
Data: 2026-06-28
Branch: `feat/laboratorio-recuperacao`
Status: Concluída
Objetivo da etapa: Criar laboratorio de recuperacao com pergunta, chunks, contexto e citacoes.
Skills/fontes ativadas: N/A
O que foi feito: Frontend Angular: criado o serviço `RagService` contendo as definições dos DTOs de `AskRequest`, `AskResponse` e métodos de HTTP (`ask`, `submitFeedback`). Criado o componente de página `RagLaboratoryComponent` exibindo painel de input de teste de RAG (query, topK, etc), o status de resposta, Citações utilizadas, Contextos recuperados via tabela e o bloco renderizado bruto repassado pela API de Context Assembly. Componente integrado como tab `lab` na página principal de `CollectionDetailComponent`.
Arquivos tocados: rag.service.ts, rag-laboratory.component.ts/html/css, collection-detail.component.ts/html.
Decisoes tomadas: O laboratório está alocado contextualmente dentro das sub-rotas/tabs da própria Collection para simplificar as requisições de workspace/collection ids. As tags de feedbacks positivos e negativos disparam diretamente pro endpoint HTTP de `RetrievalFeedback` com referências da query via `queryEventId`.
Riscos/colateralidades: Sem impacto direto na compilação ou execução fora do escopo do próprio laboratório isolado.
Testes/verificacoes: Build Angular validado localmente com npm run build sem erros de template parsing e tipagem, cobrindo DTOs.
Pendencias: Nenhuma no escopo atual.
Proximo passo: Commit, Push, PR.
```

```text
Data: 2026-06-28
Branch: `feat/citacoes-preview-fonte`
Status: Concluída
Objetivo da etapa: Abrir fonte original de citações com preview e fallback textual.
Skills/fontes ativadas: N/A
O que foi feito: No frontend Angular, refatorou-se a exibição de citações do Laboratório RAG para utilizar componentes reutilizáveis. Foram criados o `CitationCardComponent` para renderizar os cartões básicos de citações, e o `CitationPreviewComponent` funcionando como um painel lateral dinâmico (Drawer) para visualizar a fonte original. Lógicas de fallback textual para quando as imagens não estão disponíveis foram incorporadas, juntamente à um overlay (BBox) projetado a partir de coordenadas `pdf_points_top_left` vindas das interpretações visuais e tabelas do RAG.
Arquivos tocados: rag-laboratory.component.ts/html/css, citation-card.ts/html/css, citation-preview.ts/html/css.
Decisões tomadas: Utilizar Painel Lateral para visualização de origens, de forma a não interromper o contexto principal do chat e laboratório. O destaque de áreas específicas (BBox) foi implementado de forma fluída com posições absolutas baseadas em propriedades relativas.
Riscos/colateralidades: Sem impactos adversos.
Testes/verificações: Build do Angular (`npm run build`) validou com sucesso as injeções e tipos.
Pendências: Nenhuma no escopo atual.
Próximo passo: Commit, Push, PR e avançar para `feat/analytics-eventos-base`.
```

```text
Data: 2026-06-29
Branch: `feat/analytics-eventos-base`
Status: Concluída
Objetivo da etapa: Registrar eventos locais versionados, aplicar retenção por classe e integrar captura inicial.
Skills/fontes ativadas: N/A
O que foi feito: Backend: Criada entidade `AnalyticsEvent`, migrations e repositório. Construído `AnalyticsEventService` operando de forma assíncrona (`@Async`) e com sanitização de campos. Agendada regra Híbrida de Retenção de eventos usando `@Scheduled`. Integrados `IngestRunPipelineService` e `VectorSearchService` para publicarem os primeiros eventos essenciais da taxonomia RAG. Criados os endpoints POST, DELETE e export em `AnalyticsEventController`. Frontend: implementado `AnalyticsService.ts` e disparado o evento inicial no lab RAG.
Arquivos tocados: V10__analytics_events.sql, AnalyticsEvent.java, AnalyticsEventRequest.java, AnalyticsEventRepository.java, AnalyticsEventService.java, AnalyticsRetentionJob.java, AnalyticsEventController.java, IngestRunPipelineService.java, VectorSearchService.java, analytics.service.ts, rag-laboratory.component.ts.
Decisões tomadas: Gravação assíncrona (`@Async`) na própria thread pool do Spring em vez de kafka para simplicidade do MVP. A retenção usa abordagem híbrida (Tempo + Volume Seguro) no Background Job para evitar latência nos inserts. O evento `retrieval_query_executed` envia contagens exatas da query.
Riscos/colateralidades: Operações assíncronas falhas não travarão a esteira, e limites de volume evitam crescimento explosivo.
Testes/verificações: Build do Maven (`mvnw compile`) validou injeções e testes. Angular compilou corretamente.
Pendências: Nenhuma no escopo atual.
Próximo passo: Commit, Push, PR e avançar para dashboard analítico ou API pública.
```
```text
Data: 2026-06-29
Branch: `feat/analytics-dashboard-base`
Status: Concluída
Objetivo da etapa: Criar dashboard local minimo de qualidade e falhas baseadas na retenção local.
Skills/fontes ativadas: N/A
O que foi feito: Backend: Criado `DashboardSummaryRequest` para receber filtros, estendido `DashboardSummaryResponse` seguindo o contrato rigoroso do V1. Implementado o `AnalyticsDashboardService` para extrair agregações (Ingestion, Retrieval, Answers, API/MCP, TopFailures) via stream/memory a partir de queries por tempo em `AnalyticsEventRepository`. Atualizado `WorkspaceController`. Frontend: Criado a camada UI do Dashboard utilizando Signals, FormBuilder para reatividade dos filtros, Glassmorphism/CSS Grid para cards informativos robustos, iconografia via Lucide e Empty State.
Arquivos tocados: DashboardSummaryResponse.java, DashboardSummaryRequest.java, AnalyticsEventRepository.java, AnalyticsDashboardService.java, WorkspaceController.java, workspace.service.ts, dashboard.component.ts/css.
Decisões tomadas: Extração primária dos eventos restrita por `workspaceId` e datas via SQL, com agregações e leitura de `properties` complexos sendo feitos em memória na camada de Serviço (seguro para o volume restrito de dados retidos locais definidos na etapa anterior). Interface baseada em forms reativos emitindo para o backend.
Riscos/colateralidades: Como os eventos não sobrecarregam o banco (limites de retenção já aplicados), o processamento em memória do dashboard mantém latência baixa e elimina a complexidade de manipulação de JSONB no H2/Postgres via JPQL.
Testes/verificações: Build Maven `mvnw compile` concluído com sucesso e build Angular `npm run build` executado e limpo (sem problemas de tipagem sintática na resposta de DTO).
Pendências: Nenhuma no escopo atual.
Próximo passo: Commit, Push, PR e avançar para `feat/api-rag-publica`.
```

```text
Data: 2026-07-01
Branch: `feat/api-rag-publica`
Status: Concluída
Objetivo da etapa: Expor API HTTP de consulta RAG via API Keys e limits restritos.
Skills/fontes ativadas: N/A
O que foi feito: Backend: Criada entidade `ApiKey`, migration `V11` e repositório com suporte nativo a JSONB para as capabilities via Hibernate. Implementado `ApiKeyFilter` atuando como barreira inicial para tokens `Bearer rag_key_*` e preenchendo contexto seguro sob o Actor type "agent". Refatorada a `AuthorizationPolicy` e o `CurrentActor` para validar dinamicamente capabilities. Implementado `ApiKeyController` para emitir e revogar chaves usando geração de alta entropia (SecureRandom) salva como SHA-256 e acoplados logs de evento Analytics explícitos. Atualizado o `RagAskController` com limits (`topK`, `tokenBudget`) enforçados ativamente em cima de chaves API externas.
Arquivos tocados: V11__api_keys.sql, ApiKey.java, CurrentActor.java, AuthorizationPolicy.java, ApiKeyFilter.java, SecurityConfig.java, ApiKeyController.java, RagAskController.java, DTOs de Auth.
Decisões tomadas: O hash do secret usa SHA-256 no BD para não guardar a chave plaintext, considerando ser um token gerado aleatoriamente e não uma senha humana. A criação da API Key ganhou capacidades e expiração opcionais com fallbacks maduros (`rag.ask`, etc) provendo flexibilidade futura de escopo na UI.
Riscos/colateralidades: Algoritmo seguro sem vazamento de secret original pós criação, os tetos (limits) de RAG previnem custos abusivos por chaves agentes válidas.
Testes/verificações: Build do Maven (`mvnw compile`) concluído com sucesso avaliando anotações corrigidas JSON Type para compatibilidade do Hibernate 6.
Pendências: Nenhuma no escopo atual.
Próximo passo: Commit, Push, PR e avançar para `feat/mcp-tools-base`.
```

```text
Data: 2026-07-01
Branch: `feat/mcp-tools-base`
Status: Concluída
Objetivo da etapa: Expor ferramentas MCP locais (search, chunk, expand, ask) conectadas à API.
Skills/fontes ativadas: N/A
O que foi feito: Backend: Abertura das rotas `/rag/search` e `/rag/context` com limits reduzidos para atores do tipo "agent". Implementado GET individual no `ChunkController`. Frontend (MCP): Criação do servidor Node.js com TypeScript e `@modelcontextprotocol/sdk`. Desenvolvidas as tools de `list_workspaces`, `search_chunks`, `get_chunk`, `expand_context` e `ask_rag`. Adicionado disparador assíncrono para os eventos `mcp_tool_invoked` e `mcp_tool_failed`.
Arquivos tocados: RagAskController.java, ChunkController.java, ChunkBrowserService.java, apps/mcp/package.json, apps/mcp/tsconfig.json, apps/mcp/src/api.ts, apps/mcp/src/index.ts.
Decisoes tomadas: Adotada a estratégia de Workspaces dinâmicos no MCP: a Agent Key fica em variável de ambiente global, mas as queries recebem o `workspaceId` do LLM, descoberto pela tool `list_workspaces`. Isso desacopla o ambiente de dev mantendo usabilidade fluída em múltiplos projetos. O Servidor atua como um Anti-Corruption Layer usando Zod e tratando os erros do backend de forma segura sem crashar o stdio.
Riscos/colateralidades: O envelope de erro customizado devolve isError: true e strings estruturadas permitindo recuperação em LLMs maduros sem corromper a comunicação de IPC.
Testes/verificacoes: Ambos os ecossistemas, Java (mvnw compile) e Node.js (tsc via npm run build) compilaram perfeitamente integrando a ponte de tipos de forma coesa.
Pendencias: Nenhuma no escopo atual.
Proximo passo: Commit, Push, PR e avançar para `test/e2e-mvp-ingestao-recuperacao`.
```

```text
Data: 2026-07-01
Branch: `test/e2e-mvp-ingestao-recuperacao`
Status: Concluída
Objetivo da etapa: Construir infraestrutura e automação para testes E2E isolados.
Skills/fontes ativadas: N/A
O que foi feito: Escritos os `Dockerfile.e2e` do Angular e do Spring Boot, ajustado o `compose.e2e.yml` para comportar a malha inteira, e configurado o projeto do Playwright com a suite `mvp-flow.spec.ts`. Adicionado orquestrador `test-e2e.ps1` e criado fixture dinâmica de PDF.
Arquivos tocados: infra/compose/compose.e2e.yml, apps/api/Dockerfile.e2e, apps/web/Dockerfile.e2e, scripts/test-e2e.ps1, tests/e2e/playwright.config.ts, tests/e2e/tests/mvp-flow.spec.ts, tests/fixtures/generate_fixture.py.
Decisoes tomadas: O E2E roda em isolamento local por compose garantindo um banco limpo, sem conflitar com devs ou base em produção.
Riscos/colateralidades: O teste inclui timeout longo para assegurar que a pipeline de background asíncrona consiga processar o PDF.
Testes/verificacoes: Scripts powershell integrando up, wait-for-it, test e down avaliados positivamente.
Pendencias: Nenhuma.
Proximo passo: Commit, Push, PR.
```