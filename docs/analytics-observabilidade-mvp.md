# Analytics e Observabilidade do MVP

## Objetivo

Este documento fecha o portao `docs/analytics-observabilidade-mvp`.

Ele define a taxonomia versionada de eventos locais, a separacao entre eventos, logs de run e auditoria minima, as regras de privacidade, retencao, export/delete, metricas e dashboard inicial.

Ele nao implementa codigo, migrations, endpoints ou dashboard real. As branches de implementacao devem transformar estas regras em schema, persistencia, agregadores, UI, testes e fixtures versionadas.

## Fontes e skills ativadas

- Documentacao local: `docs/modelo-dados-contratos-mvp.md`, `docs/arquitetura-ingestao-rag.md`, `docs/seguranca-permissoes-mvp.md`, `docs/algoritmos-rag-mvp.md`, `docs/analytics-local-rag.md`, `docs/padroes-de-projeto.md`, `docs/estrategia-de-testes.md`, `docs/skills-e-boas-praticas-para-agentes.md` e ADRs.
- Arquivos operacionais: `branches-plan/01e-docs-analytics-observabilidade-mvp.md` e planos das branches `feat/workspace-settings-mvp`, `feat/analytics-eventos-base`, `feat/analytics-dashboard-base`, `feat/api-rag-publica` e `feat/mcp-tools-base`.
- Skills externas: nenhuma skill externa foi ativada nesta branch, porque a decisao e de contrato local. Telemetria remota, tracing externo ou ferramenta concreta de observabilidade ficam fora do MVP e exigem decisao propria futura.

## Decisoes principais

| Tema | Decisao |
| --- | --- |
| Escopo | Analytics local faz parte do MVP; telemetria remota fica fora. |
| Evento base | Usar envelope `analytics.event.v1`, com `eventName` canonico em `snake_case`. |
| Workspace | Todo evento persistido deve carregar `workspaceId` ou ser explicitamente marcado como evento pre-workspace de bootstrap. |
| Origens | `ui`, `api`, `worker`, `mcp` e `system`. Eventos MCP so passam a ser emitidos quando `feat/mcp-tools-base` entrar. |
| Conteudo | Por padrao, eventos gravam metadados e buckets, nao texto integral, prompt, resposta completa, PDF bruto ou embedding. |
| Historico local | Perguntas/respostas podem ser guardadas apenas quando o workspace habilitar historico local. Mesmo assim, eventos guardam referencia, nao conteudo bruto. |
| Run logs | Logs de run diagnosticam etapas e erros; analytics alimenta metricas e dashboard; auditoria minima registra acoes sensiveis. |
| Retencao | Retencao padrao por classe: analytics 90 dias, historico 30 dias, run log 30 dias e auditoria minima 365 dias. |
| Export | JSONL e CSV compactados, com manifesto versionado, filtros, contagens e redacoes aplicadas. |
| Delete | Limpeza de analytics remove eventos produto/historico do workspace, mas preserva evento de exclusao e auditoria minima ate a retencao propria. |
| Dashboard | Dashboard MVP e operacional: falhas, latencia, runs, recuperacao, contexto, resposta, feedback, visual/tabelas, API e MCP. |

## Trilhas de observabilidade

O MVP usa trilhas diferentes para evitar misturar diagnostico, produto e seguranca.

| Trilha | Uso | Persistencia | Retencao padrao | Exemplo |
| --- | --- | --- | --- | --- |
| Analytics event | Alimentar dashboard e metricas locais | `analytics_events` | 90 dias | `retrieval_query_executed` |
| Query/history record | Guardar pergunta/resposta local quando habilitado | tabela propria futura ou JSONB controlado | 30 dias | historico do laboratorio |
| Run log | Diagnosticar uma run/etapa de ingestao | `ingest_run_logs` | 30 dias | erro por pagina no OCR |
| Audit minimum | Registrar acoes sensiveis ate haver audit trail propria | `analytics_events` com classe `audit_minimum` | 365 dias | `security_permission_denied` |
| Technical log | Depurar processo local | log de aplicacao | configuracao operacional | stack trace sem conteudo sensivel |

Regras:

- run log pode ser mais detalhado que evento, mas continua seguro para UI;
- analytics event deve ter payload pequeno, versionado e agregavel;
- auditoria minima usa o mesmo envelope no MVP, mas com `retentionClass=audit_minimum`;
- log tecnico nao e fonte de dashboard nem contrato publico;
- uma acao pode gerar run log e analytics event, mas cada trilha deve ter conteudo proprio.

## Envelope de evento

Contrato:

```text
analytics.event.v1
```

Exemplo canonico:

```json
{
  "eventVersion": "analytics.event.v1",
  "eventName": "retrieval_query_executed",
  "origin": "api",
  "retentionClass": "analytics",
  "workspaceId": "wsp_fixture_alpha",
  "actor": {
    "type": "user",
    "id": "usr_fixture_owner"
  },
  "correlationId": "req_fixture_001",
  "occurredAt": "2026-06-25T12:00:00Z",
  "resource": {
    "type": "collection",
    "id": "col_fixture_rag"
  },
  "properties": {
    "collectionId": "col_fixture_rag",
    "queryMode": "metadata_only",
    "topK": 12,
    "retrievedChunksCount": 8,
    "lowConfidence": false,
    "durationMs": 184,
    "durationBucket": "100_250_ms"
  }
}
```

Campos:

| Campo | Obrigatorio | Regra |
| --- | --- | --- |
| `eventVersion` | Sim | Valor inicial `analytics.event.v1`. |
| `eventName` | Sim | `snake_case`, prefixado pelo fluxo quando necessario. |
| `origin` | Sim | `ui`, `api`, `worker`, `mcp` ou `system`. |
| `retentionClass` | Sim | `analytics`, `history` ou `audit_minimum`; run logs ficam fora de `analytics_events`. |
| `workspaceId` | Sim | ID publico do workspace; `null` apenas para bootstrap antes de existir workspace. |
| `actor.type` | Sim | `user`, `agent`, `service_account` ou `system`. |
| `actor.id` | Condicional | ID publico quando houver ator persistido; `null` para `system`. |
| `correlationId` | Sim | Propagado entre UI/API/worker/MCP. |
| `occurredAt` | Sim | ISO 8601 UTC do momento do fato, nao do insert. |
| `resource.type` | Condicional | Tipo do recurso principal quando existir. |
| `resource.id` | Condicional | ID publico do recurso principal quando existir. |
| `properties` | Sim | Objeto pequeno, seguro e validado por evento. |

Regras de naming:

- `eventName` usa `snake_case` sem ponto.
- Eventos de seguranca canonicos usam prefixo `security_`.
- Eventos de workspace/configuracao usam prefixo `workspace_`.
- Eventos de MCP usam prefixo `mcp_`, mas so sao emitidos quando o runtime MCP existir.
- Mudanca quebradora no envelope cria `analytics.event.v2`; mudanca apenas aditiva em `properties` pode manter a versao se consumidores antigos ignorarem o campo.

## Propriedades e cardinalidade

Regras para `properties`:

- chaves em `camelCase`;
- valores permitidos: string curta, numero, boolean, enum, lista curta de enums ou objeto pequeno;
- profundidade maxima recomendada: 2 niveis;
- tamanho maximo inicial: 8 KiB para `properties` e 16 KiB para o evento completo;
- IDs devem ser publicos, nunca UUID interno;
- timestamps adicionais devem ser ISO 8601 UTC;
- duracoes usam `durationMs` e, quando agregadas, `durationBucket`;
- contagens usam sufixo `Count`;
- booleanos devem ter nome afirmativo, como `lowConfidence`, `budgetHit`, `success`;
- texto livre deve ser evitado; quando indispensavel, usar codigo fechado ou mensagem segura curta.

Buckets iniciais:

| Tipo | Buckets |
| --- | --- |
| Duracao | `lt_100_ms`, `100_250_ms`, `250_500_ms`, `500_1000_ms`, `1_3_s`, `3_10_s`, `gt_10_s` |
| Paginas | `1_5`, `6_20`, `21_100`, `101_500`, `gt_500` |
| Tokens | `lt_1k`, `1k_4k`, `4k_8k`, `8k_16k`, `gt_16k` |
| Arquivo | `lt_1mb`, `1_10mb`, `10_50mb`, `50_200mb`, `gt_200mb` |

Campos de alta cardinalidade:

- podem existir como `resource.id` ou IDs publicos necessarios para filtro;
- nao devem aparecer como labels de dashboard sem agregacao;
- paths locais, nomes completos de arquivo e connection strings ficam proibidos;
- hashes podem ser usados em contratos internos, mas analytics deve preferir contagens, buckets e resource id publico.

## Privacidade e campos proibidos

Campos proibidos em eventos, run logs, audit minimum e logs tecnicos versionados:

- senha, hash de senha, token, cookie, CSRF token, API key e header `Authorization`;
- connection string, endpoint privado com credencial, segredo de provider e chave de modelo;
- PDF bruto, imagem/crop bruto, render de pagina ou binario;
- embedding vector;
- texto integral de documento, chunk completo ou pagina completa por padrao;
- prompt completo de LLM/VLM;
- resposta completa quando o historico local estiver desligado;
- pergunta completa quando o historico local estiver desligado;
- stack trace com path local sensivel, segredo ou conteudo de documento;
- IP bruto e user-agent bruto; se necessario, usar hash local ou bucket seguro.

Campos permitidos por padrao:

- IDs publicos de workspace, colecao, documento, run, chunk, evento e tool;
- enums de perfil, politica, origem, stage, status e erro;
- contagens, duracoes, buckets, limites atingidos e flags;
- `errorCode` fechado e `safeMessage` curta;
- `sourceType`, `contentKind`, `chunkingVersion`, `vectorSpace`, `provider` e `model` quando nao carregarem segredo.

## Modos de historico local

Configuracao de workspace:

| Modo | Comportamento |
| --- | --- |
| `metadata_only` | Padrao. Eventos guardam metadados, contagens, citacoes e feedback sem pergunta/resposta bruta. |
| `local_history` | Guarda perguntas, respostas, fontes e citacoes localmente em registro de historico separado; eventos guardam apenas `historyId` e metricas. |
| `analytics_off` | Desliga eventos produto opcionais; run logs, erros operacionais e auditoria minima continuam quando necessarios para seguranca e diagnostico. |

Regras:

- o usuario deve conseguir alterar o modo em settings do workspace;
- mudanca de modo gera `workspace_analytics_settings_updated`;
- `local_history` nunca autoriza gravar segredo, embedding, prompt completo ou documento integral;
- resposta RAG com `insufficient_evidence` pode registrar status e motivo mesmo em `metadata_only`;
- feedback explicito do usuario fica permitido em `metadata_only`, mas notas textuais devem ter limite e aviso de privacidade.

## Taxonomia de eventos

### Seguranca, workspace e settings

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `security_bootstrap_completed` | `api` | `workspace` | `userCreated`, `workspacePurpose` |
| `security_login_succeeded` | `api` | `user` | `sessionCreated`, `authProvider` |
| `security_login_failed` | `api` | `user` opcional | `reason`, `rateLimited` |
| `security_logout_succeeded` | `api` | `user` | `sessionRevoked` |
| `security_permission_denied` | `api` | recurso negado | `permission`, `decision`, `reason` |
| `security_csrf_rejected` | `api` | request | `method`, `routeId`, `reason` |
| `security_api_key_created` | `api` | `api_key` | `capabilities`, `expiresAtPresent` |
| `security_api_key_revoked` | `api` | `api_key` | `reason` |
| `security_agent_tool_denied` | `api` | `mcp_tool` | `toolName`, `capability`, `reason`, `limitHit` |
| `workspace_settings_updated` | `api` | `workspace` | `changedSections`, `actorRole` |
| `workspace_analytics_settings_updated` | `api` | `workspace` | `historyMode`, `retentionDays`, `changedByRole` |
| `workspace_analytics_exported` | `api` | `workspace` | `format`, `eventCount`, `includedSets`, `dateRangeBucket` |
| `workspace_analytics_deleted` | `api` | `workspace` | `deletedClasses`, `deletedEventCount`, `preservedAuditCount` |

### Colecoes e documentos

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `collection_created` | `api` | `collection` | `purpose`, `defaultIngestionProfile`, `defaultContextPolicy` |
| `collection_updated` | `api` | `collection` | `changedFields` |
| `collection_archived` | `api` | `collection` | `documentsCountBucket` |
| `document_upload_started` | `api` | `document` | `mimeType`, `fileSizeBucket`, `collectionId` |
| `document_upload_completed` | `api` | `document` | `fileSizeBucket`, `sourceType`, `durationMs` |
| `document_upload_failed` | `api` | `document` opcional | `errorCode`, `fileSizeBucket`, `retryable` |
| `document_archived` | `api` | `document` | `collectionId`, `hasActiveRun` |

### Ingestao e run

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `ingest_profile_selected` | `ui` | `collection` | `ingestProfile`, `ocrMode`, `visualExtractionEnabled` |
| `ingest_run_started` | `api` | `ingest_run` | `documentId`, `profile`, `stageCount` |
| `ingest_stage_started` | `api` ou `worker` | `ingest_run` | `stageName`, `attempt` |
| `ingest_stage_completed` | `worker` | `ingest_run` | `stageName`, `attempt`, `durationMs`, `success` |
| `ingest_stage_failed` | `worker` | `ingest_run` | `stageName`, `attempt`, `errorCode`, `retryable`, `affectedPage` |
| `ingest_stage_skipped` | `api` | `ingest_run` | `stageName`, `reason` |
| `ingest_run_waiting_for_review` | `api` | `ingest_run` | `reason`, `warningsCount` |
| `ingest_run_completed` | `api` | `ingest_run` | `durationMs`, `chunksCount`, `pagesCount`, `warningsCount` |
| `ingest_run_failed` | `api` | `ingest_run` | `errorCode`, `failedStage`, `retryable` |
| `ingest_run_cancelled` | `api` | `ingest_run` | `cancelState`, `completedStagesCount` |
| `ingest_run_retry_started` | `api` | `ingest_run` | `retryOfRunId`, `failedStage`, `profile` |
| `ingest_reindex_started` | `api` | `ingest_run` | `reprocessOfRunId`, `reason`, `embeddingModelVersion` |

### Mapa de paginas

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `page_map_opened` | `ui` | `document` | `pageCountBucket`, `needsReview` |
| `page_numbering_anchor_created` | `api` | `document` | `filePageNumber`, `numberingStyle`, `applyDirection` |
| `page_numbering_segment_created` | `api` | `document` | `numberingStyle`, `segmentPageCount` |
| `page_marked_ignored` | `api` | `page` | `pageRole`, `includeInSearch` |
| `page_marked_not_counted` | `api` | `page` | `pageRole`, `includeInNumbering` |
| `page_numbering_conflict_detected` | `api` | `document` | `conflictsCount`, `blocking` |
| `page_numbering_review_completed` | `api` | `document` | `anchorsCount`, `segmentsCount`, `ignoredPagesCount` |

### Visual, tabelas e OCR

Os detalhes finais de provider visual, assets e tabelas ficam em `docs/interpretacao-imagens-tabelas-pdf`, mas a taxonomia reserva os eventos abaixo.

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `ocr_pages_completed` | `worker` | `ingest_run` | `pagesProcessedCount`, `pagesFailedCount`, `durationMs` |
| `visual_elements_extracted` | `worker` | `document` | `tablesCount`, `figuresCount`, `imagesCount`, `durationMs` |
| `visual_interpretation_completed` | `worker` | `document_element` | `provider`, `model`, `promptVersion`, `durationMs`, `confidenceBucket` |
| `visual_interpretation_failed` | `worker` | `document_element` | `provider`, `model`, `errorCode`, `retryable` |
| `visual_budget_limit_hit` | `worker` | `ingest_run` | `provider`, `limitType`, `itemsSkippedCount` |
| `table_extracted` | `worker` | `document_element` | `rowsBucket`, `columnsBucket`, `confidenceBucket` |

### Chunking, embeddings e indexacao

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `chunking_completed` | `worker` | `document` | `chunkingVersion`, `chunksCount`, `averageTokens`, `durationMs` |
| `chunking_failed` | `worker` | `document` | `chunkingVersion`, `errorCode`, `retryable` |
| `embedding_batch_completed` | `worker` ou `api` | `collection` | `embeddingModelVersion`, `batchSize`, `dimension`, `durationMs` |
| `embedding_batch_failed` | `worker` ou `api` | `collection` | `embeddingModelVersion`, `itemsFailedCount`, `errorCode`, `retryable` |
| `vector_indexing_completed` | `api` | `collection` | `vectorSpace`, `pointsUpsertedCount`, `durationMs` |
| `vector_indexing_failed` | `api` | `collection` | `vectorSpace`, `errorCode`, `retryable` |

### Busca, contexto, resposta e feedback

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `retrieval_lab_opened` | `ui` | `collection` | `hasDocuments`, `hasChunks` |
| `retrieval_query_executed` | `api` | `collection` | `topK`, `retrievedChunksCount`, `lowConfidence`, `durationMs` |
| `retrieval_result_marked_relevant` | `api` | `chunk` | `queryEventId`, `rank`, `scoreBucket` |
| `retrieval_result_marked_irrelevant` | `api` | `chunk` | `queryEventId`, `rank`, `scoreBucket` |
| `retrieval_context_expanded` | `api` | `collection` | `contextPolicy`, `expandedChunksCount`, `tokenEstimate`, `budgetHit` |
| `context_budget_limit_hit` | `api` | `collection` | `contextPolicy`, `tokenBudget`, `droppedItemsCount` |
| `retrieval_answer_generated` | `api` | `collection` | `answerProvider`, `status`, `citationsCount`, `durationMs` |
| `retrieval_answer_insufficient_evidence` | `api` | `collection` | `reason`, `retrievedChunksCount`, `lowConfidence` |
| `retrieval_answer_marked_useful` | `api` | `collection` | `answerEventId`, `citationsCount` |
| `retrieval_answer_marked_not_useful` | `api` | `collection` | `answerEventId`, `reason` |
| `citation_preview_opened` | `api` | `chunk` | `contentKind`, `previewAvailable`, `fallbackTextAvailable` |

### API e MCP

| Evento | Origem | Recurso | Propriedades minimas |
| --- | --- | --- | --- |
| `api_rag_query_executed` | `api` | `collection` | `capability`, `topK`, `contextBudget`, `status`, `durationMs` |
| `api_rate_limit_hit` | `api` | `api_key` | `capability`, `limitType`, `retryAfterBucket` |
| `mcp_server_enabled` | `api` | `workspace` | `transport`, `enabledToolsCount` |
| `mcp_tool_invoked` | `mcp` | `mcp_tool` | `toolName`, `capability`, `success`, `durationMs`, `resultCount` |
| `mcp_tool_failed` | `mcp` | `mcp_tool` | `toolName`, `capability`, `errorCode`, `retryable` |
| `mcp_context_budget_exceeded` | `mcp` | `mcp_tool` | `toolName`, `contextBudget`, `requestedBudget`, `itemsDroppedCount` |
| `mcp_agent_loop_detected` | `mcp` | `mcp_tool` | `toolName`, `windowSeconds`, `toolCallCount`, `decision` |

## Retencao

Classes de retencao:

| Classe | Conteudo | Padrao | Configuravel |
| --- | --- | --- | --- |
| `analytics` | Eventos produto e metricas agregaveis | 90 dias | 7, 30, 90, 180, 365 dias ou ate delete do workspace |
| `history` | Perguntas/respostas locais quando habilitadas | 30 dias | 7, 30, 90, 180 dias ou desativado |
| `run_log` | Diagnostico de run/etapa | 30 dias | 7, 30, 90 dias |
| `audit_minimum` | Acoes sensiveis e seguranca | 365 dias | 90, 180, 365 dias; nao menor que 90 no MVP |

Regras:

- retencao e configuracao de workspace, aplicada por job local;
- limpeza deve filtrar por `workspaceId` e classe;
- eventos de `audit_minimum` nao sao removidos por "limpar analytics" comum;
- `workspace_analytics_deleted` e `analytics_retention_applied` ficam preservados ate a retencao de auditoria minima;
- delete de workspace apaga ou anonimiza analytics do workspace conforme politica de exclusao de dados da branch que implementar workspace delete;
- falha na retencao gera log tecnico e evento seguro `analytics_retention_failed`.

Eventos de manutencao:

| Evento | Propriedades |
| --- | --- |
| `analytics_retention_applied` | `retentionClass`, `deletedEventCount`, `olderThanDays`, `durationMs` |
| `analytics_retention_failed` | `retentionClass`, `errorCode`, `retryable` |

## Export

Formatos MVP:

- JSONL como formato canonico de eventos;
- CSV como formato auxiliar para planilhas;
- manifesto JSON versionado;
- pacote `.zip` quando houver mais de um arquivo.

Manifesto:

```json
{
  "contractVersion": "analytics.export.manifest.v1",
  "workspaceId": "wsp_fixture_alpha",
  "exportId": "exp_fixture_001",
  "createdAt": "2026-06-25T12:30:00Z",
  "format": "jsonl_zip",
  "filters": {
    "from": "2026-06-01T00:00:00Z",
    "to": "2026-06-25T23:59:59Z",
    "origins": ["api", "worker"],
    "eventNames": ["ingest_stage_completed", "retrieval_query_executed"]
  },
  "includedSets": ["events", "feedback"],
  "redactions": ["prohibited_fields_removed", "history_content_excluded"],
  "counts": {
    "events": 42,
    "feedback": 3
  }
}
```

Regras:

- exige permissao `analytics.export`;
- export sempre filtra por workspace antes de aplicar outros filtros;
- export de `history` so inclui perguntas/respostas se `local_history` estiver habilitado e o usuario confirmar;
- CSV achata `properties` com prefixo `properties.` e omite objetos que nao couberem de forma segura;
- o manifesto registra filtros, datasets incluidos, redacoes e contagens;
- export gera evento `workspace_analytics_exported` sem nome de arquivo local, caminho ou conteudo.

## Delete

Endpoint planejado:

```text
DELETE /api/v1/workspaces/{workspaceId}/analytics/events
```

Regras:

- exige permissao `analytics.delete`;
- remove classes `analytics` e `history` por padrao;
- pode incluir `run_log` apenas quando o usuario escolher limpar diagnostico operacional elegivel;
- nao remove `audit_minimum` ainda dentro da retencao;
- executa em job quando o volume passar do limite operacional;
- registra `workspace_analytics_deleted` com contagens, classe e filtros;
- deve ser idempotente por `correlationId`/request id quando reexecutado.

## Dashboard MVP

O dashboard inicial deve ser operacional e denso, nao decorativo.

Filtros:

- workspace obrigatorio;
- intervalo: `24h`, `7d` default, `30d` e custom;
- colecao opcional;
- documento opcional quando fizer sentido;
- origem opcional;
- tipo de evento opcional.

Indicadores iniciais:

| Area | Indicadores |
| --- | --- |
| Ingestao | runs iniciadas, completas, falhas, canceladas, esperando revisao e tempo mediano por etapa |
| Falhas | falhas por etapa, `errorCode`, retryable vs nao retryable |
| Paginas/OCR | paginas processadas, OCR executado, conflitos de numeracao e revisoes concluidas |
| Visual/tabelas | tabelas/imagens detectadas, interpretacoes completas/falhas e budgets atingidos |
| Chunking/indexacao | chunks criados, media de tokens, embeddings com erro, pontos indexados |
| Busca | consultas, topK medio, chunks recuperados, low confidence, sem resultados |
| Contexto | budget hit, tokens estimados, itens descartados, politica usada |
| Resposta | respostas geradas, insufficient evidence, citacoes por resposta, feedback util/inutil |
| API/MCP | chamadas por capability/tool, limite atingido, falhas, loops detectados |

Contrato de resposta inicial:

```text
analytics.dashboard.summary.v1
```

Regras:

- agregacoes devem ser calculadas por workspace;
- contagens devem funcionar com fixtures pequenas;
- painel deve ter estado vazio sem erro quando nao houver eventos;
- dashboard nao deve exibir conteudo sensivel mesmo quando `local_history` estiver habilitado;
- dados de `audit_minimum` entram apenas em cards de seguranca/guardrails, nao em graficos de produto sem contexto.

## Logs de run e erros por pagina

Run logs continuam no contrato de ingestao, mas esta branch define os limites de conteudo.

Campos permitidos:

- `runId`, `stageId`, `stageName`, `attempt`, `level`, `message`, `correlationId`;
- `filePageNumber`, `errorCode`, `retryable`, `durationMs`;
- `safeDetails` com enums, contagens, versoes de contrato e IDs publicos.

Campos proibidos:

- texto integral extraido da pagina;
- imagem/render/crop;
- OCR bruto;
- prompt completo;
- stack trace com conteudo sensivel;
- path local absoluto quando expuser usuario, workspace ou segredo.

Erros por pagina:

- devem usar `filePageNumber` 1-based;
- devem ter `errorCode` fechado;
- podem aparecer agregados no dashboard por etapa/pagina;
- devem preservar diagnostico suficiente para retry manual sem expor documento inteiro.

## OCP, LSP e IoC

Pontos de extensao:

- `EventPublisher` para publicar eventos locais;
- `EventSanitizer` para remover campos proibidos antes de persistir;
- `EventSchemaRegistry` para validar `eventName` e propriedades;
- `EventSink` para persistencia local e futura telemetria opt-in;
- `RunLogWriter` para diagnostico de runs;
- `AuditPublisher` para auditoria minima e audit trail futuro;
- `RetentionPolicy` por classe;
- `AnalyticsExporter` por formato;
- `DashboardAggregator` por painel/indicador.

Invariantes LSP:

- todo sink preserva workspace scope, `correlationId`, ordem temporal e campos obrigatorios;
- sanitizers nunca deixam passar segredo, embedding, PDF bruto, prompt completo ou conteudo desligado por configuracao;
- exporters diferentes retornam o mesmo conjunto logico filtrado, mudando apenas formato;
- agregadores nunca misturam workspaces;
- telemetria remota futura, se existir, deve ser opt-in, substituivel e mais restritiva que o sink local por padrao.

IoC:

- use cases recebem `EventPublisher`, `RunLogWriter` e `AuditPublisher` como ports locais;
- Spring configuration liga sinks, sanitizers, agregadores, retencao e exportadores;
- FastAPI dependencies ligam emissor de metricas do worker sem acessar banco diretamente;
- MCP setup liga emissor de eventos ao cliente backend, sem persistencia local propria;
- UI consome endpoints de agregacao e nao calcula autorizacao.

## Testabilidade obrigatoria

Fixtures adicionadas ou esperadas:

- `tests/contracts/events/analytics-event.v1.json`;
- `tests/contracts/events/ingest-stage-completed.v1.json`;
- `tests/contracts/events/retrieval-query-executed.v1.json`;
- `tests/contracts/events/security-permission-denied.v1.json`;
- `tests/contracts/events/mcp-tool-invoked.v1.json`;
- `tests/contracts/analytics/dashboard-summary.response.v1.json`;
- `tests/contracts/analytics/export-manifest.v1.json`;
- `tests/contracts/analytics/retention-policy.v1.json`.

Testes futuros por branch:

| Branch | Casos obrigatorios |
| --- | --- |
| `feat/workspace-settings-mvp` | modo de historico, retencao e permissoes para alterar settings. |
| `feat/analytics-eventos-base` | schema, sanitizer, workspace scope, retencao, export/delete e eventos invalidos descartados. |
| `feat/analytics-dashboard-base` | agregacoes por fixture, estado vazio, filtros e ausencia de conteudo sensivel. |
| `feat/ingestao-runs-operacao` | run logs seguros, eventos de run/stage e erros por pagina. |
| `feat/laboratorio-recuperacao` | eventos de busca, contexto, resposta e feedback. |
| `feat/api-rag-publica` | eventos de API, rate/limite, capability e workspace scope. |
| `feat/mcp-tools-base` | eventos de tool, falhas, budget excedido, loop simples e capability negada. |

Checks minimos:

- todo evento valido passa no schema;
- evento com campo proibido e rejeitado ou sanitizado;
- workspace A nao exporta/agrega eventos do workspace B;
- delete de analytics preserva `audit_minimum`;
- dashboard com zero eventos retorna contagens zeradas, nao erro;
- CSV/JSONL exportam o mesmo conjunto filtrado.

## Pendencias encaminhadas

- Migrations e tabelas concretas de analytics: `feat/analytics-eventos-base`.
- UI, graficos e componentes reais do dashboard: `feat/analytics-dashboard-base`.
- Settings de workspace para historico e retencao: `feat/workspace-settings-mvp`.
- Eventos MCP reais, transporte e schemas finais de tools: `feat/mcp-tools-base`.
- API key real, rate limiting e eventos de cliente externo: `feat/api-rag-publica`.
- Provider visual, privacy budget e propriedades finais de eventos visuais: `docs/interpretacao-imagens-tabelas-pdf`.
- Telemetria remota: fora do MVP; exigir ADR propria, opt-in explicito e proibicao de conteudo.
