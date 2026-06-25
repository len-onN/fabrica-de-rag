# perf/ingestao-e-contexto

Status: candidata.

## Objetivo

Medir e ajustar gargalos reais de ingestao e contexto.

## Tags

PERF, CODE, RUNTIME, TEST, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [Conectores vector store](../docs/conectores-vector-store.md), [test/e2e-mvp-ingestao-recuperacao](25-test-e2e-mvp-ingestao-recuperacao.md).
- Contratos/fixtures: [contracts](../tests/contracts), [fixtures](../tests/fixtures).

## Escopo

- Medir tempos de ingestao, chunking, embeddings, Qdrant e context builder.
- Identificar gargalos.
- Corrigir apenas gargalos observados.
- Registrar antes/depois.

## Fora de escopo

- Micro-otimizacoes especulativas.
- Reescritas amplas sem medicao.

## Definido

- Medir antes de otimizar.
- Performance deve preservar legibilidade e testes.

## Falta definir

- Fixtures de benchmark.
- Metricas alvo.
- Limites aceitaveis por etapa.

## Estrategia

1. Criar benchmark pequeno.
2. Medir baseline.
3. Corrigir gargalos claros.
4. Registrar antes/depois.

## Testabilidade

- Benchmark reproduzivel.
- Testes de regressao continuam passando.

## Fechamento

- Performance melhora ou risco fica documentado com dados.
