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
| Data do registro | 2026-06-25 |
| Fase | Fundacao tecnica testavel |
| Branch atual | `chore/frontend-angular-base` |
| Linha de integracao | `develop` |
| Objetivo atual | Criar a base Angular executavel, testavel e alinhada ao design visual. |
| Status | `chore/frontend-angular-base` concluída; Angular 22 gerado com Node 24.15.0, CDK instalado, rotas e estilos globais implementados no ShellComponent, testes validados. |
| Proximo marco | Revisar diff, commitar/publicar a branch e abrir PR para `develop`; depois iniciar `chore/worker-python-base`. |

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

Proximo passo concreto:

- revisar diff da branch;
- commitar e publicar `build/dev-runtime-compose`;
- abrir PR para `develop`;
- apos merge, iniciar `chore/backend-spring-base`.

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
| `feat/worker-pdf-visual-tables-base` | Candidata | Interpretar imagens/tabelas em PDFs com assets, elementos e adapter visual | Pendente | Necessario para preservar conhecimento visual/tabular. |
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
