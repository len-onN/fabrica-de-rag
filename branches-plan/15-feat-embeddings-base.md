# feat/embeddings-base

Status: candidata.

## Objetivo

Implementar contrato inicial de embeddings definido no planejamento.

## Tags

CODE, CONTRACT, RUNTIME, TEST, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Conectores vector store](../docs/conectores-vector-store.md), [feat/chunking-semantico](14-feat-chunking-semantico.md).
- Contratos/fixtures: [worker embeddings](../tests/contracts/worker/embeddings-text.response.v1.json), [Qdrant payload](../tests/contracts/qdrant/chunk-payload.v1.json).

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
- Contrato definido em `docs/algoritmos-rag-mvp.md` como `text_embedding_provider_v1`.
- Testes e e2e usam `mock-text-embedding-v1`, deterministico, dimensao 16, metrica `cosine` e vector space `text_chunks_v1`.

## Falta definir

- Provider real, se entrar nesta branch.
- Parametros finos do adapter real escolhido.

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
