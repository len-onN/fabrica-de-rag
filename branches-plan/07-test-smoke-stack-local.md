# test/smoke-stack-local

Status: implementada e validada.

## Objetivo

Criar o primeiro teste de fumaca da stack local.

## Tags

TEST, RUNTIME, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [build/dev-runtime-compose](03-build-dev-runtime-compose.md), [chore/backend-spring-base](04-chore-backend-spring-base.md), [chore/frontend-angular-base](05-chore-frontend-angular-base.md), [chore/worker-python-base](06-chore-worker-python-base.md).

## Escopo

- Criar script `scripts/test-smoke` ou equivalente PowerShell.
- Subir Compose.
- Validar health de Postgres, Qdrant, api, worker e web quando existirem.
- Coletar logs suficientes em falha.

## Fora de escopo

- E2E completo.
- Teste de ingestao.
- Teste de UI profundo.

## Definido

- Smoke deve ser rapido.
- Falha de servico deve falhar o script.
- Formato final dos scripts multiplataforma: por enquanto centralizado no PowerShell (`test-smoke.ps1`) para o ambiente atual.
- Tempo maximo aceitavel para subida local: usando o padrao do compose up (`--wait` timeout 120s e requests de health local com 2s timeout).

## Falta definir

- (Nada. Resolvido nesta branch)

## Estrategia

1. Definir comando unico.
2. Subir stack.
3. Esperar health checks.
4. Encerrar com exit code correto.

## Testabilidade

- Script passa quando a stack sobe.
- Script falha se um servico principal nao responde.

## Fechamento

- Ambiente local tem prova objetiva minima.
