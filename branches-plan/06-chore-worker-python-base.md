# chore/worker-python-base

Status: candidata.

## Objetivo

Criar a base Python worker executavel, testavel e pronta para contratos internos.

## Tags

CODE, RUNTIME, CONTRACT, TEST, PERF, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [build/dev-runtime-compose](03-build-dev-runtime-compose.md).
- Contratos/fixtures: [worker](../tests/contracts/worker), [PDF fixtures](../tests/fixtures/pdfs).

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
