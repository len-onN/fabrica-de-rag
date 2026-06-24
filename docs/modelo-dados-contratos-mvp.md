# Modelo de Dados e Contratos do MVP

## Objetivo

Este documento fecha o portao `docs/modelo-dados-contratos-mvp`.

Ele define o modelo relacional conceitual, a estrategia de IDs, os contratos REST, os contratos internos Spring -> worker, o payload minimo do Qdrant, o envelope de eventos locais e a politica de compatibilidade entre schemas.

Ele nao e migration, codigo Java, codigo Python ou OpenAPI gerado. As branches de implementacao devem transformar este contrato em migrations, DTOs, modelos Pydantic, testes e schemas versionados.

## Decisoes principais

| Tema | Decisao |
| --- | --- |
| Fonte da verdade | PostgreSQL guarda estado, proveniencia, relacoes, runs e analytics. |
| Indice derivado | Qdrant guarda vetores e payload minimo para busca/filtros. |
| IDs internos | `uuid` como chave primaria tecnica, gerada pela aplicacao. Preferir UUIDv7 quando a biblioteca da stack estiver definida; UUIDv4 e fallback aceitavel. |
| IDs publicos | `public_id` opaco, prefixado por tipo, usado em URLs, JSON, fixtures, logs seguros e payload Qdrant. |
| Workspace scope | Entidades de negocio carregam `workspace_id` direto ou via relacionamento obrigatorio claro. Queries devem filtrar por workspace antes de enriquecer resultado. |
| Soft delete | Recursos de usuario usam `deleted_at`/`deleted_by_user_id` e status visivel quando fizer sentido. Eventos podem expirar por retencao. Binarios e vetores derivados sao removidos/reindexados por job. |
| Contratos REST | `/api/v1`, JSON `camelCase`, enums em `snake_case`, erros em Problem Details com campos extras padronizados. |
| API interna worker | `/worker/v1`, Pydantic, `contractVersion`, `requestId`, `workspaceId`, erro estruturado e timeout por chamada. |
| OpenAPI | OpenAPI 3.1 como formato alvo. `tests/contracts` e fixtures JSON sao a fonte canonica inicial; a branch backend deve publicar `/v3/api-docs` com a biblioteca compativel com Spring Boot 4.1 validada por teste. |
| Versionamento | DTOs, eventos, schemas Pydantic, tool schemas e payload Qdrant usam versao explicita. Mudanca quebradora cria versao nova ou plano de migracao. |
| Fonte futura | O modelo deve guardar `source_type` e `source_locator` estruturado para nao prender o dominio a PDF. |

## IDs publicos

IDs publicos sao strings opacas. Eles nao codificam workspace, data, sequencia ou informacao sensivel.

Formato:

```text
<prefix>_<base32-ou-base62>
```

Prefixos iniciais:

| Recurso | Prefixo |
| --- | --- |
| User | `usr` |
| Workspace | `wsp` |
| Membership | `mem` |
| Collection | `col` |
| Document | `doc` |
| Page | `pag` |
| Element | `elm` |
| Asset | `ast` |
| Visual interpretation | `vis` |
| Chunk | `chk` |
| Embedding | `emb` |
| Ingest run | `run` |
| Event | `evt` |
| Vector connection | `vcn` |
| Vector binding | `vbd` |
| API key | `key` |
| Service account | `svc` |

Regras:

- URLs e payloads externos usam `public_id`.
- FKs internas usam `uuid`.
- Fixtures podem usar IDs previsiveis como `wsp_fixture_alpha`.
- Logs e analytics usam IDs publicos, nunca hash de senha, segredo, header de auth ou connection string.
- `public_id` deve ser unico globalmente por tabela e imutavel.

## Convencoes relacionais

Tabelas:

- nomes em `snake_case` plural;
- colunas em `snake_case`;
- `id uuid primary key`;
- `public_id varchar(...) not null unique`;
- `created_at timestamptz not null`;
- `updated_at timestamptz not null`;
- `deleted_at timestamptz null` apenas em recursos apagaveis pelo usuario;
- `metadata jsonb not null default '{}'::jsonb` apenas para metadados variaveis;
- `contract_version varchar(20) not null` quando o registro persistir resultado derivado por contrato versionado.

Constraints:

- `pk_<table>`;
- `fk_<table>_<target>`;
- `uk_<table>_<columns>`;
- `ck_<table>_<rule>`;
- `idx_<table>_<columns>`.

JSONB pode carregar metadados extensivos, mas nao deve esconder contrato essencial. Campos usados em filtro, permissao, estado, ordenacao, relacao, proveniencia ou citacao devem ser coluna normalizada.

## Modelo relacional inicial

### Identity e workspace

`users`

- `id`, `public_id`
- `display_name`
- `email`
- `status`: `active`, `disabled`
- `created_at`, `updated_at`, `deleted_at`

`auth_identities`

- `id`, `public_id`
- `user_id`
- `provider`: `local`
- `provider_subject`
- `password_hash`
- `created_at`, `updated_at`

`auth_sessions`

- `id`, `public_id`
- `user_id`
- `session_hash`
- `csrf_token_hash`
- `created_at`
- `last_seen_at`
- `expires_at`
- `absolute_expires_at`
- `revoked_at null`
- `revoked_reason null`
- `user_agent_hash null`
- `ip_hash null`

`workspaces`

- `id`, `public_id`
- `name`
- `slug`
- `owner_user_id`
- `purpose`: `study`, `research`, `writing`, `code`, `agent`, `general`
- `settings jsonb`
- `created_at`, `updated_at`, `deleted_at`

`workspace_memberships`

- `id`, `public_id`
- `workspace_id`
- `user_id`
- `role`: `owner`, `admin`, `curator`, `member`, `viewer`, `agent`
- `status`: `active`, `invited`, `disabled`
- `created_at`, `updated_at`, `deleted_at`

`api_keys`

- `id`, `public_id`
- `workspace_id`
- `created_by_user_id`
- `name`
- `key_prefix`
- `key_hash`
- `role`: `agent`
- `capabilities jsonb`
- `expires_at null`
- `revoked_at null`
- `last_used_at null`
- `created_at`, `updated_at`, `deleted_at`

Indices minimos:

- `uk_users_email`;
- `idx_auth_sessions_user_active`;
- `idx_auth_sessions_expires_at`;
- `uk_workspaces_slug`;
- `uk_workspace_memberships_workspace_user`;
- `idx_workspace_memberships_user_status`;
- `idx_api_keys_workspace_active`;

### Colecoes e configuracoes

`knowledge_collections`

- `id`, `public_id`
- `workspace_id`
- `name`
- `description`
- `purpose`: `study`, `research`, `writing`, `code`, `agent`, `general`
- `status`: `empty`, `processing`, `ready`, `failed`, `archived`
- `default_ingestion_profile`: `fast`, `balanced`
- `default_context_policy`: `conservative`, `sequential`
- `default_embedding_model`
- `default_vector_binding_id null`
- `created_by_user_id`
- `created_at`, `updated_at`, `deleted_at`

Indices minimos:

- `idx_knowledge_collections_workspace_status`;
- `idx_knowledge_collections_workspace_updated_at`;
- `uk_knowledge_collections_workspace_name_active`, considerando apenas nao deletadas.

### Fontes, documentos e storage

`documents`

- `id`, `public_id`
- `workspace_id`
- `knowledge_collection_id`
- `source_type`: `pdf_upload`
- `source_uri`
- `source_hash`
- `source_locator jsonb`
- `original_filename`
- `mime_type`: `application/pdf` no MVP
- `file_size_bytes`
- `status`: `uploaded`, `analyzing`, `needs_page_review`, `ready_to_index`, `indexing`, `ready`, `failed`, `archived`
- `page_count`
- `storage_uri`
- `created_by_user_id`
- `created_at`, `updated_at`, `deleted_at`

`assets`

- `id`, `public_id`
- `workspace_id`
- `document_id`
- `document_page_id null`
- `document_element_id null`
- `asset_type`: `original_pdf`, `page_render`, `embedded_image`, `crop`, `thumbnail`
- `storage_uri`
- `mime_type`
- `content_hash`
- `width_px null`
- `height_px null`
- `bbox jsonb null`
- `source_locator jsonb`
- `created_at`, `updated_at`, `deleted_at`

Indices minimos:

- `idx_documents_workspace_collection_status`;
- `idx_documents_workspace_hash`;
- `idx_assets_workspace_document_type`;
- `idx_assets_workspace_hash`.

### Paginas, numeracao e elementos

`document_pages`

- `id`, `public_id`
- `workspace_id`
- `document_id`
- `file_page_number`
- `detected_printed_label null`
- `effective_printed_label null`
- `numbering_style null`: `arabic`, `roman`, `custom`
- `page_role`: `cover`, `toc`, `preface`, `body`, `appendix`, `blank`, `separator`, `unknown`
- `include_in_search boolean`
- `include_in_numbering boolean`
- `ocr_status`: `not_needed`, `pending`, `completed`, `failed`, `disabled`
- `text_quality jsonb`
- `source_locator jsonb`
- `created_at`, `updated_at`

`page_numbering_anchors`

- `id`, `public_id`
- `workspace_id`
- `document_id`
- `document_page_id`
- `file_page_number`
- `printed_label`
- `numbering_style`
- `apply_direction`: `forward`, `backward`, `both`
- `created_by_user_id`
- `created_at`

`document_elements`

- `id`, `public_id`
- `workspace_id`
- `document_id`
- `document_page_id`
- `asset_id null`
- `element_type`: `text_block`, `heading`, `table`, `figure`, `image`, `caption`, `footnote`, `header`, `footer`, `unknown`
- `reading_order`
- `bbox jsonb null`
- `text_content null`
- `structured_content jsonb`
- `confidence numeric(5,4) null`
- `extraction_method`
- `source_locator jsonb`
- `created_at`, `updated_at`, `deleted_at`

`visual_interpretations`

- `id`, `public_id`
- `workspace_id`
- `document_id`
- `document_page_id`
- `document_element_id`
- `asset_id null`
- `interpretation_text`
- `structured_text jsonb`
- `provider`
- `model`
- `prompt_version`
- `input_hash`
- `confidence numeric(5,4) null`
- `status`: `completed`, `failed`, `skipped`
- `error_code null`
- `contract_version`
- `created_at`, `updated_at`

Indices minimos:

- `uk_document_pages_document_file_page`;
- `idx_document_pages_workspace_document`;
- `idx_document_elements_workspace_document_page_order`;
- `idx_document_elements_workspace_type`;
- `idx_visual_interpretations_workspace_element`.

### Chunks, relacoes e embeddings

`chunks`

- `id`, `public_id`
- `workspace_id`
- `knowledge_collection_id`
- `document_id`
- `document_page_start_id null`
- `document_page_end_id null`
- `source_element_id null`
- `sequence_number`
- `content_kind`: `text`, `table_text`, `visual_interpretation`, `mixed`
- `content`
- `heading_path text[]`
- `token_count`
- `source_locator jsonb`
- `source_hash`
- `chunking_strategy`
- `chunking_version`
- `status`: `pending_embedding`, `embedded`, `failed`, `stale`, `archived`
- `created_at`, `updated_at`, `deleted_at`

`chunk_relations`

- `id`, `public_id`
- `workspace_id`
- `from_chunk_id`
- `to_chunk_id`
- `relation_type`: `previous`, `next`, `same_section`, `caption_of_image`, `table_continuation`, `derived_from_visual`, `references`
- `weight numeric(5,4) null`
- `created_at`

`chunk_embeddings`

- `id`, `public_id`
- `workspace_id`
- `chunk_id`
- `vector_binding_id`
- `embedding_model`
- `embedding_model_version`
- `dimension`
- `distance_metric`: `cosine`, `dot`, `euclid`
- `vector_space`
- `qdrant_point_id`
- `payload_contract_version`
- `status`: `pending`, `embedded`, `failed`, `stale`
- `created_at`, `updated_at`

Indices minimos:

- `idx_chunks_workspace_collection_status`;
- `idx_chunks_workspace_document_sequence`;
- `idx_chunks_workspace_source_element`;
- `uk_chunk_embeddings_binding_chunk_model`;
- `idx_chunk_embeddings_workspace_status`;

### Runs, etapas e logs

`ingest_runs`

- `id`, `public_id`
- `workspace_id`
- `knowledge_collection_id`
- `document_id`
- `status`: `queued`, `running`, `waiting_for_review`, `completed`, `failed`, `cancelled`
- `retry_of_run_id null`
- `reprocess_of_run_id null`
- `idempotency_key`
- `ingestion_generation null`
- `profile`: `fast`, `balanced`
- `parameters jsonb`
- `worker_contract_version`
- `cancel_requested_at null`
- `started_at null`
- `finished_at null`
- `created_by_user_id`
- `created_at`, `updated_at`

`ingest_run_stages`

- `id`, `public_id`
- `workspace_id`
- `ingest_run_id`
- `stage_name`: `validation`, `inspect`, `extract_text`, `ocr`, `render`, `visual_elements`, `visual_interpretation`, `chunking`, `embedding`, `indexing`
- `status`: `queued`, `running`, `completed`, `failed`, `skipped`, `waiting_for_review`, `cancelled`
- `attempt`
- `idempotency_key`
- `progress_current null`
- `progress_total null`
- `started_at null`
- `finished_at null`
- `duration_ms null`
- `error_code null`
- `error_message null`
- `safe_details jsonb`
- `artifact_refs jsonb`
- `created_at`, `updated_at`

`ingest_run_logs`

- `id`, `public_id`
- `workspace_id`
- `ingest_run_id`
- `stage_id null`
- `level`: `debug`, `info`, `warn`, `error`
- `message`
- `safe_details jsonb`
- `created_at`

Logs nao podem carregar texto integral do PDF, secrets, headers de auth, connection strings ou embeddings.

### Vector store

`vector_connections`

- `id`, `public_id`
- `workspace_id null`
- `owner_user_id null`
- `owner_scope`: `workspace`, `user`
- `name`
- `provider`: `qdrant`
- `mode`: `managed_by_app`, `external_existing`, `search_only`
- `endpoint redacted/null`
- `auth_secret_ref null`
- `capabilities jsonb`
- `created_at`, `updated_at`, `deleted_at`

`vector_index_bindings`

- `id`, `public_id`
- `workspace_id`
- `knowledge_collection_id`
- `vector_connection_id`
- `remote_collection_name`
- `remote_namespace null`
- `vector_name`
- `embedding_model`
- `embedding_model_version`
- `dimension`
- `distance_metric`
- `payload_contract_version`
- `sync_status`: `pending`, `ready`, `failed`, `stale`
- `last_sync_at null`
- `created_at`, `updated_at`, `deleted_at`

O MVP usa Qdrant interno. O modelo ja permite conexoes externas futuras, mas UI completa de conexoes fica fora do MVP funcional inicial.

### Analytics e feedback

`analytics_events`

- `id`, `public_id`
- `workspace_id`
- `occurred_at`
- `event_name`
- `event_version`
- `origin`: `ui`, `api`, `worker`, `mcp`
- `actor_type`: `user`, `service_account`, `system`
- `actor_public_id null`
- `session_id null`
- `correlation_id`
- `resource_type null`
- `resource_public_id null`
- `properties jsonb`
- `created_at`

`retrieval_feedback`

- `id`, `public_id`
- `workspace_id`
- `knowledge_collection_id`
- `document_id null`
- `chunk_id null`
- `query_event_id null`
- `feedback_type`: `chunk_relevant`, `chunk_irrelevant`, `answer_useful`, `answer_not_useful`, `missing_context`
- `note null`
- `created_by_user_id null`
- `created_at`

Eventos seguem retencao configuravel; feedback explicito pode ser mantido enquanto a colecao existir, salvo exclusao do workspace.

## Source locator

`source_locator` e JSON estruturado, versionado implicitamente pelo `source_type` e pelo contrato do recurso.

PDF:

```json
{
  "sourceType": "pdf_upload",
  "documentId": "doc_fixture_pdf",
  "filePageNumber": 3,
  "printedLabel": "2",
  "bbox": [72, 144, 520, 360],
  "readingOrder": 18
}
```

Regras:

- `filePageNumber` e 1-based.
- `bbox` usa pontos PDF ou unidade declarada no contrato do worker. A unidade deve ser fixa por contrato.
- Campos de fontes futuras entram sem alterar chunks existentes.
- Citacoes devem conseguir montar pelo menos documento, pagina fisica, pagina impressa quando houver, chunk e trecho.

## Contratos REST

Padroes:

- content type `application/json`, exceto upload `multipart/form-data`;
- request/response em `camelCase`;
- enums serializados em `snake_case`;
- IDs em payloads sao publicos;
- timestamps em ISO 8601 UTC;
- listas usam paginacao por cursor;
- mutacoes retornam recurso atualizado ou `202 Accepted` quando iniciarem job.

Paginacao:

```json
{
  "data": [],
  "page": {
    "limit": 25,
    "nextCursor": null,
    "sort": "updated_at_desc"
  }
}
```

Parametros comuns:

```text
limit: default 25, max 100
cursor: opaco
sort: lista fechada por endpoint
q: busca textual simples quando aplicavel
```

Erro:

```json
{
  "type": "https://fabrica-rag.local/problems/validation-error",
  "title": "Validation error",
  "status": 400,
  "detail": "Request contains invalid fields.",
  "instance": "/api/v1/workspaces/wsp_fixture/collections",
  "code": "validation_error",
  "correlationId": "req_fixture_001",
  "fieldErrors": [
    {
      "field": "name",
      "code": "required",
      "message": "Name is required."
    }
  ]
}
```

DTOs canonicos iniciais:

- `WorkspaceSummary`
- `WorkspaceSettings`
- `UserSummary`
- `CollectionSummary`
- `CollectionDetail`
- `DocumentSummary`
- `DocumentDetail`
- `IngestRunDetail`
- `DocumentPageDetail`
- `DocumentElementDetail`
- `ChunkSummary`
- `ChunkDetail`
- `SearchRequest`
- `SearchResponse`
- `ContextAssembleRequest`
- `AskRequest`
- `AnalyticsEventRequest`
- `AnalyticsSummary`

## Contratos Spring -> worker

Padrao de request:

```json
{
  "contractVersion": "worker.pdf.inspect.v1",
  "requestId": "req_fixture_001",
  "workspaceId": "wsp_fixture_alpha",
  "documentId": "doc_fixture_pdf",
  "storageUri": "storage://documents/doc_fixture_pdf/original.pdf"
}
```

Padrao de erro:

```json
{
  "contractVersion": "worker.error.v1",
  "requestId": "req_fixture_001",
  "error": {
    "code": "pdf_encrypted",
    "message": "PDF is encrypted.",
    "retryable": false,
    "safeDetails": {
      "stage": "inspect"
    }
  }
}
```

Regras:

- Worker recebe IDs publicos e nunca decide permissao.
- Backend valida workspace, permissao, status e storage antes de chamar worker.
- Worker nao acessa Postgres ou Qdrant no MVP.
- Toda resposta carrega `contractVersion`.
- Timeouts e limites sao configurados pelo backend/chamada, nao embutidos em payload livre.
- Modelos Pydantic devem ter `extra="forbid"` ou equivalente para contratos fechados.

Contratos iniciais:

| Endpoint | Request version | Response version |
| --- | --- | --- |
| `POST /worker/v1/pdf/inspect` | `worker.pdf.inspect.request.v1` | `worker.pdf.inspect.response.v1` |
| `POST /worker/v1/pdf/extract-text` | `worker.pdf.extract_text.request.v1` | `worker.pdf.extract_text.response.v1` |
| `POST /worker/v1/pdf/ocr` | `worker.pdf.ocr.request.v1` | `worker.pdf.ocr.response.v1` |
| `POST /worker/v1/pdf/render-page` | `worker.pdf.render_page.request.v1` | `worker.pdf.render_page.response.v1` |
| `POST /worker/v1/pdf/extract-elements` | `worker.pdf.extract_elements.request.v1` | `worker.pdf.extract_elements.response.v1` |
| `POST /worker/v1/pdf/interpret-visual` | `worker.pdf.interpret_visual.request.v1` | `worker.pdf.interpret_visual.response.v1` |
| `POST /worker/v1/chunks/build` | `worker.chunks.build.request.v1` | `worker.chunks.build.response.v1` |
| `POST /worker/v1/embeddings/text` | `worker.embeddings.text.request.v1` | `worker.embeddings.text.response.v1` |

## Payload Qdrant

Point id:

```text
sha256(payload_contract_version + ":" + workspace_id + ":" + chunk_id + ":" + embedding_model_version)
```

O hash deve ser deterministico para upsert idempotente.

Payload minimo:

```json
{
  "payloadContractVersion": "qdrant.chunk.v1",
  "workspaceId": "wsp_fixture_alpha",
  "collectionId": "col_fixture_rag",
  "documentId": "doc_fixture_pdf",
  "chunkId": "chk_fixture_intro",
  "contentKind": "text",
  "embeddingModel": "mock-text-embedding",
  "embeddingModelVersion": "mock-text-embedding-v1",
  "vectorSpace": "text_chunks_v1",
  "sourceType": "pdf_upload",
  "filePageStart": 3,
  "filePageEnd": 3,
  "printedPageStart": "2",
  "printedPageEnd": "2",
  "headingPath": ["Capitulo 1", "Introducao"],
  "sourceHash": "sha256:fixture",
  "chunkingVersion": "semantic_block_v1"
}
```

Filtros obrigatorios por busca:

- `workspaceId`;
- `collectionId` ou lista de colecoes autorizadas;
- `contentKind` quando a busca exigir subconjunto;
- `payloadContractVersion` quando houver migracao em curso.

SQL deve enriquecer o resultado antes de retornar ao usuario. Qdrant nao deve ser fonte unica de citacao, permissao, documento, pagina, asset ou texto final.

## Eventos locais

Envelope:

```json
{
  "eventVersion": "analytics.event.v1",
  "eventName": "ingest_stage_completed",
  "origin": "worker",
  "workspaceId": "wsp_fixture_alpha",
  "actor": {
    "type": "system",
    "id": null
  },
  "correlationId": "req_fixture_001",
  "occurredAt": "2026-06-24T21:30:00Z",
  "resource": {
    "type": "ingest_run",
    "id": "run_fixture_pdf"
  },
  "properties": {
    "stageName": "inspect",
    "durationMs": 143,
    "success": true
  }
}
```

Campos proibidos:

- segredo, token, API key, header de auth;
- connection string;
- embedding vector;
- PDF bruto;
- texto integral de documento por padrao;
- pergunta/resposta quando historico completo estiver desligado;
- prompt completo de LLM/VLM quando puder carregar conteudo sensivel.

## Compatibilidade entre contratos

Fonte canonica inicial:

```text
tests/contracts
+-- rest
+-- worker
+-- qdrant
+-- events
+-- mcp
```

Regras:

- Cada contrato versionado deve ter pelo menos um exemplo valido.
- DTO Java, Pydantic, schema MCP e payload Qdrant devem ser testados contra exemplos equivalentes quando existirem.
- Alteracao aditiva opcional pode manter a mesma versao se consumidores antigos ignorarem o campo.
- Alteracao obrigatoria, renomeacao, remocao, mudanca de enum ou mudanca semantica cria nova versao.
- Fixtures nao devem conter dados sensiveis nem texto protegido.
- OpenAPI deve ser gerado/publicado pela API, mas fixtures continuam sendo exemplos regressivos.

## Pontos OCP/LSP/IoC

Pontos de extensao fechados neste portao:

- `source_connector` e `source_locator` para fontes futuras;
- adapter de vector store com payload minimo independente de Qdrant;
- contrato versionado Spring -> worker;
- provider de embeddings/LLM/VLM substituivel;
- envelope de evento versionado;
- tool schema MCP derivado de contratos backend, sem acesso direto a SQL/Qdrant/storage.

Invariantes LSP:

- adapters de vector store preservam filtros obrigatorios e retorno de point ids;
- worker real e worker mockado retornam o mesmo formato de erro e contrato;
- provider de embeddings real e mockado preservam dimensao declarada por modelo;
- vision interpreter real e mockado preservam `provider`, `model`, `promptVersion`, `inputHash`, `confidence` e erro estruturado;
- repositories e queries sempre preservam workspace scope.

IoC/DI:

- application/domain dependem de ports locais;
- Spring configuration liga repositories, worker client, vector adapter, storage e event publisher;
- FastAPI dependencies ligam extratores, OCR, render, vision interpreter e embeddings;
- setup MCP liga tools a clientes backend, nao a infraestrutura direta.

## Checklist de cobertura do MVP

| Fluxo | Cobertura neste contrato |
| --- | --- |
| Bootstrap e login | `users`, `auth_identities`, `auth_sessions`, `workspaces`, `workspace_memberships`. |
| Criar colecao | `knowledge_collections`, settings e roles. |
| Upload PDF | `documents`, `assets`, `ingest_runs`. |
| Revisar paginas | `document_pages`, `page_numbering_anchors`. |
| Interpretar imagens/tabelas | `document_elements`, `assets`, `visual_interpretations`. |
| Chunking | `chunks`, `chunk_relations`. |
| Embeddings e Qdrant | `chunk_embeddings`, `vector_index_bindings`, payload Qdrant. |
| Busca e laboratorio | `chunks`, Qdrant payload, feedback e eventos. |
| Citacoes e preview | `source_locator`, pages, elements, assets e chunks. |
| Analytics local | `analytics_events`, `retrieval_feedback`. |
| MCP/API | schemas derivados de REST/backend, role `agent`, eventos `origin=mcp`. |

## Pendencias encaminhadas

Estas decisoes ficam fora deste portao e tem branch dona:

- transicoes completas da state machine, retry/cancel, idempotencia e storage layout: `docs/arquitetura-ingestao-rag`;
- service accounts completas e convites multiusuario: fora do MVP inicial; API keys reais entram em `feat/api-rag-publica` seguindo `docs/seguranca-permissoes-mvp`;
- algoritmos de numeracao, chunking, contexto, ranking e budgets numericos: `docs/algoritmos-rag-mvp`;
- taxonomia final de eventos, retencao e dashboards: `docs/analytics-observabilidade-mvp`;
- detalhes finais de provider visual, privacy budget e fixtures visuais: `docs/interpretacao-imagens-tabelas-pdf`;
- dependencia concreta de OpenAPI no `pom.xml`: `chore/backend-spring-base`, validando compatibilidade real com Spring Boot 4.1.
