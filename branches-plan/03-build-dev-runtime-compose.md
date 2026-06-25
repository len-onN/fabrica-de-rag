# build/dev-runtime-compose

Status: em fechamento.

## Objetivo

Criar runtime local reproduzivel com Docker Compose.

## Tags

RUNTIME, DATA, OBS, TEST

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Plano de branches](../docs/plano-de-branches.md), [Escopos detalhados](../docs/escopos-detalhados-branches.md), [chore/workspace-fundacao](02-chore-workspace-fundacao.md).

## Escopo

- Criar `infra/compose/compose.yml`.
- Criar `infra/compose/compose.dev.yml` se necessario.
- Criar `infra/compose/compose.e2e.yml` conceitual ou inicial.
- Subir Postgres e Qdrant.
- Definir volumes, redes, health checks e profiles.

## Fora de escopo

- Build completo de api/web/worker se ainda nao existirem.
- Pipeline real de ingestao.
- E2E completo.

## Definido

- Postgres e Qdrant sao dependencias base.
- Compose e2e canonico fica em `infra/compose/compose.e2e.yml`.
- Imagem inicial do Postgres: `postgres:18.4`.
- Imagem inicial do Qdrant: `qdrant/qdrant:v1.18.2`.
- Arquivos Compose versionados nao usam `latest`.
- Volumes de desenvolvimento usam volumes nomeados persistentes do projeto Compose.
- O ambiente e2e usa projeto Compose separado e volumes removiveis por `compose-down.ps1 -Profile e2e -RemoveVolumes`.
- Rede padrao: `ragcreator`.
- Portas de desenvolvimento: Postgres `5432`, Qdrant HTTP `6333` e Qdrant gRPC `6334`, com override por variaveis `RAG_POSTGRES_PORT`, `RAG_QDRANT_HTTP_PORT` e `RAG_QDRANT_GRPC_PORT`.
- `scripts/compose-up.ps1` e `scripts/compose-down.ps1` sao a interface operacional inicial do Compose.

## Falta definir

- Nada bloqueante para esta branch; servicos de `api`, `web`, `worker` e `mcp` entram nas branches base de cada app.

## Estrategia

1. Criar Compose base.
2. Validar `docker compose config`.
3. Adicionar health checks.
4. Documentar comandos de up/down/logs.
5. Ligar os scripts PowerShell ao Compose real.

## Testabilidade

- `docker compose config` passa.
- Postgres responde.
- Qdrant responde.
- Health checks definidos antes de depender de smoke/e2e.
- `compose-up.ps1 -Profile dev` sobe Postgres e Qdrant com health checks saudaveis.
- `compose-up.ps1 -Profile e2e` sobe stack isolada; `compose-down.ps1 -Profile e2e -RemoveVolumes` limpa containers e volumes.

## Gates aplicaveis

- OCP: Compose cria servicos de infraestrutura reutilizaveis, sem criar apps reais fora das branches donas.
- LSP: scripts deixam de ser placeholders mantendo semantica previsivel de exit code e mensagens claras.
- IoC/DI: nao ha codigo de dominio; concretos de infraestrutura entram por arquivos Compose versionados.
- Compatibilidade: `scripts/check.ps1` continua validando a fundacao e passa a validar os arquivos Compose.

## Fechamento

- Dependencias locais sobem de forma previsivel.
- Proxima branch pode criar a base Spring Boot usando Postgres dev ja disponivel.
