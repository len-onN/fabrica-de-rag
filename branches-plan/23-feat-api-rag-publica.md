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
- API keys usam segredo exibido uma vez, hash persistido, role `agent`, capabilities, expiracao e revogacao.
- Browser pode usar sessao, mas clientes externos nao usam token passthrough de usuario humano.
- Limites iniciais herdados de `docs/algoritmos-rag-mvp.md`: `topK=8`, maximo 12, budget default 5000 tokens e maximo 8000.
- Respostas publicas usam citacoes `rag.citation.v1` e estado `insufficient_evidence` quando nao houver contexto confiavel.
- Eventos de API seguem `docs/analytics-observabilidade-mvp.md`: `api_rag_query_executed` e `api_rate_limit_hit`.

## Falta definir

- Formato final da resposta publica.
- Envelope final da resposta publica quando o endpoint real for implementado.
- Rate limiting concreto e buckets finais de limite.

## Estrategia

1. Definir contrato.
2. Criar use case.
3. Aplicar autorizacao.
4. Emitir analytics.
5. Aplicar API key/capabilities quando cliente externo for suportado.
6. Testar isolamento.

## Testabilidade

- Contrato.
- Autorizacao.
- Isolamento por workspace.
- Erros padronizados.

## Fechamento

- Clientes externos consultam RAG com seguranca minima.
