# docs/algoritmos-rag-mvp

Status: candidata.

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
- Especificar formato de citacao e abertura da fonte original.
- Definir metricas de tempo, memoria, payload e tamanho de contexto.

## Fora de escopo

- Reranking avancado.
- Busca hibrida.
- Expansao por relacoes complexas.
- Embeddings visuais.

## Definido

- Chunks recuperaveis e contexto final sao conceitos diferentes.
- Citacoes devem carregar pagina fisica e impressa.
- Qdrant encontra ancoras; SQL enriquece resultado.
- Algoritmos devem privilegiar legibilidade e testes de caso-limite.

## Falta definir

- Estrutura exata de segmento de numeracao.
- Tamanho inicial de chunk por perfil.
- Politica de overlap.
- Ordem final do contexto expandido.
- Comportamento quando budget estoura.
- Regras para abrir fonte original de uma citacao.

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

- Branches de algoritmo implementam especificacoes claras e revisaveis.
