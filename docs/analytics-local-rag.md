# Analytics Local de RAG

## Visao

A Fabrica de RAG deve incluir analytics local desde o MVP. O objetivo e ajudar o proprio usuario a entender e melhorar suas bases RAG, seus fluxos de ingestao, suas consultas, suas politicas de contexto e o comportamento de agentes que usam API ou MCP.

No MVP, os eventos ficam na instalacao local ou self-hosted. Eles nao sao enviados para um servidor central da aplicacao.

Isso torna o analytics util mesmo sem deploy em nuvem ou base ampla de usuarios: ele vira uma ferramenta de estudo, auditoria e melhoria da propria base de conhecimento.

## Decisao de escopo

MVP:

- analytics local;
- taxonomia versionada de eventos;
- eventos de UI, API e worker; MCP deve ter schema previsto e sera emitido quando as tools basicas entrarem;
- auditoria de ingestao;
- dashboard local de qualidade do RAG;
- feedback de relevancia de chunks e respostas;
- historico local de consultas quando habilitado;
- metricas de contexto, limites, erros e latencia;
- retencao configuravel.

Futuro:

- exportacao para notebooks e analise externa;
- ciencia de dados local;
- deteccao de anomalias;
- recomendacoes de presets;
- comparacao de perfis e modelos;
- telemetria remota opt-in para deploys em nuvem ou comunidade;
- estudos agregados sem conteudo privado.

## Principios

- Local-first: dados ficam na instancia do usuario.
- O usuario deve conseguir apagar historico e configurar retencao.
- O analytics deve explicar qualidade do RAG, nao vigiar pessoas.
- Eventos devem ter schema rigido e versao.
- Separar origem: UI, API, worker, MCP.
- Registrar conteudo de pergunta/resposta deve ser opcional e local.
- Embeddings, chaves, credenciais e conexoes nunca devem entrar no analytics.
- Telemetria remota fica fora do MVP.

## Valor para o usuario

O analytics local deve responder perguntas como:

- Quais documentos aparecem mais nas respostas?
- Quais documentos quase nunca sao recuperados?
- Quais perguntas falharam ou tiveram pouco contexto?
- Quais chunks foram marcados como relevantes ou irrelevantes?
- Quais politicas de contexto geram melhores respostas?
- Quando o OCR falha ou demora demais?
- Quais PDFs exigem mais correcao no mapa de paginas?
- Onde o agente MCP entra em loop ou estoura limites?
- Quais ferramentas MCP sao chamadas em sequencia?
- Quais perfis de ingestao funcionam melhor para estudo, codigo ou pesquisa?

## Eventos iniciais

### Ingestao

```text
document_upload_started
document_upload_completed
ingest_profile_selected
ingest_run_started
ingest_stage_completed
ingest_stage_failed
ingest_run_completed
ingest_run_failed
```

Campos candidatos:

```text
file_type
page_count
page_count_bucket
ingest_profile
ocr_mode
visual_extraction_enabled
stage_name
duration_ms
duration_bucket
error_code
```

### Mapa de paginas

```text
page_map_opened
page_numbering_anchor_created
page_numbering_segment_created
page_marked_ignored
page_marked_not_counted
page_numbering_review_completed
```

Campos candidatos:

```text
numbering_style
segment_count
ignored_pages_count
not_counted_pages_count
source: manual | inferred | ocr
```

### Chunking e contexto

```text
chunking_profile_selected
context_policy_selected
context_expansion_previewed
context_budget_limit_hit
```

Campos candidatos:

```text
chunking_strategy
context_policy
token_budget
neighbor_expansion_enabled
section_expansion_enabled
visual_context_enabled
```

### Laboratorio de recuperacao

```text
retrieval_lab_opened
retrieval_lab_query_executed
retrieval_result_marked_relevant
retrieval_result_marked_irrelevant
retrieval_answer_marked_useful
retrieval_answer_marked_not_useful
retrieval_context_expanded
retrieval_answer_generated
```

Campos candidatos:

```text
top_k
reranker_enabled
context_policy
retrieved_chunks_count
expanded_chunks_count
latency_ms
success
error_code
```

### API e MCP

```text
api_rag_query_executed
mcp_server_enabled
mcp_tool_invoked
mcp_tool_failed
mcp_context_budget_exceeded
mcp_agent_loop_detected
```

Campos candidatos:

```text
tool_name
origin
success
latency_ms
tool_call_count
limit_hit
error_code
```

## Conteudo local opcional

Como os dados ficam na instancia do usuario, o sistema pode oferecer uma configuracao para guardar historico de pergunta/resposta no laboratorio.

Opcoes sugeridas:

```text
Nao salvar perguntas/respostas
Salvar perguntas, respostas e fontes localmente
Salvar apenas metadados e feedback
```

Mesmo localmente, alguns campos nunca devem ser registrados:

```text
api_key
database_connection_string
embedding_vector
raw_secret
authorization_header
```

## Dashboards MVP

Dashboards locais iniciais:

- funil de ingestao;
- erros por etapa;
- documentos mais e menos recuperados;
- chunks mais citados;
- chunks marcados como irrelevantes;
- perguntas com baixo contexto;
- respostas marcadas como uteis/inuteis;
- uso de politicas de contexto;
- uso de OCR e revisao de paginas;
- imagens/tabelas interpretadas, falhas do adapter visual e budgets atingidos;
- chamadas MCP por ferramenta;
- limites atingidos por agentes;
- tempo de processamento por etapa.

## Protecao contra dados ruins

Mesmo localmente, eventos podem ser poluidos por scripts ou agentes em loop. O MVP deve incluir:

- schema validation;
- limites por sessao;
- separacao entre origem humana, API e MCP;
- deteccao simples de repeticao de ferramenta;
- descarte de eventos invalidos;
- filtros por versao da aplicacao;
- limpeza configuravel de historico.

## Telemetria remota futura

Telemetria remota nao faz parte do MVP.

Ela so deve ser considerada se houver:

- deploy em nuvem;
- distribuicao para multiplos usuarios;
- necessidade real de melhorar UX com dados agregados;
- consentimento explicito;
- transparencia sobre eventos;
- modo desligado por padrao;
- garantia de nao enviar documentos, perguntas, respostas, chunks, embeddings, prompts ou chaves.

## Estimativa de esforco

Considerando a aplicacao sem analytics como 100%, analytics local util adiciona uma estimativa de 10% a 18% ao MVP.

Telemetria remota profissional adicionaria cerca de 25% a 35% e fica fora do MVP.

Telemetria madura com ciencia de dados adicionaria 40% a 60% se fosse incluida desde o inicio, por isso fica como evolucao futura.
