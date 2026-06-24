# feat/mcp-tools-base

Status: candidata.

## Objetivo

Criar primeiras ferramentas MCP para agentes.

## Tags

AGENT, CODE, CONTRACT, TEST, SEC, OBS

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

## Falta definir

- Versionamento de tools.
- Politica de limites por ferramenta.
- Transporte inicial local/remoto.
- Estrategia de compatibilidade entre schemas MCP e contratos REST.

## Estrategia

1. Definir schemas.
2. Implementar tools em `apps/mcp`.
3. Integrar com use cases existentes via backend.
4. Aplicar identity/workspace/permission.
5. Registrar eventos.
6. Testar falhas e limites.

## Testabilidade

- Schema de tool.
- Permissoes.
- Workspace scope.
- Limites e falhas.

## Fechamento

- Agentes investigam bases por ferramentas controladas.
