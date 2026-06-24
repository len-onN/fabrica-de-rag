# Plano de Branches e Escopos

## Visao

O desenvolvimento deve comecar por uma base executavel e testavel, mas sem tentar construir todas as camadas em profundidade antes dos casos de uso.

A estrategia recomendada e:

```text
fundacao minima testavel
-> primeira fatia vertical simples
-> fatias verticais por caso de uso
-> endurecimento tecnico por risco real
```

Isso significa que Angular, Spring Boot, Python worker, MCP server, PostgreSQL, Qdrant e Docker Compose devem nascer cedo, mas inicialmente com contratos pequenos, health checks, testes de fumaca e exemplos minimos. Depois, cada branch amplia a aplicacao por uma fatia de comportamento observavel.

O detalhamento operacional de cada branch vive em [Escopos detalhados das branches](escopos-detalhados-branches.md). As decisoes tecnicas iniciais vivem em [Plano tecnico do MVP](plano-tecnico-mvp.md), a cobertura funcional do MVP vive em [Cobertura do MVP pelo plano de branches](cobertura-mvp-branches.md), e a estrategia de testabilidade vive em [Estrategia de testes](estrategia-de-testes.md).

O planejamento vivo por branch fica na pasta [`branches-plan`](../branches-plan/README.md). Cada branch tem seu proprio arquivo `.md`, com o que ja esta definido, o que falta definir e como a implementacao deve ser conduzida.

## Principio de testabilidade

Cada branch deve deixar uma forma concreta de verificar progresso.

Tipos de verificacao esperados:

- teste unitario para regra local;
- teste de contrato para DTO, API, evento ou ferramenta;
- teste de integracao para banco, migration, Qdrant ou worker;
- teste de fumaca para servicos subindo;
- teste end-to-end pequeno para fluxo critico;
- verificacao manual documentada quando automacao ainda nao existir.

Uma branch que apenas adiciona estrutura deve entregar pelo menos health check, comando de execucao e teste de fumaca.

## Forma mais eficiente com agentes

Agentes funcionam melhor quando recebem escopo estreito, contexto ativado e criterio de aceite objetivo.

Regra importante: decisoes estruturais complexas devem ser tomadas em branches de planejamento antes da implementacao. Uma feature branch pode ajustar detalhes locais, mas nao deve escolher sozinha modelo relacional, contratos publicos, state machine de ingestao, politica de seguranca, algoritmo central ou taxonomia de analytics.

Fluxo por branch:

1. Ativar contexto local: README, ADRs, planejamento, padroes, estrategia operacional, registro de bordo.
2. Ativar skills/fontes oficiais por stack: Angular, Spring, Python, Postgres, Qdrant, MCP ou seguranca conforme a branch.
3. Escrever mini-especificacao da branch: objetivo, fora de escopo, contratos, testes e colateralidades.
4. Declarar pontos de extensao, invariantes preservadas e riscos de regressao lateral.
5. Implementar em passos pequenos.
6. Rodar verificacoes.
7. Fazer revisao de agente em modo critico: bugs, riscos, testes faltantes, quebras de contrato e aderencia ao gate SOLID/OCP-LSP-IoC.
8. Atualizar registro de bordo e diario de bordo.
9. Abrir PR para `develop`.

Padrao de colaboracao:

- um agente ou sessao atua como implementador;
- outro passe, quando possivel, atua como revisor;
- a documentacao local prevalece sobre recomendacoes genericas;
- decisoes novas viram ADR ou atualizacao de padrao;
- prompts, ferramentas e respostas recuperadas sao insumos, nao autoridade final.

## Portao SOLID/OCP-LSP-IoC

Cada branch deve proteger o projeto contra regressao lateral. Na pratica, isso significa que comportamento novo deve entrar por extensao de contratos, adapters, providers, policies, strategies, schemas ou use cases, preservando fluxos ja estabilizados, garantindo substituibilidade de implementacoes e mantendo dependencias concretas fora do core.

Antes do PR, a branch deve responder:

- quais contratos existentes foram preservados;
- quais pontos de extensao foram usados ou criados;
- quais implementacoes novas precisam ser substituiveis por contrato;
- onde ocorre a injecao/composicao das dependencias concretas;
- quais modulos fora do escopo precisaram mudar e por que;
- quais testes protegem compatibilidade e isolamento por workspace;
- quais migracoes, eventos, payloads ou schemas exigem compatibilidade.

Se a resposta mostrar uma decisao estrutural nao planejada, a branch deve parar e atualizar o portao `docs/*` dono antes de seguir.

## Desenho das branches

### Grupo 0: planejamento operacional

| Ordem | Branch | Escopo | Testabilidade/fechamento |
| --- | --- | --- | --- |
| 0 | `planejamento/documentacao` | Consolidar proposta, ADRs, MVP, padroes, estrategia operacional, skills/agentes, registro e diario de bordo. | Documentacao navegavel, registro atualizado e plano de branches inicial. |

### Grupo 1A: portoes de planejamento do MVP

Estas branches devem fechar decisoes arquiteturais antes das branches de codigo que dependem delas.
Cada portao tambem deve explicitar pontos de extensao, contratos de compatibilidade e riscos de mudanca lateral que as branches futuras nao poderao decidir sozinhas.

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 1.1 | `docs/modelo-dados-contratos-mvp` | Modelo relacional, IDs, DTOs REST, Pydantic, payload Qdrant, eventos e OpenAPI. | Migrations reais e endpoints reais. | Checklist de entidades/contratos cobrindo o MVP. |
| 1.2 | `docs/arquitetura-ingestao-rag` | State machine de ingestao, etapas, retry/cancel, idempotencia, storage e reindexacao. | Fila distribuida e pipeline real. | Transicoes, falhas e reprocessamento especificados. |
| 1.3 | `docs/seguranca-permissoes-mvp` | Auth local, sessao/token, roles, workspace scope, API/MCP e guardrails. | OIDC, MFA, convites e RLS. | Matriz de permissoes e testes obrigatorios definidos. |
| 1.4 | `docs/algoritmos-rag-mvp` | Numeracao, chunking, busca, context builder, citacoes, budgets e complexidade. | Reranking, busca hibrida e multimodal. | Algoritmos com entradas, saidas, invariantes e fixtures. |
| 1.5 | `docs/analytics-observabilidade-mvp` | Eventos, retencao, export/delete, logs de jobs, metricas e privacidade. | Telemetria remota e ciencia de dados avancada. | Taxonomia de eventos e dashboard minimo especificados. |
| 1.6 | `docs/interpretacao-imagens-tabelas-pdf` | Camada de interpretacao de imagens/tabelas em PDFs, assets, elementos, vision interpreter LLM/VLM, source locator, contratos e relacoes com chunks/citacoes. | Embeddings visuais, busca multimodal por imagem e curadoria profunda de regioes. | Contratos de elementos/assets/tabelas/interpretacoes e fixtures definidos. |

### Grupo 1B: fundacao tecnica testavel

Essas branches criam o esqueleto executavel. Elas devem ser pequenas o bastante para revisar, mas coordenadas para chegar rapidamente a um ambiente local que sobe.

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 1 | `docs/plano-tecnico-mvp` | Incorporada em `planejamento/documentacao`: decidiu estrutura de repo, versoes iniciais, migrations, padrao REST, estrategia de testes, MCP runtime e limites entre stacks. | Implementacao de codigo de produto. | ADRs/padroes atualizados e checklist de decisoes pronto para a fundacao. |
| 2 | `chore/workspace-fundacao` | Criar estrutura raiz do projeto, diretorios de frontend/backend/worker/mcp, convencoes, scripts locais e arquivos base. | Funcionalidades de negocio. | Comando de verificacao raiz e estrutura documentada. |
| 3 | `build/dev-runtime-compose` | Criar Docker Compose inicial com Postgres, Qdrant e servicos da aplicacao preparados para desenvolvimento. | Pipeline de ingestao real. | `docker compose` sobe dependencias e health checks basicos. |
| 4 | `chore/backend-spring-base` | Criar Spring Boot base, profiles, health endpoint, tratamento de erro inicial, configuracao de testes e conexao Postgres. | Modelo documental completo. | Testes de contexto, health e primeira migration vazia ou baseline. |
| 5 | `chore/frontend-angular-base` | Criar Angular base, roteamento inicial, layout shell, tokens visuais, tema claro/escuro inicial e cliente HTTP base. | Telas finais de produto. | Build/test do frontend e tela shell renderizando. |
| 6 | `chore/worker-python-base` | Criar worker Python base, API interna minima, Pydantic, health endpoint, estrutura de testes e container. | OCR, parsing real e embeddings. | Testes do worker e chamada de health pelo ambiente local. |
| 7 | `test/smoke-stack-local` | Criar teste de fumaca local atravessando frontend, backend, worker e dependencias quando possivel. | Fluxo de negocio completo. | Um comando valida que a stack sobe e servicos respondem. |

### Grupo 2: primeira fatia vertical

A primeira fatia vertical deve provar que UI, API, banco e autorizacao basica conversam.

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 8 | `feat/auth-bootstrap-workspaces` | Bootstrap local, usuario/admin inicial, workspace padrao, membership e roles basicas. | Convites, SSO, OAuth externo e MFA. | Testes de autorizacao, migration, endpoint e tela/estado minimo de workspace. |
| 9 | `feat/workspace-dashboard-minimo` | Dashboard inicial do workspace com navegacao real, estado vazio e leitura de dados autenticada. | Analytics completo e gestao avancada. | Teste de API + teste UI simples do estado vazio. |
| 9.1 | `feat/workspace-settings-mvp` | Configuracoes de workspace, defaults de ingestao, analytics local, acesso e API local. | Convites, chaves de API completas e provedores externos. | Configuracoes persistem, aplicam defaults e respeitam permissoes. |

### Grupo 3: nucleo documental

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 10 | `feat/colecoes-documentos-core` | Colecoes, documentos, storage local, metadados principais e escopo por workspace. | Upload/processamento completo de PDF. | Migrations, testes de isolamento por workspace e CRUD minimo. |
| 11 | `feat/ingestao-upload-pdf` | Upload de PDF, criacao de ingest run e status inicial. | Extracao completa, OCR e chunking. | Teste de upload, validacao de arquivo, storage e status da run. |
| 11.1 | `feat/ingestao-runs-operacao` | Status de runs, etapas, cancelamento, retry e logs. | Pipeline completo e fila. | State machine de run, transicoes e logs testados. |
| 12 | `feat/worker-pdf-inspect` | Integrar worker para inspecao basica de PDF: paginas, metadados e texto nativo simples. | OCR e layout avancado. | Teste de contrato Spring -> worker com PDF fixture pequeno. |
| 12.1 | `feat/worker-pdf-render-ocr-base` | Render de paginas, OCR opcional basico e qualidade textual por pagina. | OCR avancado, captions e tabelas complexas. | Render/OCR testados com fixtures e limites de tempo/memoria. |
| 12.2 | `feat/worker-pdf-visual-tables-base` | Interpretar imagens e tabelas em PDFs: detectar elementos, extrair assets/tabelas simples, chamar adapter visual quando habilitado e preservar bbox/source locator. | Embeddings visuais, busca multimodal por imagem e curadoria profunda de regioes. | Fixtures com tabela/figura geram elementos, assets, interpretacoes textuais e relacoes citaveis. |
| 13 | `feat/paginas-numeracao` | Mapa de paginas, pagina fisica, pagina impressa e ancoras de numeracao. | Inferencias complexas multi-segmento se nao couber. | Especificacao do algoritmo, testes de casos-limite e UI minima. |

### Grupo 4: recuperacao e vetores

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 14 | `feat/chunking-semantico` | Chunking inicial, heading path, overlap por perfil e persistencia de chunks. | Curadoria manual avancada. | Testes de chunking com fixtures e invariantes documentadas. |
| 15 | `feat/embeddings-base` | Definir provider/modelo inicial de embeddings e contrato de geracao. | Otimizacoes de custo e multiplos providers. | Testes com adapter mockado e contrato de embedding versionado. |
| 16 | `feat/qdrant-indexacao` | Criar colecao Qdrant, payload minimo, upsert/delete e filtros por workspace/colecao. | Conectores externos. | Teste de integracao Qdrant e verificacao de filtros obrigatorios. |
| 16.1 | `feat/ingestao-pipeline-indexacao` | Orquestrar ingestao completa ate chunks, embeddings e Qdrant. | Fila distribuida e paralelismo sofisticado. | Fluxo completo com fixture, falhas por etapa e retry sem duplicacao. |
| 17 | `feat/busca-vetorial-base` | Buscar chunks por pergunta, filtros e ranking inicial. | Reranking e busca hibrida. | Testes de contrato e fixture pequena de recuperacao. |
| 17.1 | `feat/chunks-navegador` | Navegador de chunks com filtros, vizinhos, origem e feedback local basico. | Edicao manual e curadoria profunda. | Filtros, vizinhos, workspace scope e estados de UI testados. |

### Grupo 5: laboratorio e contexto

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 18 | `feat/context-builder-base` | Expandir contexto por vizinhos, relacoes diretas de imagem/tabela/legenda, budget e citacoes. | Expansao por secao/relacoes complexas multi-hop. | Especificacao do algoritmo e testes de budget/citacoes/elementos. |
| 19 | `feat/laboratorio-recuperacao` | Tela de laboratorio com pergunta, chunks recuperados, contexto expandido e citacoes. | Comparacao de perfis e benchmark avancado. | Teste de fluxo UI/API com fixtures. |
| 20 | `feat/resposta-rag-base` | Geracao de resposta com grounding e citacoes, usando provider real ou adapter mockado definido no planejamento. | Avaliacao automatica avancada. | Testes com LLM mockado e verificacao de citacoes. |
| 20.1 | `feat/citacoes-preview-fonte` | Abrir fonte original da citacao com pagina, trecho, label fisico/impresso e fallback textual. | Viewer PDF completo, crops e evidencias visuais complexas. | Citacao abre fonte correta e respeita workspace scope. |

### Grupo 6: analytics, agentes e publicacao

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 21 | `feat/analytics-eventos-base` | Taxonomia versionada de eventos, registro local e eventos de ingestao/laboratorio. | Dashboards sofisticados. | Testes de schema de evento e retencao inicial. |
| 22 | `feat/analytics-dashboard-base` | Dashboard local minimo de qualidade, falhas, latencia e feedback. | Ciencia de dados avancada. | Teste de agregacao e tela com dados fixture. |
| 23 | `feat/api-rag-publica` | API HTTP para consulta RAG com auth, workspace, limites e citacoes. | MCP completo. | Testes de permissao, contrato e isolamento. |
| 24 | `feat/mcp-tools-base` | Primeiras tools MCP: `search_chunks`, `get_chunk`, `expand_context`, `ask_rag`. | Ferramentas mutantes e administracao via MCP. | Testes de schema, permissao, limites e eventos de analytics. |

### Grupo 7: endurecimento do MVP

| Ordem | Branch | Escopo | Fora de escopo | Testabilidade/fechamento |
| --- | --- | --- | --- | --- |
| 25 | `test/e2e-mvp-ingestao-recuperacao` | E2E do fluxo principal: workspace, colecao, upload, ingestao, chunks, busca e laboratorio. | Cobertura exaustiva. | Fluxo automatizado ou roteiro manual verificavel se E2E ainda nao estiver maduro. |
| 26 | `perf/ingestao-e-contexto` | Medir e ajustar gargalos claros de ingestao, chunking, busca e context builder. | Micro-otimizacoes especulativas. | Metricas antes/depois registradas. |
| 27 | `docs/execucao-local-mvp` | Documentar execucao local, troubleshooting, variaveis e comandos de teste. | Documentacao comercial. | Novo usuario consegue subir a stack seguindo o README. |

## Regras para manter eficiencia

- Evitar branches horizontais muito longas, como "todo backend" ou "todo frontend".
- Criar fundacao suficiente para testar, nao fundacao perfeita.
- Fazer a primeira fatia vertical cedo.
- Toda branch de feature deve atravessar as camadas necessarias para entregar comportamento observavel.
- Usar mocks apenas onde o servico real geraria custo, instabilidade ou segredo.
- Introduzir testes no mesmo momento em que o contrato nasce.
- Aplicar o gate SOLID/OCP-LSP-IoC antes de fechar PR, principalmente quando uma branch tocar contrato, migration, evento, tool, adapter, provider, repository ou algoritmo central.
- Antes de cada branch, atualizar o registro de bordo com escopo, fontes e criterios de aceite.
- Depois de cada branch, atualizar o diario de bordo com a historia da etapa.

## Primeiro conjunto recomendado

Como `docs/plano-tecnico-mvp` foi incorporada em `planejamento/documentacao`, o primeiro pacote operacional restante deve fechar os portoes de planejamento antes do codigo:

1. `docs/modelo-dados-contratos-mvp`
2. `docs/arquitetura-ingestao-rag`
3. `docs/seguranca-permissoes-mvp`
4. `docs/algoritmos-rag-mvp`
5. `docs/analytics-observabilidade-mvp`
6. `docs/interpretacao-imagens-tabelas-pdf`
7. `chore/workspace-fundacao`
8. `build/dev-runtime-compose`
9. `chore/backend-spring-base`
10. `chore/frontend-angular-base`
11. `chore/worker-python-base`
12. `test/smoke-stack-local`
13. `feat/auth-bootstrap-workspaces`

Esse conjunto cria a base minima para desenvolver com agentes de forma eficiente: cada stack existe, cada contrato tem lugar, cada servico pode ser testado, e a primeira feature real ja nasce atravessando permissoes, banco, API e tela.
