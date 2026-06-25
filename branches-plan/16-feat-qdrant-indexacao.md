# feat/qdrant-indexacao

Status: candidata.

## Objetivo

Indexar embeddings no Qdrant com payload minimo e filtros obrigatorios.

## Tags

CODE, RUNTIME, DATA, CONTRACT, TEST, SEC, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Conectores vector store](../docs/conectores-vector-store.md), [build/dev-runtime-compose](03-build-dev-runtime-compose.md), [feat/embeddings-base](15-feat-embeddings-base.md).
- Contratos/fixtures: [Qdrant payload](../tests/contracts/qdrant/chunk-payload.v1.json), [worker embeddings](../tests/contracts/worker/embeddings-text.response.v1.json).

## Escopo

- Criar colecao Qdrant.
- Definir payload minimo.
- Implementar upsert/delete.
- Filtrar por workspace, colecao e documento.
- Registrar status de indexacao.

## Fora de escopo

- Conectores externos.
- Busca hibrida.
- Reranking.

## Definido

- Qdrant e indice derivado.
- SQL enriquece e governa a verdade documental.
- Filtros impedem vazamento entre workspaces.

## Falta definir

- Nome da colecao.
- Metrica vetorial.
- Payload minimo final.

## Estrategia

1. Criar adapter Qdrant.
2. Criar colecao.
3. Implementar upsert/delete.
4. Testar filtros obrigatorios.

## Testabilidade

- Integracao Qdrant.
- Upsert.
- Busca filtrada.
- Delete/reindexacao inicial.

## Fechamento

- Chunks ficam pesquisaveis sem perder escopo.
