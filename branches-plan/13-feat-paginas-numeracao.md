# feat/paginas-numeracao

Status: candidata.

## Objetivo

Implementar mapa de paginas e numeracao impressa por ancoras.

## Tags

CODE, DATA, CONTRACT, UX, TEST, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [Design visual](../docs/design-visual.md).
- Contratos/fixtures: [mapa de numeracao](../tests/contracts/rag/page-numbering-map.v1.json), [worker](../tests/contracts/worker), [PDF fixtures](../tests/fixtures/pdfs).

## Escopo

- Criar tabelas de paginas.
- Registrar pagina fisica.
- Permitir ancora de pagina impressa.
- Inferir sequencia simples.
- Criar UI minima do mapa de paginas.
- Usar preview/render de pagina quando disponivel.
- Permitir incluir/excluir paginas do RAG e da numeracao.

## Fora de escopo

- Inferencia sofisticada multi-documento.
- OCR de numeracao.
- Curadoria visual avancada.

## Definido

- Pagina fisica e pagina impressa sao conceitos distintos.
- Citacoes devem carregar ambas quando aplicavel.
- Algoritmo definido em `docs/algoritmos-rag-mvp.md` como `page_numbering_anchor_segments_v1`.
- Segmentos usam ancoras, estilo, direcao de aplicacao e paginas excluidas da numeracao.

## Falta definir

- Fallback quando render de pagina nao estiver disponivel.

## Estrategia

1. Especificar algoritmo.
2. Criar schema.
3. Criar use case de ancora.
4. Criar inferencia simples.
5. Criar UI minima.

## Testabilidade

- Sequencia simples.
- Capa/prefacio.
- Pagina sem numeracao.
- Segmento quando incluido.
- Conflito entre ancoras.

## Fechamento

- Citacoes podem usar pagina fisica/impressa e o mapa de paginas fica revisavel.
