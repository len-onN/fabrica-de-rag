# Apps

Aplicacoes versionadas do monorepo.

| Pasta | Stack planejada | Responsabilidade |
| --- | --- | --- |
| `api` | Spring Boot 4.1 / Java 21 | API principal, autorizacao, orquestracao, banco, jobs e contratos publicos. |
| `web` | Angular 22 / Node 24 | Interface web, shell, rotas, fluxos de workspace e laboratorio. |
| `worker` | Python 3.13 / FastAPI / Pydantic | Processamento de PDF, OCR, elementos visuais, chunking e embeddings por contratos internos. |
| `mcp` | TypeScript / Node 24 | Servidor MCP fino sobre contratos do backend, sem acesso direto a banco ou storage. |

As proximas branches criam os projetos reais dentro dessas pastas.

