# docs/modelo-dados-contratos-mvp

Status: concluida.

## Objetivo

Fechar o modelo de dados e os contratos do MVP antes das branches de codigo que criarem schema, endpoints, eventos ou payloads vetoriais.

## Tags

DOC, DATA, CONTRACT, SEC, TEST, PERF

## Escopo

- Definir entidades relacionais iniciais.
- Definir estrategia de IDs internos e externos.
- Definir convencoes de tabelas, colunas, constraints e indices.
- Definir DTOs REST principais e padrao de paginacao.
- Definir contratos Pydantic Spring -> worker.
- Definir payload minimo Qdrant.
- Definir schema inicial de eventos de analytics.
- Definir ferramenta/padrao de OpenAPI para Spring Boot 4.1.

## Fora de escopo

- Implementar migrations.
- Criar endpoints reais.
- Criar codigo de worker.

## Definido

- PostgreSQL e fonte da verdade.
- Qdrant e indice derivado.
- Flyway sera usado para migrations.
- API publica usa `/api/v1`.
- API interna usa contratos versionados.
- IDs internos usam `uuid`; IDs publicos opacos prefixados sao usados em URLs, JSON, fixtures, logs seguros e payload Qdrant.
- O modelo inicial cobre users, auth identities, workspaces, memberships, collections, documents, pages, elements, assets, visual interpretations, chunks, embeddings, runs, vector bindings, analytics events e feedback.
- Toda entidade principal e escopada por workspace diretamente ou por relacionamento obrigatorio claro.
- REST usa JSON `camelCase`, enums `snake_case`, paginacao por cursor e erro em Problem Details com `code`, `correlationId` e `fieldErrors`.
- Worker usa contratos Pydantic versionados por endpoint, com `contractVersion`, `requestId`, `workspaceId` e erro estruturado.
- Payload Qdrant minimo e versionado como `qdrant.chunk.v1`, com point id deterministico e filtros obrigatorios por workspace/collection.
- Eventos locais usam envelope `analytics.event.v1`, origem `ui|api|worker|mcp`, correlation id e propriedades sem dados sensiveis.
- OpenAPI 3.1 e o formato alvo; `tests/contracts` e exemplos JSON sao fonte regressiva inicial ate a biblioteca compativel com Spring Boot 4.1 ser validada no scaffold.

## Falta definir

- Nada bloqueante para este portao.
- A dependencia concreta de OpenAPI no `pom.xml` sera validada em `chore/backend-spring-base`, porque depende do scaffold real.
- Transicoes completas de ingestao, matriz de permissoes, algoritmos, budgets e taxonomia final de analytics ficam nas branches `docs/*` donas.

## Detalhes que devem ficar explicitos

- Estrategia de IDs: identificador interno, identificador publico opaco, uso em URLs e estabilidade em fixtures/e2e.
- Campos padrao por entidade: `id`, `workspace_id`, timestamps, status, versao de contrato e metadados variaveis.
- Regras de soft delete/archival, incluindo impacto em Qdrant, analytics e storage local.
- Indices minimos por query esperada, especialmente `workspace_id`, colecao, documento, run e busca por status.
- Limite entre dados normalizados e JSONB, evitando esconder contrato essencial em metadado livre.
- Payload Qdrant minimo, point id deterministico/idempotente, filtros obrigatorios e referencia de volta ao SQL.
- Modelo de elementos/assets/interpretacoes visuais como fonte para chunks/citacoes sem transformar LLM/VLM em fonte canonica.
- Envelope de erro, paginacao, ordenacao e filtros comuns em REST.
- Compatibilidade entre OpenAPI, JSON fixtures, Pydantic e schemas MCP, com exemplos em `tests/contracts`.
- Pontos OCP/LSP/IoC: source connector, vector store adapter, event schema versionado e worker contract versionado; adapters devem ser substituiveis por suite de contrato e ligados por composition root da stack.

## Estrategia

1. Mapear entidades a partir do contrato do MVP.
2. Desenhar relacionamentos e fronteiras de workspace.
3. Registrar DTOs e exemplos JSON em `tests/contracts`.
4. Definir payload Qdrant e invariantes.
5. Registrar decisoes duradouras em ADR.

## Testabilidade

- Checklist de entidades cobre todos os fluxos do MVP.
- Cada endpoint do MVP tem request/response conceitual.
- Cada entidade escopada por workspace possui regra de isolamento.
- Payload Qdrant referencia SQL como fonte da verdade.

## Fechamento

- Branches de backend, worker, Qdrant e analytics podem implementar sem inventar contrato estrutural.
- Documento canonico criado em `docs/modelo-dados-contratos-mvp.md`.
- Exemplos JSON iniciais criados em `tests/contracts`.
