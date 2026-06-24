# Estrategia de Testes

## Visao

A estrategia de testes deve equilibrar confianca e simplicidade. O projeto tem varias camadas, entao tentar validar tudo apenas com e2e deixaria a suite lenta e fragil. Por outro lado, testar apenas unidades isoladas nao provaria que Angular, Spring Boot, Python worker, PostgreSQL, Qdrant e MCP conversam corretamente.

A abordagem sera:

```text
unitarios rapidos
-> contratos e slices por stack
-> integracao com infraestrutura real quando importa
-> smoke da stack local
-> e2e curto para fluxos criticos
```

## Piramide pratica

| Nivel | Objetivo | Ferramentas candidatas | Quando roda |
| --- | --- | --- | --- |
| Static checks | Formato, lint, tipos e imports | Angular/Vitest tooling, Java build, pytest/ruff/mypy quando definidos | Toda branch |
| Unit | Regras puras e algoritmos | JUnit, Vitest, pytest | Toda branch |
| Component/slice | Controller, service, componente ou endpoint isolado | Spring test slices, Angular component tests, FastAPI TestClient | Toda branch que toca a camada |
| Contract | DTOs, eventos, schemas e ferramentas | OpenAPI, JSON fixtures, Pydantic, testes de schema | Toda mudanca de contrato |
| Integration | Banco, migrations, Qdrant, worker real | Testcontainers, Docker Compose local, pytest integration | Quando toca infra real |
| Smoke | Servicos sobem e health checks respondem | Docker Compose | Antes de features verticais |
| E2E | Fluxos principais do usuario | Playwright + compose e2e | Fluxos criticos e antes de fechar MVP |

## Regras gerais

- Teste nasce junto com o contrato.
- Cada branch deve declarar quais niveis de teste se aplicam.
- Unitarios devem ser rapidos e deterministicos.
- Integracao deve usar servico real quando o comportamento do servico for parte do risco.
- E2E deve cobrir poucos fluxos de alto valor.
- Fixtures devem ser pequenas, versionadas e sem dados sensiveis.
- Testes devem validar autorizacao e isolamento por workspace sempre que dados forem escopados.
- Falhas devem ser diagnosticaveis por logs, traces ou mensagens claras.

## Testes por stack

### Backend Spring Boot

Tipos:

- unitarios para dominio e application services;
- slice tests para controllers e serializacao;
- testes de repository/migration com PostgreSQL real;
- testes de integracao com Qdrant quando houver payload/filtro/upsert/delete;
- testes de autorizacao e workspace scope;
- testes de contrato para APIs publicas e internas.

Padroes:

- preferir `spring-boot-starter-test` como base;
- usar Testcontainers para Postgres/Qdrant quando o teste precisa provar integracao real;
- usar fixtures JSON para request/response relevantes;
- validar erro e sucesso;
- validar filtro por `workspace_id` com pelo menos dois workspaces.

### Frontend Angular

Tipos:

- unitarios para services, formatadores e guards;
- component tests para estados de loading, erro, vazio e sucesso;
- testes de roteamento para fluxos principais;
- testes de client API quando houver transformacao relevante;
- Playwright apenas para fluxos integrados.

Padroes:

- preferir testes de comportamento visivel;
- evitar snapshot como prova principal;
- usar seletores estaveis apenas quando necessario;
- telas novas devem nascer com estado vazio e erro testaveis.

### Worker Python

Tipos:

- unitarios para parsing, normalizacao, chunking e validadores;
- FastAPI TestClient para endpoints internos;
- testes com PDF fixture pequeno;
- testes de tempo/memoria apenas quando houver risco real;
- testes de contrato Pydantic para request/response.
- testes de interpretacao visual com adapter mockado/deterministico.

Padroes:

- usar layout `src`;
- separar tests fora do pacote principal quando crescer;
- fixtures pequenas e deterministicos;
- nao depender de modelo pesado em teste unitario;
- usar adapter mockado para embeddings/LLM quando houver custo ou rede.
- usar adapter mockado para LLM/VLM de visao em unit, contract, smoke e e2e por padrao.

### Banco e migrations

Tipos:

- migrations aplicam em banco limpo;
- constraints protegem invariantes;
- indices existem para queries esperadas;
- rollback operacional documentado quando reversao automatica nao existir.

Padroes:

- migration pequena por mudanca coerente;
- dados seed apenas para dev/e2e;
- testes de isolamento cross-workspace para entidades principais.

### Qdrant e vector store

Tipos:

- criacao de colecao;
- upsert;
- delete;
- filtros obrigatorios;
- payload minimo;
- busca por workspace/colecao;
- reindexacao quando aplicavel.

Padroes:

- SQL guarda a verdade;
- Qdrant guarda indice derivado;
- testes devem provar que filtros impedem vazamento entre workspaces.

## Docker Compose para e2e

Sim: vale ter um compose dedicado para e2e. A recomendacao e usar Compose em camadas:

```text
infra/compose/compose.yml
infra/compose/compose.dev.yml
infra/compose/compose.e2e.yml
```

O arquivo canonico deve ser `infra/compose/compose.e2e.yml`. Se a ergonomia pedir, podemos criar tambem um atalho na raiz chamado `docker-compose.e2e.yml`, mas sem duplicar configuracao importante.

Uso esperado:

```text
docker compose \
  -f infra/compose/compose.yml \
  -f infra/compose/compose.e2e.yml \
  up --build --abort-on-container-exit --exit-code-from e2e
```

Modelo conceitual:

```yaml
services:
  postgres:
    image: postgres
    environment:
      POSTGRES_DB: ragcreator_e2e

  qdrant:
    image: qdrant/qdrant

  api:
    build: ../../apps/api
    environment:
      SPRING_PROFILES_ACTIVE: e2e
    depends_on:
      postgres:
        condition: service_healthy
      qdrant:
        condition: service_started

  worker:
    build: ../../apps/worker
    environment:
      APP_ENV: e2e

  web:
    build: ../../apps/web
    environment:
      API_BASE_URL: http://api:8080

  e2e:
    image: mcr.microsoft.com/playwright:<pinned-version>
    working_dir: /work
    volumes:
      - ../../:/work
    environment:
      BASE_URL: http://web:4200
      API_BASE_URL: http://api:8080
    depends_on:
      web:
        condition: service_started
```

Esse exemplo e deliberadamente conceitual. A branch `build/dev-runtime-compose` deve criar o compose real; a branch `test/smoke-stack-local` deve criar o primeiro comando verificavel; a branch `test/e2e-mvp-ingestao-recuperacao` deve amadurecer o fluxo e2e completo.

## Design do e2e

Fluxos e2e devem ser poucos:

1. app sobe e mostra shell;
2. bootstrap local cria usuario/workspace;
3. criar colecao e ver estado vazio;
4. upload de PDF fixture pequeno;
5. ingestao gera paginas/chunks;
6. busca/laboratorio retorna chunk e citacao;
7. permissao/workspace impede acesso indevido.

Evitar no e2e:

- testar todos os campos de formulario;
- depender de OCR pesado;
- usar LLM real por padrao;
- usar LLM/VLM real para interpretacao visual por padrao;
- depender de rede externa;
- validar detalhes visuais pequenos;
- usar sleeps fixos.

## Dados de teste

Fixtures iniciais:

```text
tests/fixtures/pdfs/minimal-text.pdf
tests/fixtures/pdfs/page-numbering.pdf
tests/fixtures/pdfs/noisy-scan-placeholder.pdf
tests/fixtures/json/workspaces.json
tests/fixtures/json/collections.json
tests/fixtures/json/chunks.json
```

Regras:

- PDFs pequenos.
- Conteudo sem direitos restritivos.
- Dados deterministicos.
- IDs previsiveis apenas em e2e/fixtures.
- Seeds separadas de migrations.

## Comandos alvo

Os comandos finais serao definidos quando a estrutura existir, mas a intencao e:

```text
scripts/check
scripts/test-unit
scripts/test-integration
scripts/test-smoke
scripts/test-e2e
```

No Windows, esses comandos podem ser implementados tambem como PowerShell:

```text
scripts/check.ps1
scripts/test-unit.ps1
scripts/test-integration.ps1
scripts/test-smoke.ps1
scripts/test-e2e.ps1
```

## Criterio por branch

| Tipo de branch | Teste minimo |
| --- | --- |
| `docs/*` | Links, consistencia e registro atualizado |
| `chore/*` | Build/check da stack afetada |
| `build/*` | Compose sobe ou valida config |
| `feat/*` | Unit + contrato + teste da camada afetada |
| `test/*` | O teste novo falha pelo motivo certo antes ou cobre lacuna clara |
| `perf/*` | Medida antes/depois |
| `fix/*` | Teste de regressao |

## Referencias oficiais

- Docker Compose multiple files: https://docs.docker.com/compose/how-tos/multiple-compose-files/
- Docker Compose profiles: https://docs.docker.com/compose/how-tos/profiles/
- Spring Boot testing: https://docs.spring.io/spring-boot/reference/testing/
- Spring Boot Testcontainers: https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
- Angular testing: https://angular.dev/guide/testing
- FastAPI testing: https://fastapi.tiangolo.com/tutorial/testing/
- pytest good practices: https://docs.pytest.org/en/stable/explanation/goodpractices.html
- Playwright Docker: https://playwright.dev/docs/docker
