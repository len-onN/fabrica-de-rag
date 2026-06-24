# feat/api-rag-publica

Status: candidata.

## Objetivo

Publicar API HTTP de consulta RAG.

## Tags

CODE, CONTRACT, TEST, SEC, OBS, PERF

## Escopo

- Criar endpoint autenticado.
- Aplicar workspace scope.
- Aplicar limites iniciais.
- Retornar chunks, contexto e citacoes.
- Registrar eventos de analytics.

## Fora de escopo

- MCP completo.
- Ferramentas administrativas.
- Rate limiting sofisticado se nao definido.

## Definido

- API versionada em `/api/v1`.
- Permissoes separadas.
- Erros padronizados.

## Falta definir

- Formato final da resposta publica.
- Limites iniciais.
- Politica de chaves de API.

## Estrategia

1. Definir contrato.
2. Criar use case.
3. Aplicar autorizacao.
4. Emitir analytics.
5. Testar isolamento.

## Testabilidade

- Contrato.
- Autorizacao.
- Isolamento por workspace.
- Erros padronizados.

## Fechamento

- Clientes externos consultam RAG com seguranca minima.

