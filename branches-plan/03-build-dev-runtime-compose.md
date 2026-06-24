# build/dev-runtime-compose

Status: candidata.

## Objetivo

Criar runtime local reproduzivel com Docker Compose.

## Tags

RUNTIME, DATA, OBS, TEST

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
