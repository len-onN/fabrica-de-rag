# chore/worker-python-base

Status: candidata.

## Objetivo

Criar a base Python worker executavel, testavel e pronta para contratos internos.

## Tags

CODE, RUNTIME, CONTRACT, TEST, PERF, OBS

## Escopo

- Criar `apps/worker`.
- Criar `pyproject.toml`.
- Criar layout `src/rag_worker`.
- Criar FastAPI app.
- Criar health endpoint.
- Criar contratos Pydantic base.
- Criar Dockerfile do worker.

## Fora de escopo

- OCR.
- Parsing PDF real.
- Embeddings reais.

## Definido

- Worker separado.
- API interna simples no MVP.
- Pydantic para contratos.

## Falta definir

- Versao Python.
- Ferramenta de dependency management.
- Lint/type checking inicial.

## Estrategia

1. Criar layout.
2. Criar app FastAPI.
3. Criar health.
4. Criar teste com TestClient.
5. Criar container.

## Testabilidade

- Pytest passa.
- Health responde.
- Container inicia.

## Fechamento

- Worker pode ser chamado pela API.

