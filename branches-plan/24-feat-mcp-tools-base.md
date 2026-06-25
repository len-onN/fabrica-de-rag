# feat/mcp-tools-base

Status: candidata.

## Objetivo

Criar primeiras ferramentas MCP para agentes.

## Tags

AGENT, CODE, CONTRACT, TEST, SEC, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Skills e boas praticas para agentes](../docs/skills-e-boas-praticas-para-agentes.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [MVP interface e API](../docs/mvp-interface-e-api.md), [feat/api-rag-publica](23-feat-api-rag-publica.md).
- Contratos/fixtures: [MCP](../tests/contracts/mcp), [RAG](../tests/contracts/rag), [eventos MCP](../tests/contracts/events/mcp-tool-invoked.v1.json), [seguranca](../tests/contracts/security).

## Escopo

- Implementar `search_chunks`.
- Implementar `get_chunk`.
- Implementar `expand_context`.
- Implementar `ask_rag` se resposta estiver habilitada.
- Registrar analytics de chamadas e falhas.

## Fora de escopo

- Ferramentas mutantes.
- Administracao via MCP.
- SQL livre.

## Definido

- Ferramentas guiadas.
- Schemas explicitos.
- Menor privilegio.
- Sem SQL livre.
- Runtime inicial em TypeScript/Node 24 dentro de `apps/mcp`.
- MCP server como adaptador fino sobre contratos do backend.
- Sem acesso direto a Postgres, Qdrant ou filesystem livre pelo MCP no MVP.
- Identidade `agent` com capabilities explicitas.
- Proibido token passthrough de usuario humano.
- Tool call carrega workspace, correlation id, capability e limites.
- Limites iniciais herdados de `docs/algoritmos-rag-mvp.md`: `topK=8`, maximo 12, budget default 4000 tokens e maximo 6000.
- Tools de contexto usam `rag.citation.v1` e retornam `insufficient_evidence` quando aplicavel.
- Eventos MCP seguem `docs/analytics-observabilidade-mvp.md`: `mcp_tool_invoked`, `mcp_tool_failed`, `mcp_context_budget_exceeded` e `mcp_agent_loop_detected`.

## Falta definir

- Versionamento de tools.
- Transporte inicial local/remoto.
- Estrategia de compatibilidade entre schemas MCP e contratos REST.
- Envelope final de erro por tool quando a implementacao real chegar.
- Janelas e limites concretos para deteccao simples de loop.

## Estrategia

1. Definir schemas.
2. Implementar tools em `apps/mcp`.
3. Integrar com use cases existentes via backend.
4. Aplicar identity/workspace/permission.
5. Registrar eventos.
6. Testar falhas, schemas fechados, capabilities e limites.

## Testabilidade

- Schema de tool.
- Permissoes.
- Workspace scope.
- Limites e falhas.

## Fechamento

- Agentes investigam bases por ferramentas controladas.
