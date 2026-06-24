# Contract Fixtures

Esta pasta guarda exemplos JSON versionados usados como fonte regressiva para DTOs REST, contratos Pydantic do worker, payload Qdrant, eventos locais e schemas MCP.

Regras:

- cada arquivo deve representar um contrato pequeno e valido;
- IDs devem ser publicos e deterministicos;
- exemplos nao devem conter dados sensiveis, secrets, headers reais, embeddings reais ou texto protegido;
- alteracao quebradora deve criar novo arquivo/versao em vez de reescrever silenciosamente o contrato antigo;
- schemas gerados por OpenAPI, Pydantic ou MCP devem ser comparados com estes exemplos quando a stack existir.

Layout inicial:

```text
rest/
worker/
ingestion/
qdrant/
events/
mcp/
```
