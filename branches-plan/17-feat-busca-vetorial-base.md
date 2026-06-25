# feat/busca-vetorial-base

Status: candidata.

## Objetivo

Buscar chunks por pergunta usando embeddings e Qdrant.

## Tags

CODE, CONTRACT, DATA, TEST, SEC, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [Conectores vector store](../docs/conectores-vector-store.md), [feat/qdrant-indexacao](16-feat-qdrant-indexacao.md).
- Contratos/fixtures: [busca RAG](../tests/contracts/rag/search-response.v1.json), [Qdrant payload](../tests/contracts/qdrant/chunk-payload.v1.json), [eventos de recuperacao](../tests/contracts/events/retrieval-query-executed.v1.json).

## Escopo

- Gerar embedding da pergunta.
- Consultar Qdrant.
- Buscar metadados no SQL.
- Retornar chunks ordenados com citacao minima.

## Fora de escopo

- Reranking.
- Busca hibrida.
- Context builder completo.

## Definido

- Qdrant encontra ancoras.
- SQL enriquece resultado.
- Qdrant nao responde sozinho ao usuario.
- Busca definida em `docs/algoritmos-rag-mvp.md` como `vector_search_v1`.
- `topK` default: 12 no laboratorio, 8 na API/MCP.
- Nao ha score minimo duro por padrao; resultados abaixo de confianca sao marcados com `lowConfidence`.
- Filtros obrigatorios: workspace, colecao autorizada, payload contract e vector space no Qdrant; revalidacao SQL apos busca.

## Falta definir

- Ajustes de score apos dados reais, preservando `lowConfidence` e sem reranking no MVP.

## Estrategia

1. Definir contrato de busca.
2. Criar use case.
3. Integrar adapter de embeddings e Qdrant.
4. Enriquecer resultado via SQL.

## Testabilidade

- Busca retorna chunk esperado.
- Workspace errado nao retorna dado.
- Sem resultados retorna estado claro.

## Fechamento

- API consegue recuperar ancoras.
