# feat/embeddings-base

Status: candidata.

## Objetivo

Implementar contrato inicial de embeddings definido no planejamento.

## Tags

CODE, CONTRACT, RUNTIME, TEST, PERF

## Escopo

- Implementar provider/modelo inicial ou adapter mockado definido em `docs/algoritmos-rag-mvp`.
- Registrar dimensao, metrica e versao.
- Criar interface de geracao.
- Preparar erro/retry sem travar toda ingestao.

## Fora de escopo

- Multiplos providers.
- Otimizacao de custo.
- Benchmark amplo de modelos.

## Definido

- Embedding deve ser versionado.
- Custo/rede devem ficar isolados por adapter.

## Falta definir

- Parametros finos do adapter escolhido.
- Limites de lote/timeout.

## Estrategia

1. Aplicar contrato definido no planejamento.
2. Criar adapter.
3. Criar mock deterministico para testes.
4. Persistir metadados de embedding.

## Testabilidade

- Adapter mockado.
- Contrato de dimensao.
- Erro controlado.

## Fechamento

- Chunks podem receber embedding versionado.
