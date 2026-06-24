# feat/busca-vetorial-base

Status: candidata.

## Objetivo

Buscar chunks por pergunta usando embeddings e Qdrant.

## Tags

CODE, CONTRACT, DATA, TEST, SEC, PERF

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

## Falta definir

- Top-k inicial.
- Score minimo.
- Formato inicial de citacao no response.

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

