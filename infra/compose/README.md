# Compose

Runtime local inicial da Fabrica de RAG.

Arquivos:

- `compose.yml`: servicos base, rede, volumes e health checks.
- `compose.dev.yml`: portas locais publicadas e volumes persistentes do projeto dev.
- `compose.e2e.yml`: projeto isolado para e2e, sem portas publicadas e com volumes removiveis.

Servicos atuais:

| Servico | Imagem | Portas dev | Health check |
| --- | --- | --- | --- |
| `postgres` | `postgres:18.4` | `5432` | `pg_isready` |
| `qdrant` | `qdrant/qdrant:v1.18.2` | `6333`, `6334` | abertura TCP local em `6333` |

Comandos:

```powershell
.\scripts\check.ps1
.\scripts\compose-up.ps1 -Profile dev
.\scripts\compose-down.ps1 -Profile dev
.\scripts\compose-up.ps1 -Profile e2e
.\scripts\compose-down.ps1 -Profile e2e -RemoveVolumes
```

Overrides de portas dev:

```powershell
$env:RAG_POSTGRES_PORT = "15432"
$env:RAG_QDRANT_HTTP_PORT = "16333"
$env:RAG_QDRANT_GRPC_PORT = "16334"
.\scripts\compose-up.ps1 -Profile dev
```

As imagens sao versionadas, sem `latest`. Servicos de `api`, `web`, `worker` e `mcp` entram nas branches base de cada app.
