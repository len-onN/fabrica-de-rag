# feat/qdrant-indexacao

Status: candidata.

## Objetivo

Indexar embeddings no Qdrant com payload minimo e filtros obrigatorios.

## Tags

CODE, RUNTIME, DATA, CONTRACT, TEST, SEC, PERF

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

