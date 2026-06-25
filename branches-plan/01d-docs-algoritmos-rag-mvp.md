# docs/algoritmos-rag-mvp

Status: em fechamento.

## Objetivo

Especificar os algoritmos centrais do MVP antes das branches que implementarem numeracao, chunking, busca, contexto e citacoes.

## Tags

DOC, PERF, DATA, CONTRACT, TEST

## Escopo

- Especificar inferencia de numeracao impressa por ancoras e segmentos.
- Especificar chunking por bloco semantico.
- Especificar heading path, overlap e preservacao de ordem.
- Especificar busca vetorial, filtros obrigatorios e enriquecimento via SQL.
- Especificar context builder por vizinhos e budget.
- Especificar como elementos de imagem/tabela entram no contexto como texto derivado citavel.
- Especificar formato de citacao e abertura da fonte original.
- Definir metricas de tempo, memoria, payload e tamanho de contexto.

## Fora de escopo

- Reranking avancado.
- Busca hibrida.
- Expansao por relacoes complexas.
- Embeddings visuais.
- Busca multimodal por imagem.

## Definido

- Chunks recuperaveis e contexto final sao conceitos diferentes.
- Citacoes devem carregar pagina fisica e impressa.
- Qdrant encontra ancoras; SQL enriquece resultado.
- Algoritmos devem privilegiar legibilidade e testes de caso-limite.
- Numeracao usa `page_numbering_anchor_segments_v1`.
- Chunking usa `semantic_block_v1`.
- Testes e e2e usam `mock-text-embedding-v1`, deterministico, dimensao 16 e metrica `cosine`.
- Context builder usa `context_builder_v1`, com politicas `conservative` e `sequential`.
- Resposta RAG retorna `insufficient_evidence` quando nao houver contexto confiavel e citavel.
- Defaults iniciais: chunk `balanced` 700 tokens com overlap 80, laboratorio `topK=12`, API/MCP `topK=8`, budgets de contexto 6000/5000/4000 tokens para laboratorio/API/MCP.
- Fixtures de contrato RAG e worker foram adicionadas em `tests/contracts/rag` e `tests/contracts/worker`.

## Falta definir

- Provider real de embedding e parametros concretos de dependencia, a fechar em `feat/embeddings-base`.
- Provider real de LLM e prompt final, a fechar em `feat/resposta-rag-base`.
- Taxonomia final de eventos e retencao, a fechar em `docs/analytics-observabilidade-mvp`.
- Provider visual, privacy budget e fixtures visuais finais, a fechar em `docs/interpretacao-imagens-tabelas-pdf`.

## Detalhes que devem ficar explicitos

- Numeracao: modelo de ancora, segmento, pagina excluida da contagem, reinicio, pagina sem label e conflito entre ancoras.
- Chunking: entrada normalizada, limite de tamanho, overlap, heading path, preservacao de ordem, proveniencia e token count estimado.
- Embeddings: dimensao, metrica, modelo, versao, batch size, erro/retry e adapter mockado para testes.
- Busca: filtros obrigatorios de workspace/colecao/documento, topK, score, enriquecimento via SQL e comportamento sem resultados.
- Context builder: entrada de chunks ancora, expansao por vizinhos, deduplicacao, ordenacao final, budget e truncamento seguro.
- Elementos visuais/tabelas: regras para incluir interpretacao textual derivada, legenda, tabela estruturada e source locator sem duplicar contexto.
- Citacoes: document id, source locator, pagina fisica, pagina impressa, chunk id, trecho, fallback textual e link para preview.
- Budgets: tokens/resultados/tempo por fluxo de laboratorio, API e MCP.
- Fixtures: PDFs/textos pequenos para capa, sumario, paginas sem numeracao, chunk longo, overlap e budget pequeno.
- Pontos OCP/LSP/IoC: nova politica de chunking, novo provider de embedding, nova estrategia de ranking, novo context policy e novo citation resolver; providers reais e mockados devem preservar dimensao, metrica, erro estruturado, budget e contrato de resposta.

## Estrategia

1. Especificar entrada, saida e invariantes por algoritmo.
2. Registrar casos-limite.
3. Definir fixtures pequenas.
4. Definir complexidade esperada.
5. Atualizar branches funcionais com criterios de aceite.

## Testabilidade

- Cada algoritmo tem fixtures.
- Casos-limite cobrem capa, sumario, paginas sem numeracao, chunks longos e budget pequeno.
- Contexto final preserva citacoes.

## Fechamento

- Branches de algoritmo implementam as especificacoes de `docs/algoritmos-rag-mvp.md` e validam as fixtures versionadas.
