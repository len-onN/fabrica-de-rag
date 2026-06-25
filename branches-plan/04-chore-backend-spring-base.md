# chore/backend-spring-base

Status: candidata.

## Objetivo

Criar a base Spring Boot executavel, testavel e preparada para dominios modulares.

## Tags

CODE, RUNTIME, DATA, CONTRACT, TEST, SEC, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [build/dev-runtime-compose](03-build-dev-runtime-compose.md).
- Contratos/fixtures: [REST](../tests/contracts/rest), [eventos](../tests/contracts/events), [ingestao](../tests/contracts/ingestion).

## Escopo

- Criar projeto Spring Boot em `apps/api`.
- Configurar profiles `dev`, `test`, `e2e`.
- Criar health endpoint.
- Configurar Postgres e Flyway baseline.
- Criar estrutura de pacotes.
- Criar tratamento de erro inicial.

## Fora de escopo

- Modelo documental completo.
- Autenticacao real.
- Qdrant real, salvo configuracao placeholder.

## Definido

- Spring Boot como orquestrador transacional.
- Modular monolith.
- Controllers finos e application services.
- Erros padronizados.

## Falta definir

- Como materializar o padrao OpenAPI decidido em `docs/modelo-dados-contratos-mvp`.
- Detalhes locais de profiles e nomes de properties.

## Estrategia

1. Gerar projeto base.
2. Configurar profiles.
3. Criar health e erro padrao.
4. Adicionar Flyway baseline.
5. Adicionar testes de contexto e health.

## Testabilidade

- Teste de contexto.
- Teste de health.
- Migration aplica em ambiente de teste.

## Fechamento

- API base sobe localmente e em teste.
