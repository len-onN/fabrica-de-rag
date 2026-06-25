# build/dev-runtime-compose

Status: candidata.

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

## Falta definir

- Politica de volumes dev vs e2e.
- Nome de rede e portas padrao.

## Estrategia

1. Criar Compose base.
2. Validar `docker compose config`.
3. Adicionar health checks.
4. Documentar comandos de up/down/logs.

## Testabilidade

- `docker compose config` passa.
- Postgres responde.
- Qdrant responde.
- Health checks definidos antes de depender de smoke/e2e.

## Fechamento

- Dependencias locais sobem de forma previsivel.
