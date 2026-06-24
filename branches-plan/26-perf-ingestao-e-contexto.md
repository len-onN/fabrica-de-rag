# perf/ingestao-e-contexto

Status: candidata.

## Objetivo

Medir e ajustar gargalos reais de ingestao e contexto.

## Tags

PERF, CODE, RUNTIME, TEST, OBS

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

