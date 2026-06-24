# docs/modelo-dados-contratos-mvp

Status: candidata.

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

## Falta definir

- UUID vs bigint vs IDs opacos publicos.
- Campos obrigatorios de workspace, user, collection, document, page, document_element, asset, visual_interpretation, chunk, run e event.
- Estrategia de soft delete/archival.
- Biblioteca final de OpenAPI/schema.
- Estrategia de compatibilidade entre DTO REST, Pydantic e MCP tool schema.
- Contratos que serao fonte canonica para geracao/validacao de clientes e fixtures.
- Politica de versionamento/deprecacao para DTOs, eventos, schemas Pydantic, tool schemas e payload Qdrant.
- Pontos de extensao para fontes futuras sem prender o modelo a PDF.
- Invariantes de workspace scope que devem aparecer em constraints, indices e queries.

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
