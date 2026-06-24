# Plano Tecnico do MVP

## Objetivo

Este documento define as decisoes tecnicas iniciais incorporadas na branch `planejamento/documentacao`, absorvendo o escopo originalmente planejado como `docs/plano-tecnico-mvp`.

Ele nao substitui os ADRs. Ele organiza a execucao inicial para que backend, frontend, worker, banco, vector store, testes e compose crescam juntos com performance, manutencao e escalabilidade.

## Decisoes iniciais

| Area | Decisao inicial | Motivo |
| --- | --- | --- |
| Repositorio | Monorepo unico | Facilita desenvolvimento local, contratos entre stacks, testes e e2e. |
| Estrutura | `apps/web`, `apps/api`, `apps/worker`, `apps/mcp`, `infra/compose`, `tests`, `scripts`, `docs` | Separa ownership sem espalhar contexto e reserva um runtime proprio para ferramentas de agentes. |
| Backend | Spring Boot 4.1.x como modular monolith | Mantem transacoes e autorizacao coesas, com dominios internos bem separados. |
| Java | Java 21 LTS | Linha conservadora e estavel dentro do suporte do Spring Boot 4.1. |
| Frontend | Angular 22.x, standalone components e feature-first | Usa a linha ativa do Angular e facilita evolucao por telas/fluxos reais. |
| Node.js | Node 24 LTS | Linha LTS compativel com Angular 22 e reutilizavel para o runtime MCP. |
| Worker | Python 3.13.x com FastAPI, Pydantic e layout `src` | Contratos claros, testes simples e maturidade melhor para dependencias de processamento. |
| MCP | TypeScript/Node 24 em `apps/mcp` | SDK MCP TypeScript e Tier 1; servidor fino, guiado por tools e sem bypass de autorizacao do backend. |
| Banco | PostgreSQL 18.x como fonte da verdade | Preserva proveniencia, relacoes e auditoria. |
| Migracoes | Flyway no MVP | Simples, versionado, integrado ao ciclo Spring e suficiente para schema inicial. |
| Vector DB | Qdrant 1.18.x interno no MVP | Bom alinhamento com busca vetorial, payloads e filtros. |
| API publica | REST versionada em `/api/v1` | Contrato simples para UI, clientes e agentes. |
| API interna | REST interna Spring -> worker | Suficiente para o MVP; fila entra quando jobs longos justificarem. |
| Erros API | Problem Details/RFC 7807 quando aplicavel | Formato padronizado e facil de consumir. |
| Testes e2e | Docker Compose dedicado + Playwright | Ambiente reprodutivel e proximo do uso real. |
| UI | Componentes proprios + Angular CDK | Preserva identidade visual, acessibilidade e densidade sem travar o design em um kit pesado cedo demais. |
| Embeddings/LLM | Adaptadores mockados por padrao ate branch dedicada | Mantem testes deterministicos e evita dependencia de rede/custos antes do contrato estar maduro. |

## Versoes iniciais e ferramentas

| Componente | Linha definida | Ponto de fixacao | Observacao |
| --- | --- | --- | --- |
| Angular | `22.x` | `apps/web/package.json` e `package-lock.json` | `@angular/core` e Angular CLI devem ficar no mesmo major. |
| Node.js | `24 LTS`, minimo `24.15.0` para Angular 22 | `.node-version` ou `.nvmrc`, `engines` e imagem Docker quando houver | Node 26 fica fora do MVP por estar em Current, nao LTS. |
| TypeScript/RxJS | Faixas exigidas pelo Angular 22 | Lockfile do frontend | Nao escolher manualmente fora da matriz oficial do Angular. |
| Java | `21 LTS` | `.java-version`, Maven toolchain ou Dockerfile | Sem preview features no MVP. |
| Spring Boot | `4.1.0` como ponto inicial | `apps/api/pom.xml` e Maven wrapper | Se o scaffold oficial oferecer patch mais novo da linha 4.1.x, usar o patch mais recente e registrar no bordo. |
| Build backend | Maven wrapper | `apps/api/mvnw` | Menor atrito com Spring Boot e verificacao local simples. |
| Python | `3.13.14` como ponto inicial da linha 3.13.x | `.python-version`, `pyproject.toml` e imagem Docker | Python 3.14 existe, mas 3.13 reduz risco de incompatibilidade em bibliotecas de processamento. |
| Build worker | `pyproject.toml` + lockfile | `apps/worker/pyproject.toml` | Ferramenta de lock sera confirmada na branch do worker para evitar escolha prematura. |
| FastAPI/Pydantic | Versoes pinadas no worker | Lockfile do worker | Contratos Pydantic versionados e testaveis. |
| PostgreSQL | `18.4` | `infra/compose/compose*.yml` | Imagem inicial: `postgres:18.4`. |
| Qdrant | `v1.18.2` | `infra/compose/compose*.yml` | Imagem inicial: `qdrant/qdrant:v1.18.2`; nunca usar `latest` em compose versionado. |
| Docker Compose | Compose V2 | `infra/compose` e scripts | Compose base/dev/e2e em arquivos separados. |
| Playwright | Linha atual no momento do e2e | `tests/e2e/package.json` e lockfile | Usar imagem oficial somente na branch e2e/smoke quando houver testes reais. |

Regra geral: majors e linhas sao definidos aqui; patches exatos devem ficar nos lockfiles e imagens versionadas criados pelas branches de implementacao. Quando uma branch atualizar patch por motivo legitimo, ela atualiza o registro de bordo e, se alterar comportamento, uma ADR.

## Estrutura alvo

```text
.
+-- apps
|   +-- api
|   +-- mcp
|   +-- web
|   +-- worker
+-- docs
+-- infra
|   +-- compose
+-- scripts
+-- tests
    +-- e2e
    +-- fixtures
    +-- contracts
```

Diretrizes:

- `apps/api`: codigo Spring Boot, migrations, testes do backend e contratos REST.
- `apps/mcp`: servidor MCP TypeScript fino, com tools guiadas que chamam contratos do backend e preservam identity/workspace.
- `apps/web`: Angular, componentes, rotas, cliente HTTP, testes de unidade/componente.
- `apps/worker`: FastAPI, processamento PDF, contratos Pydantic, testes Python.
- `infra/compose`: compose base, dev, e2e e arquivos auxiliares.
- `tests/contracts`: fixtures compartilhadas de request/response, schemas e exemplos.
- `tests/e2e`: Playwright e scripts de seed/teardown de e2e.
- `tests/fixtures`: PDFs pequenos, payloads JSON e dados deterministicos.

## Padroes de backend

Arquitetura inicial:

```text
apps/api/src/main/java/.../
+-- bootstrap
+-- shared
+-- identity
+-- workspace
+-- collection
+-- document
+-- ingestion
+-- retrieval
+-- analytics
+-- agent
```

Cada dominio deve preferir:

```text
domain
application
infrastructure
web
```

Regras:

- Controllers ficam finos.
- Casos de uso ficam em application services.
- Entidades e invariantes ficam no dominio.
- Repositories concretos ficam em infrastructure.
- DTOs de API nao vazam para o dominio.
- Transacoes ficam no nivel de caso de uso.
- Autorizacao e workspace scope sao aplicados no backend.
- Queries devem filtrar por `workspace_id` sempre que o recurso for escopado.
- Eventos de analytics nascem nos fluxos principais, nao como remendo posterior.

Performance e escala:

- Evitar N+1 desde o primeiro schema.
- Criar indices junto com queries reais.
- Paginar listas por padrao.
- Evitar payloads grandes em endpoints de lista.
- Tratar Qdrant como indice derivado, nunca como fonte da verdade.
- Separar jobs longos de requests sincronos assim que o tempo de resposta ficar instavel.

## Padroes de frontend

Estrutura inicial:

```text
apps/web/src/app/
+-- core
+-- shared
+-- layout
+-- features
|   +-- setup
|   +-- workspace
|   +-- collections
|   +-- documents
|   +-- ingestion
|   +-- retrieval-lab
|   +-- analytics
+-- api
```

Regras:

- Usar standalone components.
- Usar Angular CDK quando a primitiva acessivel existir.
- Manter componentes visuais proprios para preservar a identidade do produto.
- Preferir signals para estado local e derivado.
- Usar services para comunicacao com API e regras de frontend.
- Nao criar store global antes de haver pressao real.
- Componentes devem ter estados de loading, erro, vazio e permissao negada.
- Rotas devem refletir fluxos reais do MVP.
- UI deve ser densa, utilitaria e consistente com `design-visual.md`.
- Testes de componentes devem cobrir comportamento observavel, nao detalhes internos.

Performance e manutencao:

- Carregar features por rota quando fizer sentido.
- Evitar componentes gigantes.
- Evitar subscriptions soltas.
- Preferir DTOs tipados e client API centralizado.
- Separar transformacao de dados da camada visual.

## Padroes do MCP server

Estrutura inicial:

```text
apps/mcp/
+-- package.json
+-- src
|   +-- server
|   +-- tools
|   +-- contracts
|   +-- clients
|   +-- observability
+-- tests
```

Regras:

- O MCP server comeca como adaptador fino em TypeScript/Node 24.
- Tools devem ser explicitamente nomeadas, versionadas e descritas por schema.
- Tools chamam o backend ou contratos internos aprovados; nao acessam Postgres, Qdrant ou filesystem livremente.
- Identity, workspace scope, permissao, limites e auditoria continuam sob responsabilidade do backend.
- SQL livre, ferramentas mutantes e administracao por MCP ficam fora do MVP inicial.
- Cada chamada de tool deve carregar correlation id, workspace id e evento de analytics local.
- Erros devem ser estruturados e seguros para exibicao ao agente.

Performance e seguranca:

- Definir timeout por tool.
- Limitar tamanho de contexto retornado.
- Evitar retorno de documento inteiro.
- Aplicar budget de chamadas e tamanho por sessao quando houver cliente persistente.
- Testar schemas, limites, permissoes e falhas antes de expor tools novas.

## Padroes do worker Python

Estrutura inicial:

```text
apps/worker/
+-- pyproject.toml
+-- src/rag_worker
|   +-- api
|   +-- contracts
|   +-- pdf
|   +-- ocr
|   +-- chunking
|   +-- embeddings
|   +-- observability
+-- tests
```

Regras:

- Usar Pydantic para contratos de entrada e saida.
- Versionar contratos internos quando mudarem comportamento.
- Separar endpoint HTTP, regra de processamento e adaptador de biblioteca.
- Usar fixtures pequenas e deterministicos para PDF.
- Nunca executar processamento pesado em teste unitario.
- Medir tempo e memoria em etapas pesadas quando houver fixture representativa.

Performance:

- Processar PDFs por pagina ou lote pequeno.
- Evitar carregar o documento inteiro em memoria quando nao for necessario.
- Registrar duracao por etapa: inspect, extract, OCR, chunking, embeddings.
- Adiar fila ate haver necessidade real, mas manter contrato de job preparado.

## Padroes de banco

Regras iniciais:

- Toda entidade principal escopada por workspace deve carregar `workspace_id` direto ou relacao obrigatoria clara.
- IDs externos devem ser opacos.
- Tabelas centrais devem ter `created_at` e `updated_at`.
- Jobs/runs devem registrar status, parametros, versoes e erro.
- JSONB deve ser usado para metadados variaveis, nao para contratos essenciais.
- Constraints devem proteger invariantes importantes.
- Migrations devem ser pequenas e revisaveis.

Nomes:

- tabelas em `snake_case` plural quando representar colecao de entidades;
- colunas em `snake_case`;
- constraints com prefixo: `pk_`, `fk_`, `uk_`, `ck_`, `idx_`;
- migrations Flyway: `V0001__descricao_curta.sql`.

## Padroes de contratos

REST:

- Base publica: `/api/v1`.
- Endpoints internos: `/internal/v1`.
- DTOs com campos explicitos.
- Erros padronizados.
- IDs no path quando identificam recurso.
- Filtros e paginacao por query string.
- Respostas de lista devem incluir dados e metadados de paginacao.
- OpenAPI deve ser publicado pelo backend quando a biblioteca escolhida for validada com Spring Boot 4.1.
- Enquanto isso, `tests/contracts` guarda exemplos JSON versionados e fixtures de request/response.

Worker:

- Contratos Pydantic versionados.
- Timeout explicito.
- Erros estruturados.
- Correlation id/request id entre Spring e worker.

Agentes/MCP:

- Ferramentas guiadas.
- Schemas explicitos.
- Menor privilegio.
- Workspace e identidade obrigatorios.
- Eventos de analytics para chamadas, limites e falhas.
- Sem acesso direto a banco, storage local ou vector DB pelo MCP no MVP.

## Organizacao do Compose

Arquivos planejados:

- `infra/compose/compose.yml`: servicos base reutilizaveis.
- `infra/compose/compose.dev.yml`: portas locais, volumes persistentes e facilidades de desenvolvimento.
- `infra/compose/compose.e2e.yml`: ambiente isolado, volumes temporarios e seed deterministico.
- `docker-compose.e2e.yml`: atalho opcional na raiz, somente se melhorar ergonomia, apontando para a configuracao canonica.

Regras:

- Imagens versionadas, sem `latest`.
- Health checks para Postgres, Qdrant, api, worker e web quando existirem.
- Profiles para ligar servicos opcionais sem inflar o fluxo comum.
- Volumes persistentes em dev; volumes descartaveis ou nomeados por run no e2e.
- Rede interna unica por stack local, com portas publicadas so quando necessario.
- Secrets reais nunca entram no repositorio; e2e usa variaveis fake e deterministicos.

## Comandos alvo

Estes comandos ainda serao implementados pelas branches de codigo, mas ficam definidos como interface operacional alvo:

```powershell
.\scripts\check.ps1
.\scripts\test-unit.ps1
.\scripts\test-integration.ps1
.\scripts\test-smoke.ps1
.\scripts\test-e2e.ps1
.\scripts\compose-up.ps1 -Profile dev
.\scripts\compose-down.ps1
```

Expectativa por comando:

- `check.ps1`: format/lint/test rapido do que existir na branch.
- `test-unit.ps1`: unitarios de cada stack ja criada.
- `test-integration.ps1`: testes com Postgres, Qdrant ou worker quando a branch tocar integracao real.
- `test-smoke.ps1`: sobe stack minima e valida health checks.
- `test-e2e.ps1`: usa Compose e2e e Playwright para poucos fluxos criticos.

No Windows os scripts PowerShell sao a primeira interface. Scripts POSIX podem entrar depois quando houver necessidade real de CI/Linux.

## Quality gates por PR

Cada PR para `develop` deve explicitar:

- escopo da branch e arquivo correspondente em `branches-plan`;
- fontes oficiais/skills usadas quando houver decisao tecnica sensivel;
- colateralidades em dados, contratos, runtime, permissao, analytics e testes;
- comandos executados e resultado;
- pendencias deixadas para branches futuras;
- atualizacao de `docs/registro-de-bordo.md` e, quando a etapa fechar, `docs/diario-de-bordo.md`.

Gate minimo por tipo de branch:

| Tipo | Gate minimo |
| --- | --- |
| `docs/*` | Consistencia de links internos, registro de bordo atualizado e ADR quando houver decisao duradoura. |
| `chore/*` | Estrutura criada, scripts ou comandos com saida previsivel e docs atualizados. |
| `build/*` | `docker compose config`, health checks quando houver servico e imagens pinadas. |
| `feat/*` | Unitarios/contratos, integracao quando tocar infraestrutura e smoke quando tocar fluxo vertical. |
| `test/*` | Suite nova reproduzivel, fixture pequena e falhas claras. |
| `perf/*` | Baseline, metrica, mudanca, comparacao e limite de regressao documentado. |

## Padroes de testes

O detalhe esta em [Estrategia de testes](estrategia-de-testes.md). Decisao resumida:

- Testes unitarios e de contrato em toda branch.
- Testes de integracao para banco, Qdrant e worker quando o contrato tocar infraestrutura real.
- Teste de fumaca da stack antes da primeira feature vertical.
- E2E com Docker Compose dedicado para poucos fluxos criticos.
- Playwright para fluxos de UI.
- Testcontainers para integracao backend com servicos reais quando o custo for aceitavel.

## Definicoes com branch dona

Estas decisoes nao devem ser tomadas de improviso dentro das feature branches. Cada uma passa a ter uma branch ou escopo dono:

| Decisao | Onde fechar |
| --- | --- |
| Modelo relacional, IDs, DTOs, OpenAPI, Pydantic, payload Qdrant e eventos | `docs/modelo-dados-contratos-mvp` |
| Pipeline de ingestao, retry/cancel, idempotencia, storage e reindexacao | `docs/arquitetura-ingestao-rag` |
| Auth local, sessao/token, roles, workspace scope, API/MCP e guardrails | `docs/seguranca-permissoes-mvp` |
| Provider/modelo de embeddings, LLM real vs adapter mockado, budgets e algoritmos | `docs/algoritmos-rag-mvp` |
| Taxonomia de analytics, retencao, export/delete, logs e metricas | `docs/analytics-observabilidade-mvp` |
| Ferramenta de lock do worker Python | `chore/worker-python-base` |
| Comando unico de verificacao local | `chore/workspace-fundacao` e branches base das stacks |

## Decisoes incorporadas nesta etapa

- O escopo `docs/plano-tecnico-mvp` foi executado dentro de `planejamento/documentacao`.
- A fundacao passa a prever `apps/mcp` como runtime TypeScript/Node 24.
- Stack baseline definida: Angular 22, Node 24 LTS, Spring Boot 4.1, Java 21, Python 3.13, PostgreSQL 18 e Qdrant 1.18.
- Flyway fica aceito como ferramenta inicial de migracoes.
- UI inicial usa componentes proprios com Angular CDK.
- E2E segue Compose dedicado, sem rede externa e sem LLM real por padrao.
- OpenAPI, embeddings, seguranca, analytics, ingestao e algoritmos passam a ter branches de planejamento antes da implementacao.

## Referencias oficiais

- Angular releases: https://angular.dev/reference/releases
- Angular version compatibility: https://angular.dev/reference/versions
- Node.js releases: https://nodejs.org/en/about/previous-releases
- Spring Boot system requirements: https://docs.spring.io/spring-boot/system-requirements.html
- Python downloads: https://www.python.org/downloads/
- Python versions: https://devguide.python.org/versions/
- PostgreSQL versioning policy: https://www.postgresql.org/support/versioning/
- Qdrant quickstart: https://qdrant.tech/documentation/quickstart/
- Qdrant releases: https://github.com/qdrant/qdrant/releases
- MCP introduction: https://modelcontextprotocol.io/docs/getting-started/intro
- MCP SDKs: https://modelcontextprotocol.io/docs/sdk
- Docker Compose multiple files: https://docs.docker.com/compose/how-tos/multiple-compose-files/
- Docker Compose profiles: https://docs.docker.com/compose/how-tos/profiles/
- Spring Boot testing: https://docs.spring.io/spring-boot/reference/testing/
- Spring Boot Testcontainers: https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
- Angular testing: https://angular.dev/guide/testing
- FastAPI testing: https://fastapi.tiangolo.com/tutorial/testing/
- pytest good practices: https://docs.pytest.org/en/stable/explanation/goodpractices.html
- Playwright Docker: https://playwright.dev/docs/docker
