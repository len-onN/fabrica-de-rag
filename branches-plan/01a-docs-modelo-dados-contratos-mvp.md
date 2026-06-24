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
- Campos obrigatorios de workspace, user, collection, document, page, chunk, run e event.
- Estrategia de soft delete/archival.
- Biblioteca final de OpenAPI/schema.
- Estrategia de compatibilidade entre DTO REST, Pydantic e MCP tool schema.

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
