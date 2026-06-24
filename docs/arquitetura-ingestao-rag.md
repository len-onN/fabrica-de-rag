# Arquitetura de Ingestao e RAG

## Objetivo

Este documento fecha o portao `docs/arquitetura-ingestao-rag`.

Ele define a orquestracao de upload, inspecao, revisao de paginas, extracao, OCR, interpretacao visual, chunking, embeddings, indexacao, retry, cancelamento, storage, logs e reindexacao.

Ele nao implementa fila, worker real, OCR real, chunking, embeddings ou Qdrant. As branches de codigo devem implementar esta arquitetura por etapas pequenas e testaveis.

## Decisoes principais

| Tema | Decisao |
| --- | --- |
| Orquestrador | Spring Boot coordena runs, estado transacional, permissao, storage e chamadas ao worker. |
| Worker | Python executa operacoes documentais/ML por contratos HTTP internos versionados. |
| Execucao | Upload nao processa PDF de forma pesada no request. Ingestao roda como job observavel. |
| Pausa humana | Uma run pode entrar em `waiting_for_review` quando o mapa de paginas precisar de revisao. |
| Fonte da verdade | SQL guarda estado, etapas, logs seguros, artefatos e resultado ativo. |
| Storage | PDF original e artefatos ficam em storage por URI, com layout estavel por workspace/documento/run. |
| Qdrant | Indice derivado. Upsert/delete sao idempotentes e reconstruiveis a partir do SQL/storage. |
| Idempotencia | Toda run e etapa tem `idempotency_key`; artefatos e pontos vetoriais usam nomes/ids deterministicos. |
| Retry | Retry manual cria nova tentativa controlada, sem duplicar chunks, assets ativos ou pontos Qdrant. |
| Cancelamento | Cancelamento e cooperativo. Antes do worker e imediato; durante chamada ao worker e best-effort; depois da finalizacao nao altera run concluida. |
| Fila | REST interno e suficiente no MVP. Fila entra quando concorrencia, durabilidade ou cancelamento ativo justificarem. |

## Estados

### Document status

| Estado | Significado |
| --- | --- |
| `uploaded` | PDF original persistido, sem processamento pesado concluido. |
| `analyzing` | Ha run ativa preparando inspecao, render, extracao ou OCR. |
| `needs_page_review` | Run pausada para revisao/correcao do mapa de paginas. |
| `ready_to_index` | Paginas revisadas ou revisao dispensada; documento pode gerar chunks/embeddings. |
| `indexing` | Chunking, embedding ou indexacao vetorial em andamento. |
| `ready` | Resultado ativo pronto para busca e citacao. |
| `failed` | Ultima run ativa falhou sem resultado pronto novo. |
| `archived` | Documento removido da experiencia ativa. |

Transicoes permitidas:

```text
uploaded -> analyzing
analyzing -> needs_page_review
analyzing -> ready_to_index
needs_page_review -> ready_to_index
ready_to_index -> indexing
indexing -> ready
analyzing -> failed
ready_to_index -> failed
indexing -> failed
uploaded -> archived
needs_page_review -> archived
ready -> archived
failed -> analyzing
ready -> analyzing
```

Transicoes proibidas:

- `archived -> ready` sem reativacao explicita futura;
- `ready -> uploaded`;
- `failed -> ready` sem run de retry/reprocessamento;
- qualquer transicao que ignore workspace scope.

### Ingest run status

| Estado | Significado |
| --- | --- |
| `queued` | Run criada, ainda nao iniciou etapas. |
| `running` | Etapa automatica em execucao. |
| `waiting_for_review` | Run pausada esperando acao humana no mapa de paginas. |
| `completed` | Run finalizou e promoveu resultado ativo. |
| `failed` | Run falhou com erro seguro registrado. |
| `cancelled` | Run cancelada antes de concluir/promover resultado. |

Transicoes permitidas:

```text
queued -> running
queued -> cancelled
running -> waiting_for_review
waiting_for_review -> running
waiting_for_review -> cancelled
running -> completed
running -> failed
running -> cancelled
failed -> queued        # por retry que cria nova run vinculada
completed -> queued     # por reprocessamento/reindexacao que cria nova run
```

Uma run concluida nao muda para `cancelled` ou `failed`. Uma nova operacao cria nova run com `retry_of_run_id` ou `reprocess_of_run_id`.

### Ingest step status

| Estado | Significado |
| --- | --- |
| `queued` | Etapa planejada, ainda nao iniciou. |
| `running` | Etapa em execucao. |
| `completed` | Etapa gerou saida valida ou confirmou ausencia de trabalho. |
| `skipped` | Etapa dispensada por configuracao ou pre-condicao. |
| `waiting_for_review` | Etapa bloqueada por revisao humana. |
| `failed` | Etapa falhou e registrou erro seguro. |
| `cancelled` | Etapa interrompida antes de produzir/promover saida ativa. |

Cada etapa deve registrar `attempt`, `idempotency_key`, `started_at`, `finished_at`, `duration_ms`, `error_code`, `safe_details` e artefatos gerados.

## Pipeline MVP

```text
upload
-> create_ingest_run
-> validate_upload
-> inspect_pdf
-> render_pages
-> extract_text
-> decide_ocr
-> ocr_pages
-> extract_visual_elements
-> interpret_visual_elements
-> build_page_map
-> wait_for_page_review?
-> build_chunks
-> generate_embeddings
-> index_qdrant
-> finalize
```

### Upload

Responsabilidades:

- validar content type e extensao `.pdf`;
- persistir PDF original;
- calcular `source_hash`;
- criar `document` em `uploaded`;
- criar `asset` `original_pdf`;
- nao chamar worker pesado no request de upload.

Resultado:

- response `201 Created` com `documentId`;
- ingestao inicia por `POST /api/v1/documents/{documentId}/ingest-runs` ou por acao explicita do wizard.

### Create ingest run

Responsabilidades:

- validar workspace, permissao, documento e parametros;
- gerar `idempotency_key` da run;
- impedir duas runs ativas para o mesmo documento quando elas concorrem pelo resultado ativo;
- criar etapas planejadas conforme perfil e configuracao.

Idempotency key sugerida:

```text
sha256(workspace_id + ":" + document_id + ":" + source_hash + ":" + profile + ":" + normalized_parameters_hash)
```

### Validate upload

Verifica:

- arquivo existe no storage;
- hash bate com metadado;
- MIME/assinatura sao compativeis com PDF;
- tamanho esta dentro de limites configurados;
- documento nao esta arquivado.

### Inspect PDF

Worker:

- `POST /worker/v1/pdf/inspect`

Saidas:

- page count;
- encrypted/corrupt flags;
- metadados seguros;
- erro `pdf_encrypted`, `pdf_corrupt`, `pdf_too_large` quando aplicavel.

### Render pages

Worker:

- `POST /worker/v1/pdf/render-page`

Saidas:

- renders por pagina;
- thumbnails opcionais;
- asset `page_render`;
- duracao e erro por pagina.

Render e necessario para mapa de paginas, preview de citacao, OCR e evidencias visuais.

### Extract text

Worker:

- `POST /worker/v1/pdf/extract-text`

Saidas:

- text blocks;
- qualidade textual por pagina;
- headings quando detectaveis;
- source locator por bloco.

### Decide OCR

Politica:

- `ocrMode=off`: nao executa OCR;
- `ocrMode=force`: executa nas paginas selecionadas ou em todas, conforme request;
- `ocrMode=auto`: executa em paginas com texto nativo insuficiente ou suspeita de scan.

Esta etapa apenas decide paginas candidatas. Algoritmo fino de qualidade fica em `docs/algoritmos-rag-mvp` quando envolver limiares.

### OCR pages

Worker:

- `POST /worker/v1/pdf/ocr`

Saidas:

- texto OCR por pagina;
- qualidade;
- erro por pagina;
- artefatos intermediarios quando necessario.

Falha de OCR em algumas paginas nao precisa falhar a run inteira se o perfil permitir indexar o restante com warning.

### Extract visual elements

Worker:

- `POST /worker/v1/pdf/extract-elements`

Saidas:

- `document_elements`;
- assets/crops quando existirem;
- tabelas simples em texto estruturado;
- bbox, reading order e source locator.

### Interpret visual elements

Worker:

- `POST /worker/v1/pdf/interpret-visual`

Politica:

- mock/deterministico por padrao em testes;
- provider remoto apenas quando configurado;
- budget e privacidade detalhados em `docs/interpretacao-imagens-tabelas-pdf`;
- interpretacao e dado derivado, nunca fonte canonica.

### Build page map

Responsabilidades:

- criar/atualizar `document_pages`;
- aplicar labels detectados quando houver;
- marcar pagina com OCR, warnings, vazia ou desconhecida;
- deixar `document_status=needs_page_review` e `run_status=waiting_for_review` quando `reviewPageMapBeforeIndexing=true` ou quando houver conflito critico.

Pausa obrigatoria quando:

- usuario pediu revisao antes de indexar;
- PDF tem numeracao ambigua relevante;
- numero de paginas detectado diverge de artefatos gerados;
- pagina essencial falhou em render/extracao e o perfil nao permite seguir.

### Resume after page review

Pre-condicoes:

- usuario com permissao `page_map.edit` salvou o mapa;
- documento ainda pertence ao workspace;
- run esta em `waiting_for_review`;
- nenhuma run mais nova promoveu resultado ativo.

Transicao:

```text
document: needs_page_review -> ready_to_index
run: waiting_for_review -> running
step: waiting_for_review -> completed
```

### Build chunks

Responsabilidades:

- gerar chunks a partir de texto, tabelas e interpretacoes visuais;
- preservar `source_locator`;
- criar relacoes `previous`, `next`, `caption_of_image`, `table_continuation` e `derived_from_visual` quando diretas;
- atribuir `ingestion_generation`.

Algoritmo fino de chunking fica em `docs/algoritmos-rag-mvp`.

### Generate embeddings

Responsabilidades:

- chamar provider mockado ou real conforme branch de algoritmos/embeddings;
- registrar modelo, versao, dimensao e status;
- nao bloquear armazenamento dos chunks quando embedding falhar de forma recuperavel.

### Index Qdrant

Responsabilidades:

- criar/upsert de pontos com point id deterministico;
- filtrar por workspace e colecao;
- marcar `chunk_embeddings.status`;
- registrar falhas por lote.

Qdrant nunca promove sozinho o resultado ativo. A promocao acontece no SQL na etapa `finalize`.

### Finalize

Responsabilidades:

- promover nova `ingestion_generation` como ativa;
- marcar chunks/embeddings antigos como `stale` ou `archived`;
- remover ou agendar delete de pontos antigos;
- atualizar contadores da colecao;
- registrar eventos locais;
- marcar documento `ready` e run `completed`.

## Retry

Retry manual:

- cria nova run vinculada por `retry_of_run_id`;
- copia parametros normalizados da run anterior, salvo override explicito;
- recomputa idempotency keys;
- reutiliza artefatos seguros quando hash, contrato e parametros forem iguais;
- nunca duplica resultado ativo.

Retry automatico:

- permitido apenas para falhas transientes: timeout do worker, erro de rede local, Qdrant indisponivel, lock temporario;
- maximo inicial: 2 tentativas por etapa transiente;
- backoff simples no MVP;
- falhas de contrato, PDF invalido, permissao, hash divergente e storage ausente nao sao retry automatico.

Etapas seguras para repetir:

| Etapa | Seguro repetir? | Regra |
| --- | --- | --- |
| `validate_upload` | Sim | Leitura pura. |
| `inspect_pdf` | Sim | Saida sobrescreve por idempotency key. |
| `render_pages` | Sim | Artefato por pagina/hash. |
| `extract_text` | Sim | Artefato por run/input hash. |
| `ocr_pages` | Sim com custo | Respeitar budget e parametros. |
| `extract_visual_elements` | Sim | Upsert por source locator/hash. |
| `interpret_visual_elements` | Sim com custo | Cache por input hash/model/prompt. |
| `build_chunks` | Sim | Usa generation nova, nao altera ativa ate finalize. |
| `generate_embeddings` | Sim com custo | Idempotente por chunk/model/version. |
| `index_qdrant` | Sim | Point id deterministico. |
| `finalize` | Sim com trava | Deve ser transacional e idempotente. |

## Cancelamento

Cancelamento por estado:

| Estado da run | Semantica |
| --- | --- |
| `queued` | Marcar run e steps como `cancelled`; documento volta ao estado anterior. |
| `running` antes do worker | Cancelar antes de iniciar a proxima etapa. |
| `running` durante worker | Marcar `cancel_requested_at`; aguardar retorno/timeout; nao iniciar novas etapas. |
| `waiting_for_review` | Cancelar run; documento permanece com artefatos e pode receber nova run. |
| `completed` | Nao cancela. Criar reprocessamento se quiser novo resultado. |
| `failed` | Nao cancela. Retry cria nova run. |

Cancelamento nao deve apagar diagnostico automaticamente. Limpeza de temporarios acontece por politica de retencao.

## Reprocessamento e reindexacao

Conceito:

- cada run que chega a chunking/indexacao cria uma `ingestion_generation`;
- somente uma generation fica ativa por documento/colecao;
- chunks, embeddings e pontos antigos nao sao sobrescritos no meio da run;
- `finalize` promove a generation nova e torna a anterior `stale`.

Reindexacao sem novo OCR/chunking:

- reutiliza chunks ativos;
- cria nova generation de embeddings se modelo/payload mudar;
- apaga/recria pontos Qdrant por filtro de workspace/collection/document/generation;
- preserva historico de runs.

Reprocessamento completo:

- roda pipeline desde inspect/extracao conforme parametros;
- pode reutilizar PDF original e renders se hash/contrato forem iguais;
- gera nova generation de chunks.

## Storage URI

Formato canonico:

```text
storage://workspaces/{workspaceId}/documents/{documentId}/original/original.pdf
storage://workspaces/{workspaceId}/documents/{documentId}/runs/{runId}/pages/{filePageNumber}/render.png
storage://workspaces/{workspaceId}/documents/{documentId}/runs/{runId}/pages/{filePageNumber}/text.json
storage://workspaces/{workspaceId}/documents/{documentId}/runs/{runId}/pages/{filePageNumber}/ocr.json
storage://workspaces/{workspaceId}/documents/{documentId}/runs/{runId}/elements/{elementId}/asset.png
storage://workspaces/{workspaceId}/documents/{documentId}/runs/{runId}/chunks/chunks.json
```

Regras:

- storage URI usa IDs publicos;
- backend resolve URI para caminho local ou MinIO/S3 futuro;
- worker recebe URI autorizada, nao caminho livre arbitrario;
- artefatos temporarios usam sufixo `.tmp` ou pasta `tmp/` e so sao promovidos apos etapa concluir;
- nenhum arquivo pode escapar do workspace/documento/run;
- exclusao logica do documento remove da experiencia ativa e agenda limpeza de storage conforme politica.

## Erro estruturado

Erro de etapa:

```json
{
  "contractVersion": "ingestion.step_error.v1",
  "workspaceId": "wsp_fixture_alpha",
  "runId": "run_fixture_pdf",
  "stepName": "inspect_pdf",
  "attempt": 1,
  "correlationId": "req_fixture_001",
  "code": "pdf_encrypted",
  "message": "PDF is encrypted.",
  "retryable": false,
  "affectedPage": null,
  "safeDetails": {
    "workerContractVersion": "worker.pdf.inspect.response.v1"
  }
}
```

`message` deve ser segura para UI. Detalhe tecnico interno pode ir para log seguro sem segredo ou conteudo bruto.

## Observabilidade

Tres trilhos diferentes:

| Trilha | Uso | Conteudo |
| --- | --- | --- |
| Run log | Diagnostico operacional da run | etapa, status, mensagem segura, pagina, correlation id |
| Analytics event | Metrica local e dashboard | duracao, origem, sucesso, contadores, buckets |
| Audit futuro | Acoes sensiveis | ator, permissao, recurso, decisao |

Regras:

- run log pode ser mais detalhado, mas nao carrega segredo nem texto integral;
- analytics usa schema versionado e propriedades pequenas;
- auditoria enterprise fica fora do MVP, mas a arquitetura nao deve impedir sua adicao.

## Quando REST interno deixa de bastar

REST interno continua no MVP enquanto:

- uma instancia local controla o worker;
- concorrencia e baixa;
- chamada pesada pode ser feita por job backend fora do request do usuario;
- retry/cancelamento cooperativo sao suficientes;
- o banco persiste estado antes/depois de cada etapa.

Fila deve entrar quando houver pelo menos um destes sinais:

- varias instancias de API/worker concorrendo;
- necessidade de reentrega duravel por mensagem;
- cancelamento ativo de tarefa longa no worker;
- backpressure por muitos PDFs simultaneos;
- jobs que precisam sobreviver a restart sem reconciliacao manual;
- escalonamento horizontal do worker.

Quando a fila entrar, ela deve implementar o mesmo contrato de step. Use cases e dominio nao devem depender de tecnologia da fila.

## Pontos OCP/LSP/IoC

Pontos de extensao:

- `IngestStep` por etapa;
- `WorkerClient` por adapter HTTP/futuro queue;
- `StoragePort` por filesystem local/MinIO/S3;
- `VectorIndexPort` por Qdrant/futuro provider;
- `OcrPolicy`, `PageReviewPolicy`, `RetryPolicy`, `ChunkingPolicy`;
- `EventPublisher` para analytics local.

Invariantes LSP:

- toda implementacao de step preserva status, erro, timeout, idempotency key e artefatos declarados;
- worker mockado e real retornam erros estruturados equivalentes;
- storage local e S3 futuro preservam isolamento por workspace/documento/run;
- Qdrant local e futuro vector store preservam filtros obrigatorios;
- fila futura nao altera semantica observavel da run.

IoC:

- Spring configuration liga steps, worker client, storage, event publisher e vector adapter;
- use cases dependem de ports e policies locais;
- worker Python liga bibliotecas concretas por dependencies/factories;
- MCP/API apenas observam status via backend, sem acesso direto ao pipeline.

## Testabilidade

Testes de contrato planejados:

- transicoes validas e invalidas da state machine;
- retry de etapa idempotente;
- cancelamento em `queued`, `running` e `waiting_for_review`;
- storage URI nao escapa do workspace/documento/run;
- erro estruturado sem segredo;
- reindexacao nao duplica chunks/pontos;
- Qdrant sempre recebe filtro workspace/collection.

Fixtures iniciais:

- `tests/contracts/ingestion/ingest-run.snapshot.v1.json`;
- `tests/contracts/ingestion/ingest-step-error.v1.json`;
- `tests/contracts/ingestion/reindex-request.v1.json`.

## Pendencias encaminhadas

- thresholds de OCR, chunking, contexto e budgets numericos: `docs/algoritmos-rag-mvp`;
- matriz de permissoes para iniciar, cancelar, retry e reindexar runs: `docs/seguranca-permissoes-mvp`;
- taxonomia final de eventos e retencao: `docs/analytics-observabilidade-mvp`;
- provider visual, privacidade e budgets de LLM/VLM: `docs/interpretacao-imagens-tabelas-pdf`;
- implementacao real de background jobs no Spring: branches de codigo da fundacao/backend/ingestao.
