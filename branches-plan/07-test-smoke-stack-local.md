# test/smoke-stack-local

Status: candidata.

## Objetivo

Criar o primeiro teste de fumaca da stack local.

## Tags

TEST, RUNTIME, OBS

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

## Falta definir

- Formato final dos scripts multiplataforma.
- Tempo maximo aceitavel para subida local.

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

