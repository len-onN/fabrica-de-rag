# Algoritmos RAG do MVP

## Objetivo

Este documento fecha o portao `docs/algoritmos-rag-mvp`.

Ele especifica os algoritmos e contratos operacionais para numeracao impressa, chunking, embeddings textuais, busca vetorial, montagem de contexto, citacoes, budgets e politica de resposta RAG.

Ele nao implementa codigo, nao escolhe um provider remoto obrigatorio e nao cria schema final gerado por stack. As branches de implementacao devem transformar estas regras em use cases, ports, adapters, DTOs, modelos Pydantic, schemas MCP, testes e fixtures versionadas.

## Fontes e skills ativadas

- Documentacao local: `docs/modelo-dados-contratos-mvp.md`, `docs/arquitetura-ingestao-rag.md`, `docs/seguranca-permissoes-mvp.md`, `docs/mvp-interface-e-api.md`, `docs/padroes-de-projeto.md`, `docs/estrategia-de-testes.md`, `docs/skills-e-boas-praticas-para-agentes.md` e ADRs.
- Arquivos operacionais: `branches-plan/01d-docs-algoritmos-rag-mvp.md` e planos das branches `feat/paginas-numeracao`, `feat/chunking-semantico`, `feat/embeddings-base`, `feat/busca-vetorial-base`, `feat/context-builder-base`, `feat/resposta-rag-base`, `feat/citacoes-preview-fonte`, `feat/api-rag-publica` e `feat/mcp-tools-base`.
- Skills externas: nenhuma skill externa foi ativada nesta branch, porque a decisao e de contrato local. Provider/modelo real de embedding ou LLM fica atras de adapter e pode ativar fonte oficial propria quando a branch de implementacao escolher uma dependencia concreta.

## Decisoes principais

| Tema | Decisao |
| --- | --- |
| Numeracao | Usar `page_numbering_anchor_segments_v1`, com ancoras revisaveis e segmentos deterministicos. |
| Chunking | Usar `semantic_block_v1`, baseado em blocos normalizados, limite por tokens estimados, overlap controlado e proveniencia obrigatoria. |
| Embeddings | Usar contrato `text_embedding_provider_v1`; testes e fixtures usam `mock-text-embedding-v1`, deterministico, dimensao 16 e metrica `cosine`. |
| Busca | Qdrant retorna ancoras filtradas por workspace/colecao; SQL revalida permissao, status, soft delete e enriquece resposta. |
| Contexto | Usar `context_builder_v1`, com politicas `conservative` e `sequential`, budget deterministico e citacoes preservadas. |
| Visual/tabelas | Interpretacoes visuais e tabelas entram como texto derivado citavel, sem embeddings visuais no MVP. |
| Citacoes | Toda resposta ao usuario deve conseguir apontar documento, chunk, pagina fisica, pagina impressa quando houver, source locator e fallback textual. |
| Resposta RAG | LLM nao responde sem evidencia suficiente; quando nao houver contexto confiavel, retornar estado `insufficient_evidence`. |
| Providers reais | Provider remoto de embedding/LLM e opcional por configuracao futura; mock/fake e obrigatorio para testes e e2e. |
| Budgets | Limites iniciais sao pequenos, configuraveis por workspace/colecao e aplicados tambem a API/MCP. |

## Nomes e versoes

| Contrato | Valor inicial |
| --- | --- |
| Algoritmo de numeracao | `page_numbering_anchor_segments_v1` |
| Estrategia de chunking | `semantic_block` |
| Versao de chunking | `semantic_block_v1` |
| Builder de contexto | `context_builder_v1` |
| Politica conservadora | `conservative_neighbors_v1` |
| Politica sequencial | `sequential_neighbors_v1` |
| Provider mock de embedding | `mock-text-embedding` |
| Versao mock de embedding | `mock-text-embedding-v1` |
| Dimensao mock | `16` |
| Metrica inicial | `cosine` |
| Vector space textual | `text_chunks_v1` |
| Payload Qdrant | `qdrant.chunk.v1` |
| Citacao | `rag.citation.v1` |

Alteracoes quebradoras nestes contratos devem criar nova versao ou plano de migracao.

## Defaults numericos

| Uso | Default | Maximo MVP | Observacao |
| --- | --- | --- | --- |
| Chunk `fast` target | 450 tokens | 700 | Prioriza velocidade e chunks menores. |
| Chunk `fast` overlap | 40 tokens | 80 | Apenas para continuidade local. |
| Chunk `balanced` target | 700 tokens | 1000 | Default do MVP. |
| Chunk `balanced` overlap | 80 tokens | 150 | Preserva contexto sem inflar indice. |
| Tamanho minimo de chunk | 120 tokens | Nao aplicavel | Blocos menores podem ser fundidos quando seguro. |
| Tamanho maximo de item de contexto | 1200 tokens | 1600 | Item maior pode ser truncado com flag. |
| Busca laboratorio `topK` | 12 | 20 | Usuario pode reduzir/aumentar dentro do limite. |
| Busca API `topK` | 8 | 12 | Mais conservador para clientes externos. |
| Busca MCP `topK` | 8 | 12 | Alinha com fixture `mcp.search_chunks.request.v1`. |
| Vizinhos antes/depois | 2 / 2 | 4 / 4 | Politica `sequential`. |
| Budget laboratorio | 6000 tokens | 10000 | Inclui chunks, visual/tabelas e metadados textuais. |
| Budget API | 5000 tokens | 8000 | Aplicado por request autenticada. |
| Budget MCP | 4000 tokens | 6000 | Evita retorno excessivo para agentes. |
| Timeout busca | 3000 ms | 8000 ms | Sem incluir geracao LLM. |
| Timeout context builder | 2000 ms | 5000 ms | Operacao local/SQL. |
| Timeout embedding batch | 10000 ms | 30000 ms | Provider real pode ajustar por config. |
| Batch embedding | 32 chunks | 96 | Mock pode processar mais, mas contrato usa este limite. |

`token` aqui e estimativa operacional. A implementacao pode usar contador aproximado ate haver tokenizer concreto, desde que seja deterministico e testado.

## Qualidade textual e OCR automatico

Nome:

```text
text_quality_gate_v1
```

Objetivo:

- decidir se a pagina tem texto nativo suficiente para indexacao inicial ou se deve entrar como candidata a OCR.

Entrada:

- `filePageNumber`;
- texto extraido por pagina;
- quantidade de blocos;
- area aproximada de texto quando disponivel;
- `pageRole`;
- `includeInSearch`.

Saida:

- `textQuality`: `empty`, `poor`, `usable`, `good`;
- `ocrRecommended`: boolean;
- `reasonCodes`: lista pequena e segura.

Invariantes:

- pagina marcada como `includeInSearch=false` nao forca OCR automatico;
- pagina `blank` nao forca OCR automatico;
- decisao automatica nunca remove texto nativo existente;
- falha de OCR nao apaga texto nativo.

Regras iniciais:

- `empty`: menos de 20 caracteres extraiveis e sem blocos relevantes;
- `poor`: menos de 80 caracteres extraiveis, ou texto com muitos caracteres quebrados, ou densidade muito baixa em pagina nao vazia;
- `usable`: texto suficiente para busca, mas com warnings;
- `good`: texto suficiente e sem warnings criticos.

`ocrMode=auto` recomenda OCR para paginas `empty` ou `poor`, exceto paginas excluidas do RAG, `blank`, `cover` sem texto relevante ou paginas acima dos limites da instancia. O usuario pode forcar OCR por pagina no mapa.

## Numeracao impressa

Nome:

```text
page_numbering_anchor_segments_v1
```

Objetivo:

- inferir `effective_printed_label` a partir de ancoras humanas, segmentos e flags de inclusao.

Entrada:

- paginas do documento ordenadas por `filePageNumber`;
- ancoras `filePageNumber`, `printedLabel`, `numberingStyle`, `applyDirection`;
- flags `includeInNumbering`;
- `pageRole`;
- labels detectados quando existirem.

Saida:

- lista de paginas com `effectivePrintedLabel`, `numberingStyle`, `numberingSource` e warnings;
- lista de segmentos;
- conflitos que exigem revisao humana.

Modelo:

- `filePageNumber` e sempre 1-based;
- um segmento e um intervalo continuo de paginas que compartilha estilo e regra de incremento;
- paginas com `includeInNumbering=false` nao consomem numero;
- paginas sem label podem ter `effectivePrintedLabel=null`;
- estilo `custom` nunca e inferido alem das paginas explicitamente ancoradas.

Regras:

- uma ancora cria ou ajusta um segmento;
- `applyDirection=forward` aplica ate proxima ancora, pagina excluida que reinicie segmento, fim do documento ou conflito;
- `applyDirection=backward` aplica ate ancora anterior, inicio do documento ou conflito;
- `applyDirection=both` combina as duas direcoes;
- labels arabicos incrementam por 1;
- labels romanos preservam estilo e incrementam por 1;
- conflito entre duas ancoras sobre a mesma pagina gera warning e bloqueia promocao automatica se afetar pagina indexavel;
- label detectado pelo worker e sugestao, nao substitui label efetivo salvo confirmacao ou ausencia de conflito.

Complexidade:

- `O(p + a log a)`, onde `p` e numero de paginas e `a` numero de ancoras.

Casos-limite obrigatorios:

- capa sem numeracao;
- sumario romano;
- corpo arabico iniciando depois da capa;
- pagina ignorada que nao conta;
- reinicio de numeracao em anexo;
- conflito entre label detectado e ancora humana;
- pagina sem render disponivel.

## Chunking semantico

Nome:

```text
semantic_block_v1
```

Objetivo:

- gerar chunks recuperaveis pequenos, ordenados e rastreaveis, sem confundir chunk de busca com contexto final.

Entrada:

- blocos textuais normalizados;
- tabelas simples extraidas;
- interpretacoes visuais textuais;
- paginas e source locators;
- heading candidates;
- perfil `fast` ou `balanced`;
- limites de target/overlap.

Saida:

- chunks com `contentKind`, `content`, `headingPath`, `tokenCount`, `sourceLocator`, `sourceHash`, `sequenceNumber`, `chunkingStrategy` e `chunkingVersion`;
- relacoes `previous` e `next`;
- relacoes diretas `caption_of_image`, `table_continuation` e `derived_from_visual` quando aplicavel.

Invariantes:

- todo chunk pertence a um workspace, colecao e documento;
- `sequenceNumber` e monotonicamente crescente por documento/generation;
- source locator nunca e perdido;
- tabelas pequenas devem ser mantidas como unidade quando couberem no limite;
- interpretacao visual e marcada como `visual_interpretation`, nao como texto canonico;
- nenhum chunk inclui texto de pagina com `includeInSearch=false`;
- chunk vazio nao e persistido.

Passos:

1. Normalizar blocos por pagina e `readingOrder`.
2. Remover headers/footers repetitivos apenas quando houver evidencia local simples.
3. Construir `headingPath` a partir de elementos `heading` e padroes de ordem.
4. Acumular blocos ate o target do perfil.
5. Fundir blocos pequenos adjacentes quando preservarem source locator coerente.
6. Quebrar bloco muito longo por paragrafos/sentencas, mantendo overlap.
7. Criar chunks especificos para tabela e interpretacao visual quando forem citaveis.
8. Criar relacoes `previous`/`next` e relacoes diretas com elementos visuais/tabelas.
9. Calcular `sourceHash` por conteudo normalizado + locators + versao.

Overlap:

- overlap e medido por tokens estimados;
- o overlap deve vir do fim do chunk anterior;
- overlap nao deve atravessar documento;
- overlap pode atravessar pagina apenas quando o texto flui naturalmente e ambas entram no RAG;
- tabelas e interpretacoes visuais nao sao duplicadas por overlap, apenas relacionadas.

Complexidade:

- `O(b)`, onde `b` e numero de blocos/elementos de entrada.

Casos-limite obrigatorios:

- texto menor que o minimo;
- paragrafo maior que o maximo;
- tabela simples maior que o limite de item;
- heading ausente;
- pagina ignorada;
- interpretacao visual sem legenda;
- documento com apenas uma pagina.

## Embeddings textuais

Nome:

```text
text_embedding_provider_v1
```

Objetivo:

- gerar vetores textuais versionados para chunks, isolando custo/rede por adapter.

Entrada:

- lista de itens `{itemId, content, contentKind, sourceHash}`;
- modelo configurado;
- vector space;
- timeout e batch size.

Saida:

- `embeddingModel`;
- `embeddingModelVersion`;
- `dimension`;
- `distanceMetric`;
- `vectorSpace`;
- vetor por item;
- erro estruturado por item/lote quando aplicavel.

Decisao MVP:

- testes e e2e usam `mock-text-embedding-v1`;
- o mock e deterministico por hash de conteudo, dimensao 16, metrica `cosine`;
- provider real e configuravel por adapter, mas nao e obrigatorio para rodar testes;
- a branch `feat/embeddings-base` pode implementar apenas mock + porta se provider real ainda nao estiver configurado;
- Qdrant collection deve ser criada com a dimensao declarada no `vector_index_binding`.

Invariantes LSP:

- mock e provider real preservam contrato de erro;
- a dimensao retornada deve bater com o binding antes de indexar;
- provider nao recebe workspace inteiro, PDF bruto ou metadados sensiveis;
- erro de embedding recuperavel nao apaga chunk.

## Busca vetorial

Nome:

```text
vector_search_v1
```

Objetivo:

- recuperar chunks ancoras por pergunta, com filtros obrigatorios e enriquecimento SQL.

Entrada:

- `workspaceId`;
- `collectionId` ou colecoes autorizadas;
- query textual;
- `topK`;
- filtros opcionais: `documentId`, `contentKind`, paginas, `payloadContractVersion`;
- identity/capabilities do ator.

Saida:

- lista ordenada de chunks ancoras;
- score bruto do vector store;
- rank;
- metadados SQL;
- citacao minima;
- metricas de latencia e filtros aplicados.

Filtros obrigatorios antes/depois do Qdrant:

- antes da busca: workspace autorizado, colecao autorizada, permissao `rag.search`;
- no Qdrant: `workspaceId`, `collectionId`, `payloadContractVersion`, `vectorSpace`;
- opcionais no Qdrant: `documentId`, `contentKind`, pagina, `chunkingVersion`;
- depois da busca: revalidar no SQL workspace, colecao, documento, status ativo, soft delete e permissao.

Ranking:

- ordenar primariamente pelo score do vector store;
- empatar por `documentId`, `filePageStart`, `sequenceNumber`;
- remover duplicados por `chunkId`;
- nao aplicar reranking no MVP;
- nao usar score minimo duro por padrao;
- marcar `lowConfidence=true` quando o melhor score vier abaixo de `0.35` ou quando houver menos de 2 resultados.

Sem resultados:

- retornar lista vazia, `lowConfidence=true`, `reason=no_results`;
- nao chamar context builder nem LLM automaticamente.

Complexidade:

- Qdrant domina a busca vetorial;
- enriquecimento SQL deve ser `O(k)` com batch por IDs, nunca N+1 por chunk.

## Context builder

Nome:

```text
context_builder_v1
```

Objetivo:

- montar contexto final a partir de chunks ancoras, vizinhos, relacoes diretas e budget.

Entrada:

- chunks ancoras ordenados;
- politica `conservative` ou `sequential`;
- `neighborBefore`;
- `neighborAfter`;
- `tokenBudget`;
- flags de incluir interpretacoes visuais/tabelas;
- permissoes do ator.

Saida:

- itens de contexto ordenados;
- itens descartados e motivo;
- `budgetHit`;
- citacoes por item;
- metricas de token estimate, chunks e relacoes.

Politicas:

### `conservative_neighbors_v1`

- inclui ancoras recuperadas;
- inclui relacao direta de tabela, legenda ou interpretacao visual quando a ancora depende dela;
- nao adiciona vizinhos por padrao, salvo parametro explicito;
- indicada para API/MCP quando o budget e menor.

### `sequential_neighbors_v1`

- inclui ancoras recuperadas;
- inclui ate `neighborBefore` e `neighborAfter` por ancora;
- inclui relacoes diretas de tabela, legenda e interpretacao visual;
- ordena cada grupo por `documentId`, `filePageNumber` e `sequenceNumber`;
- mantem grupos na ordem do rank da ancora.

Deduplicacao:

- deduplicar por `chunkId`;
- se o mesmo chunk aparecer como ancora e vizinho, manter prioridade de ancora;
- se uma interpretacao visual e uma tabela apontarem para o mesmo source locator, manter a representacao mais especifica e registrar relacao.

Budget:

- prioridade 1: metadados de citacao e chunks ancoras;
- prioridade 2: elementos/tabelas/legendas diretamente relacionados;
- prioridade 3: vizinhos mais proximos;
- prioridade 4: vizinhos mais distantes;
- item acima do maximo pode ser truncado com `truncated=true`;
- tabela deve ser preservada como unidade quando couber; se nao couber, usar resumo estruturado e marcar truncamento;
- nunca remover source locator para economizar tokens.

Complexidade:

- `O(k * n + r)`, onde `k` e numero de ancoras, `n` vizinhos por lado e `r` relacoes diretas carregadas em batch.

## Citacoes e preview de fonte

Contrato:

```text
rag.citation.v1
```

Campos minimos:

- `citationId`;
- `documentId`;
- `chunkId`;
- `sourceElementId` quando houver;
- `assetId` quando houver;
- `sourceType`;
- `sourceLocator`;
- `filePageNumber`;
- `printedLabel`;
- `sourceLabel`;
- `quote`;
- `contentKind`;
- `previewAvailable`;
- `fallbackTextAvailable`.

Regras:

- `sourceLabel` segue o formato `PDF p. X / impresso p. Y` quando houver pagina impressa;
- se nao houver pagina impressa, usar `PDF p. X`;
- citacao de tabela/imagem deve preservar `bbox` e `sourceElementId` quando existirem;
- preview usa render de pagina quando disponivel;
- sem render, usar fallback textual com aviso;
- workspace scope e permissao sao revalidados ao abrir preview.

## Resposta RAG

Nome:

```text
grounded_answer_policy_v1
```

Objetivo:

- gerar resposta apenas quando houver contexto suficiente e citavel.

Regras:

- `ask` sempre depende do context builder;
- LLM real ou mockado entra por `AnswerProvider`;
- testes usam provider mockado deterministico;
- resposta afirmativa precisa de pelo menos uma citacao;
- se o contexto estiver vazio ou `lowConfidence=true` sem evidencia suficiente, retornar `status=insufficient_evidence`;
- o texto da resposta nao deve esconder que faltou evidencia;
- prompt completo nao entra em analytics por padrao.

Estado sem evidencia:

```json
{
  "status": "insufficient_evidence",
  "answer": null,
  "citations": [],
  "reason": "no_reliable_context"
}
```

## Visual, tabelas e contexto

Regras:

- O contrato detalhado de elementos, assets, tabelas, bbox, source locator e vision interpreter vive em `docs/interpretacao-imagens-tabelas-pdf.md`.
- `document_elements` com `element_type=table` podem gerar chunks `table_text`;
- `visual_interpretations` podem gerar chunks `visual_interpretation`;
- uma imagem/tabela pode ser incluida no contexto por relacao direta com chunk ancora;
- embeddings visuais ficam fora do MVP;
- busca multimodal por imagem fica fora do MVP;
- texto derivado deve carregar provider, modelo, prompt version e source locator no registro de origem;
- context builder deve diferenciar texto canonico, tabela extraida e interpretacao visual derivada.

## Metricas e eventos

Metricas minimas por fluxo:

| Fluxo | Metricas |
| --- | --- |
| Numeracao | paginas processadas, ancoras, segmentos, conflitos, duracao. |
| Chunking | chunks criados, media de tokens, chunks truncados, tabelas/visuais incluidos, duracao. |
| Embeddings | batch size, dimension, provider, itens com erro, duracao. |
| Busca | topK, resultados, score maximo, low confidence, latencia Qdrant, latencia SQL. |
| Contexto | itens de contexto, tokens estimados, budget hit, descartes, duracao. |
| Ask | provider, status, citations count, tokens estimados, duracao, insufficient evidence. |
| MCP/API | capability, limites aplicados, timeout, resultados retornados, budget hit. |

Eventos finais e retencao ficam em `docs/analytics-observabilidade-mvp`, mas os algoritmos devem expor estas metricas como propriedades pequenas e seguras.

Campos proibidos continuam valendo:

- segredo, token, API key, cookie ou header de auth;
- embedding vector em log/evento;
- PDF bruto;
- texto integral de documento por padrao;
- prompt completo quando puder conter conteudo sensivel.

## Pontos OCP/LSP/IoC

Pontos de extensao:

- `PageNumberingPolicy`;
- `TextQualityPolicy`;
- `ChunkingStrategy`;
- `EmbeddingProvider`;
- `VectorSearchPort`;
- `ContextPolicy`;
- `CitationResolver`;
- `AnswerProvider`;
- `BudgetPolicy`.

Invariantes LSP:

- policies novas preservam workspace scope e source locator;
- providers reais e mockados preservam dimensao, metrica, erro estruturado e timeout;
- adapters de vector store preservam filtros obrigatorios;
- context policies nunca retornam chunk sem permissao ou sem citacao minima;
- citation resolvers sempre revalidam workspace e recurso antes do preview;
- answer providers nunca geram resposta afirmativa sem evidencias citaveis.

IoC:

- use cases dependem de ports/policies locais;
- Spring configuration liga repositorios, Qdrant adapter, providers e policies;
- FastAPI dependencies ligam chunker, embedding provider e utilitarios de PDF;
- MCP chama backend e nao acessa Qdrant, SQL, storage ou provider diretamente.

## Testabilidade obrigatoria

Fixtures adicionadas nesta branch:

- `tests/contracts/rag/page-numbering-map.v1.json`;
- `tests/contracts/worker/chunks-build.response.v1.json`;
- `tests/contracts/worker/embeddings-text.response.v1.json`;
- `tests/contracts/rag/search-response.v1.json`;
- `tests/contracts/rag/context-assemble.response.v1.json`;
- `tests/contracts/rag/citation.v1.json`;
- `tests/contracts/rag/citation-visual.v1.json`;
- `tests/contracts/rag/ask-response-insufficient-evidence.v1.json`.

Testes futuros por branch:

| Branch | Casos obrigatorios |
| --- | --- |
| `feat/paginas-numeracao` | capa sem numero, sumario romano, corpo arabico, pagina ignorada, conflito de ancoras. |
| `feat/chunking-semantico` | texto curto, texto longo, overlap, tabela, interpretacao visual, pagina ignorada. |
| `feat/embeddings-base` | mock deterministico, dimensao, erro recuperavel, batch limite. |
| `feat/busca-vetorial-base` | filtros obrigatorios, workspace errado, sem resultados, low confidence, enriquecimento SQL em batch. |
| `feat/context-builder-base` | budget pequeno, duplicado, vizinho ausente, tabela como unidade, visual relacionado. |
| `feat/resposta-rag-base` | provider mockado, citacao obrigatoria, insuficiencia de evidencia. |
| `feat/citacoes-preview-fonte` | render disponivel, fallback textual, source locator com bbox, workspace errado. |
| `feat/api-rag-publica` | limites numericos, API key/capability, resposta publica sem vazamento. |
| `feat/mcp-tools-base` | topK/budget MCP, schema fechado, capability negada, output estruturado. |

## Pendencias encaminhadas

- Provider real de embeddings e parametros concretos de dependencia: `feat/embeddings-base`.
- Provider real de LLM e prompt final: `feat/resposta-rag-base`.
- Taxonomia final de eventos, retencao e dashboard: `docs/analytics-observabilidade-mvp`.
- Provider visual, privacy budget e fixtures visuais finais: `docs/interpretacao-imagens-tabelas-pdf`.
- Implementacao de UI/preview de citacao: `feat/citacoes-preview-fonte`.
