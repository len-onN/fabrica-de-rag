# Escopos Detalhados das Branches

## Como usar

Este documento detalha a realizacao esperada de cada branch inicial. Ele deve ser atualizado antes da abertura de cada branch, transformando a linha correspondente em mini-especificacao executavel.

Cada branch deve registrar:

- objetivo;
- realizacao;
- padroes e decisoes;
- testes e verificacoes;
- criterio de fechamento.

## Grupo 0: planejamento

### `planejamento/documentacao`

Objetivo:

- Consolidar proposta, MVP, ADRs, padroes iniciais, estrategia operacional, skills/agentes, registro, diario, plano de branches e estrategia de testes.

Realizacao:

- Organizar documentos de base.
- Registrar fluxo `develop -> branch -> PR`.
- Definir politica de skills/fontes.
- Definir plano de branches e testabilidade.

Testes/verificacoes:

- Revisao de links internos.
- Consistencia de nomes de branches.
- Registro de bordo atualizado.

Fechamento:

- PR para `develop` com camada documental inicial pronta.

## Grupo 1: fundacao tecnica testavel

### `docs/plano-tecnico-mvp`

Status:

- Incorporada em `planejamento/documentacao`.

Objetivo:

- Fechar decisoes tecnicas minimas para iniciar codigo sem ambiguidade.

Realizacao:

- Confirmar estrutura de monorepo.
- Definir versoes iniciais das stacks.
- Escolher ferramenta de migration.
- Definir padrao REST, erro, paginacao e versionamento.
- Definir estrategia inicial de testes e comandos.
- Registrar decisoes em ADRs quando necessario.

Padroes/decisoes:

- Modular monolith no Spring Boot.
- Angular 22.x com Node 24 LTS.
- Spring Boot 4.1.x com Java 21 LTS.
- Python 3.13.x para worker.
- PostgreSQL 18.x e Qdrant 1.18.x.
- Angular feature-first.
- Worker Python com Pydantic.
- MCP server em TypeScript/Node 24 como adaptador fino.
- Flyway como decisao inicial de migrations, salvo bloqueio.
- Compose base/dev/e2e.

Testes/verificacoes:

- Checklist de decisoes completo.
- Links para fontes oficiais.
- Registro de bordo atualizado.

Fechamento:

- Os portoes de planejamento do MVP podem comecar sem decisoes bloqueantes de stack.

### `docs/modelo-dados-contratos-mvp`

Objetivo:

- Fechar modelo relacional, contratos REST/worker/Qdrant/eventos e estrategia OpenAPI.

Realizacao:

- Mapear entidades do MVP.
- Definir IDs, workspace scope, constraints e indices iniciais.
- Definir DTOs REST e exemplos JSON.
- Definir contratos Pydantic do worker.
- Definir payload minimo Qdrant.
- Definir schema base de eventos locais.

Testes/verificacoes:

- Checklist de cobertura contra `mvp-interface-e-api.md`.
- Cada entidade escopada tem regra de isolamento.
- Cada contrato tem exemplo em `tests/contracts` planejado.

Fechamento:

- Branches de backend, worker, Qdrant e analytics implementam contratos ja definidos.

### `docs/arquitetura-ingestao-rag`

Objetivo:

- Fechar arquitetura de ingestao, state machine, reprocessamento e indexacao.

Realizacao:

- Definir estados de documento e ingest run.
- Definir etapas do pipeline.
- Definir retry, cancelamento, erro estruturado e logs.
- Definir idempotencia de reprocessamento/reindexacao.
- Definir layout de storage local.

Testes/verificacoes:

- Tabela de transicoes valida.
- Casos de falha por etapa documentados.
- Semantica de retry/cancel/reindexacao definida.

Fechamento:

- Branches de ingestao implementam uma orquestracao planejada.

### `docs/seguranca-permissoes-mvp`

Objetivo:

- Fechar auth local, permissoes, workspace scope e guardrails de API/MCP.

Realizacao:

- Definir bootstrap/login/logout.
- Definir sessao/token local.
- Definir matriz role x acao.
- Definir testes obrigatorios de autorizacao.
- Definir regras de MCP/API local.

Testes/verificacoes:

- Cada endpoint sensivel tem permissao minima.
- Cada recurso do MVP tem workspace scope.
- Matriz de roles cobre owner/admin/curator/member/viewer/agent.

Fechamento:

- A primeira fatia vertical implementa seguranca sem improviso.

### `docs/algoritmos-rag-mvp`

Objetivo:

- Especificar algoritmos centrais antes de implementa-los.

Realizacao:

- Especificar numeracao por ancoras/segmentos.
- Especificar chunking, heading path e overlap.
- Especificar busca, context builder, citacoes e budgets.
- Definir fixtures e casos-limite.

Testes/verificacoes:

- Cada algoritmo tem entrada, saida, invariantes, complexidade e fixtures.
- Casos-limite do MVP estao cobertos.

Fechamento:

- Branches de algoritmo passam a implementar especificacao, nao descobri-la.

### `docs/analytics-observabilidade-mvp`

Objetivo:

- Fechar analytics local, eventos, logs, retencao e dashboard minimo.

Realizacao:

- Definir taxonomia versionada de eventos.
- Definir retencao, export/delete e privacidade.
- Definir logs de job e erros por pagina.
- Definir metricas e agregacoes do dashboard.

Testes/verificacoes:

- Eventos tem schema.
- Campos proibidos estao listados.
- Dashboard tem fixtures planejadas.

Fechamento:

- Branches de analytics e observabilidade implementam taxonomia aprovada.

### `chore/workspace-fundacao`

Objetivo:

- Criar a estrutura raiz do repositorio.

Realizacao:

- Criar diretorios `apps`, `infra`, `tests`, `scripts`.
- Prever `apps/api`, `apps/web`, `apps/worker` e `apps/mcp`.
- Criar arquivos base de convencao.
- Criar comandos de verificacao placeholder que falham claramente quando uma stack ainda nao existir.
- Documentar como navegar a estrutura.

Padroes/decisoes:

- Monorepo.
- Separacao por aplicacao.
- Scripts na raiz como interface operacional.

Testes/verificacoes:

- Comando raiz de verificacao existe.
- Estrutura documentada.
- Nenhum segredo ou arquivo local indevido versionado.

Fechamento:

- Repositorio tem esqueleto preparado para receber as apps principais.

### `build/dev-runtime-compose`

Objetivo:

- Criar runtime local com Docker Compose.

Realizacao:

- Criar `infra/compose/compose.yml`.
- Criar `infra/compose/compose.dev.yml` se necessario.
- Subir Postgres e Qdrant.
- Preparar placeholders ou configuracoes para api, web e worker conforme ja existirem.
- Criar health checks basicos.

Padroes/decisoes:

- Compose base para servicos essenciais.
- Profiles/overrides para dev/e2e.
- Volumes nomeados para dev e efemeros para e2e.

Testes/verificacoes:

- `docker compose config` valida.
- Postgres responde.
- Qdrant responde.
- Comando de teardown documentado.

Fechamento:

- Dependencias locais estao reproduziveis.

### `chore/backend-spring-base`

Objetivo:

- Criar base Spring Boot executavel e testavel.

Realizacao:

- Criar projeto backend.
- Configurar profiles `dev`, `test`, `e2e`.
- Criar health endpoint.
- Configurar datasource Postgres.
- Configurar Flyway baseline.
- Criar tratamento de erro inicial.
- Criar estrutura de pacotes.

Padroes/decisoes:

- Controllers finos.
- Application services.
- Dominio separado de DTOs.
- `ProblemDetail` ou formato equivalente para erros.

Testes/verificacoes:

- Teste de contexto.
- Teste de health.
- Migration aplica em banco de teste.
- Comando de teste backend documentado.

Fechamento:

- API base sobe localmente e em teste.

### `chore/frontend-angular-base`

Objetivo:

- Criar base Angular executavel e testavel.

Realizacao:

- Criar app Angular.
- Configurar roteamento inicial.
- Criar shell visual.
- Criar tokens de tema claro/escuro.
- Criar client HTTP base.
- Criar estrutura por features.

Padroes/decisoes:

- Standalone components.
- Signals para estado local.
- Services para API.
- UI sem landing page.

Testes/verificacoes:

- Build passa.
- Teste de shell/render basico.
- Tema inicial nao quebra contraste essencial.

Fechamento:

- Frontend renderiza shell e esta pronto para primeira tela real.

### `chore/worker-python-base`

Objetivo:

- Criar worker Python executavel e testavel.

Realizacao:

- Criar `pyproject.toml`.
- Criar layout `src`.
- Criar FastAPI app.
- Criar health endpoint.
- Criar contratos Pydantic base.
- Criar container do worker.

Padroes/decisoes:

- `src` layout.
- Testes com pytest.
- API interna simples.
- Separacao entre endpoint e servico.

Testes/verificacoes:

- Pytest passa.
- TestClient valida health.
- Container inicia.

Fechamento:

- Worker responde health e pode ser chamado pela API.

### `test/smoke-stack-local`

Objetivo:

- Provar que a stack local sobe e conversa minimamente.

Realizacao:

- Criar script de smoke.
- Subir compose.
- Validar health de api, worker, Postgres e Qdrant.
- Validar shell web quando existir.

Padroes/decisoes:

- Smoke deve ser rapido.
- Logs devem ajudar a diagnosticar falha.

Testes/verificacoes:

- Script retorna exit code correto.
- Falha de servico falha o smoke.

Fechamento:

- Ambiente local tem prova objetiva de funcionamento.

## Grupo 2: primeira fatia vertical

### `feat/auth-bootstrap-workspaces`

Objetivo:

- Criar bootstrap local, usuario/admin, workspace padrao, membership e roles basicas.

Realizacao:

- Modelar tabelas de usuarios/workspaces/memberships.
- Criar bootstrap do primeiro admin.
- Criar autorizacao backend por workspace.
- Criar endpoints minimos.
- Criar UI minima de setup/workspace.

Padroes/decisoes:

- Roles por workspace.
- Backend valida permissao.
- `workspace_id` como fronteira de dados.

Testes/verificacoes:

- Migration.
- Teste de autorizacao.
- Dois workspaces nao vazam dados.
- UI cobre estado inicial.

Fechamento:

- Primeira fatia vertical prova UI + API + banco + permissao.

### `feat/workspace-dashboard-minimo`

Objetivo:

- Criar dashboard inicial autenticado do workspace.

Realizacao:

- Mostrar estado vazio.
- Mostrar navegacao principal.
- Consumir API real.
- Preparar areas para colecoes, documentos, jobs e analytics.

Padroes/decisoes:

- Estados vazios claros.
- Dados sempre escopados por workspace.

Testes/verificacoes:

- Teste de API.
- Teste de componente ou Playwright simples quando e2e ja existir.

Fechamento:

- Usuario entra no workspace e ve estado inicial real.

### `feat/workspace-settings-mvp`

Objetivo:

- Criar configuracoes de workspace para ingestao, analytics, acesso e API local.

Realizacao:

- Criar tela `/w/:workspaceId/settings`.
- Persistir nome/finalidade do workspace.
- Persistir defaults de ingestao e contexto.
- Configurar historico, retencao, export/delete de analytics local.
- Exibir role/acesso e status da API local.

Testes/verificacoes:

- Configuracoes persistem por workspace.
- Defaults aparecem no upload e laboratorio.
- Usuario sem permissao nao altera settings.
- Export/delete respeitam workspace scope.

Fechamento:

- Workspace tem configuracao MVP operavel antes dos fluxos documentais crescerem.

## Grupo 3: nucleo documental

### `feat/colecoes-documentos-core`

Objetivo:

- Criar entidades centrais de colecoes, documentos e metadados.

Realizacao:

- Criar schema relacional inicial.
- Criar CRUD minimo de colecoes.
- Criar registro de documentos sem processamento completo.
- Criar storage local inicial para arquivos.

Padroes/decisoes:

- SQL como fonte da verdade.
- Workspace scope obrigatorio.
- Storage guarda binarios; banco guarda metadados.

Testes/verificacoes:

- Migrations.
- Repository/API tests.
- Isolamento por workspace.

Fechamento:

- Colecoes e documentos existem como recursos reais.

### `feat/ingestao-upload-pdf`

Objetivo:

- Implementar upload de PDF e run inicial de ingestao.

Realizacao:

- Validar arquivo.
- Persistir PDF no storage local.
- Criar `ingest_run`.
- Expor status inicial.
- UI de upload minima.

Padroes/decisoes:

- PDF como unica fonte do MVP.
- Upload nao executa processamento pesado sincrono.

Testes/verificacoes:

- Upload valido.
- Arquivo invalido.
- Storage e metadados consistentes.
- Status da run.

Fechamento:

- Usuario consegue enviar PDF e acompanhar run inicial.

### `feat/ingestao-runs-operacao`

Objetivo:

- Criar operacao observavel de runs de ingestao.

Realizacao:

- Criar rota de status de run.
- Exibir etapas, progresso, parametros, duracao, erros e logs.
- Implementar cancelamento e retry conforme state machine planejada.
- Registrar eventos locais de run.

Testes/verificacoes:

- Transicoes validas de status.
- Cancel/retry respeitam regras.
- Logs nao vazam conteudo sensivel.
- Workspace scope preservado.

Fechamento:

- Usuario consegue acompanhar, cancelar e tentar novamente uma run.

### `feat/worker-pdf-inspect`

Objetivo:

- Integrar worker para inspecao basica de PDF.

Realizacao:

- Criar endpoint interno `pdf/inspect`.
- Extrair numero de paginas e metadados basicos.
- Criar contrato Spring -> worker.
- Persistir resultado inicial.

Padroes/decisoes:

- Pydantic para contrato do worker.
- Erros estruturados.
- Timeout explicito.

Testes/verificacoes:

- Teste worker com PDF fixture.
- Teste contrato Spring -> worker.
- Falha de PDF malformado.

Fechamento:

- Run de ingestao registra inspecao real do PDF.

### `feat/worker-pdf-render-ocr-base`

Objetivo:

- Adicionar render de paginas e OCR opcional basico ao worker.

Realizacao:

- Implementar `pdf/render-page`.
- Implementar ou completar `pdf/extract-text`.
- Implementar `pdf/ocr` com modo basico configuravel.
- Persistir artefatos de preview no storage local.
- Registrar qualidade textual por pagina.

Testes/verificacoes:

- Render gera artefato esperado.
- OCR desligado nao executa OCR.
- OCR forcado marca pagina corretamente.
- PDF malformado retorna erro estruturado.
- Limites de tempo/memoria sao observados.

Fechamento:

- Mapa de paginas e preview de citacao podem usar fonte visual real.

### `feat/paginas-numeracao`

Objetivo:

- Implementar mapa de paginas e numeracao impressa.

Realizacao:

- Criar tabelas de paginas.
- Registrar pagina fisica.
- Permitir ancora de pagina impressa.
- Inferir sequencia simples.
- UI minima do mapa.

Padroes/decisoes:

- Pagina fisica e impressa sao conceitos distintos.
- Algoritmo deve registrar invariantes e casos-limite.

Testes/verificacoes:

- Sequencia simples.
- Capa/prefacio.
- Reinicio ou segmento quando incluido.
- Pagina sem numeracao.

Fechamento:

- Citacoes podem usar pagina fisica e impressa.

## Grupo 4: recuperacao e vetores

### `feat/chunking-semantico`

Objetivo:

- Criar chunking inicial e persistencia de chunks.

Realizacao:

- Definir algoritmo inicial.
- Gerar chunks por bloco/paragrafo.
- Registrar heading path quando disponivel.
- Aplicar overlap por perfil.
- Persistir chunks.

Padroes/decisoes:

- Chunk recuperavel nao e contexto final.
- Invariantes do algoritmo documentadas.

Testes/verificacoes:

- Texto curto.
- Texto longo.
- Limite de tamanho.
- Overlap.
- Ordem e proveniencia.

Fechamento:

- Documento gera chunks rastreaveis.

### `feat/embeddings-base`

Objetivo:

- Definir e implementar contrato inicial de embeddings.

Realizacao:

- Implementar provider/modelo inicial ou adapter mockado definido em `docs/algoritmos-rag-mvp`.
- Registrar dimensao, metrica e versao.
- Criar interface de geracao.
- Preparar retry/erro sem travar ingestao.

Padroes/decisoes:

- Embedding versionado.
- Custo/rede isolados por adapter.

Testes/verificacoes:

- Adapter mockado.
- Contrato de dimensao.
- Erro controlado.

Fechamento:

- Chunks podem receber embedding versionado.

### `feat/qdrant-indexacao`

Objetivo:

- Indexar embeddings no Qdrant.

Realizacao:

- Criar colecao.
- Definir payload minimo.
- Upsert/delete.
- Filtros por workspace/colecao/documento.
- Registrar status de indexacao.

Padroes/decisoes:

- Qdrant e indice derivado.
- Filtros obrigatorios impedem vazamento.

Testes/verificacoes:

- Criacao de colecao.
- Upsert.
- Busca filtrada.
- Delete/reindexacao inicial.

Fechamento:

- Chunks ficam pesquisaveis no Qdrant sem perder escopo.

### `feat/ingestao-pipeline-indexacao`

Objetivo:

- Orquestrar a ingestao completa ate indexacao vetorial.

Realizacao:

- Encadear inspect, revisao de paginas, extracao/OCR, chunking, embeddings e Qdrant.
- Atualizar `ingest_run` por etapa.
- Aplicar retry/cancelamento.
- Suportar reprocessamento e reindexacao inicial.
- Garantir idempotencia contra duplicacao de chunks e pontos.

Testes/verificacoes:

- Fluxo completo com PDF fixture.
- Falha intermediaria deixa run diagnosticavel.
- Retry nao duplica chunks/pontos.
- Cancelamento respeita estado.

Fechamento:

- Documento passa de upload a indexado com proveniencia e status claros.

### `feat/busca-vetorial-base`

Objetivo:

- Buscar chunks por pergunta.

Realizacao:

- Gerar embedding da pergunta.
- Consultar Qdrant.
- Buscar metadados no SQL.
- Retornar chunks ordenados com citacao minima.

Padroes/decisoes:

- SQL enriquece resultado.
- Qdrant nao responde sozinho ao usuario.

Testes/verificacoes:

- Busca retorna chunk esperado em fixture.
- Workspace errado nao retorna dado.
- Sem resultados retorna estado claro.

Fechamento:

- API consegue recuperar ancoras.

### `feat/chunks-navegador`

Objetivo:

- Criar navegador de chunks para inspecao, filtros, vizinhos e feedback local.

Realizacao:

- Criar rota/tab de chunks da colecao.
- Listar trecho, documento, pagina PDF, pagina impressa, heading path, token count e status.
- Filtrar por documento, pagina, label, status e busca textual simples.
- Abrir origem.
- Ver vizinhos anterior/proximo.

Testes/verificacoes:

- Filtros retornam chunks esperados.
- Workspace errado nao ve chunks.
- Vizinho respeita ordem e documento.
- UI cobre vazio, loading e erro.

Fechamento:

- Usuario consegue auditar chunks sem depender apenas do laboratorio.

## Grupo 5: laboratorio e contexto

### `feat/context-builder-base`

Objetivo:

- Montar contexto expandido por vizinhos.

Realizacao:

- Receber chunks ancoras.
- Expandir before/after conforme politica.
- Aplicar budget.
- Deduplicar.
- Preservar citacoes.

Padroes/decisoes:

- Context builder separado da busca.
- Algoritmo documentado com complexidade.

Testes/verificacoes:

- Budget pequeno.
- Chunks duplicados.
- Vizinho ausente.
- Ordem final.

Fechamento:

- Sistema monta contexto previsivel.

### `feat/laboratorio-recuperacao`

Objetivo:

- Criar laboratorio de recuperacao.

Realizacao:

- Tela de pergunta.
- Mostrar chunks recuperados.
- Mostrar contexto expandido.
- Mostrar citacoes.
- Registrar eventos basicos.

Padroes/decisoes:

- Transparencia antes de automacao.
- UI mostra o que foi enviado ao modelo.

Testes/verificacoes:

- API com fixture.
- UI renderiza chunks/contexto.
- Estado sem resultado.

Fechamento:

- Usuario consegue testar a qualidade de recuperacao.

### `feat/resposta-rag-base`

Objetivo:

- Gerar resposta RAG com grounding, usando provider real ou adapter mockado definido em `docs/algoritmos-rag-mvp`.

Realizacao:

- Adapter de LLM.
- Prompt base.
- Resposta com citacoes.
- Fallback quando nao houver evidencia suficiente.

Padroes/decisoes:

- LLM nao substitui fonte.
- Mock por padrao em testes.

Testes/verificacoes:

- LLM mockado.
- Citacao obrigatoria.
- Sem evidencia.

Fechamento:

- Pergunta pode retornar resposta citada quando habilitada.

### `feat/citacoes-preview-fonte`

Objetivo:

- Permitir abrir a fonte original de uma citacao.

Realizacao:

- Criar componente reutilizavel de citacao.
- Abrir pagina/preview a partir de chunk ou resposta.
- Mostrar documento, pagina PDF, pagina impressa, trecho e contexto minimo.
- Integrar laboratorio, resposta RAG e navegador de chunks.
- Tratar fallback textual quando render nao existir.

Testes/verificacoes:

- Citacao abre fonte correta.
- Workspace errado nao acessa fonte.
- Pagina fisica e impressa aparecem quando existem.
- Fallback textual funciona sem render.

Fechamento:

- Usuario consegue validar a evidencia por tras de uma resposta.

## Grupo 6: analytics, agentes e publicacao

### `feat/analytics-eventos-base`

Objetivo:

- Criar taxonomia e armazenamento de eventos locais.

Realizacao:

- Definir schema versionado.
- Registrar eventos de ingestao/laboratorio.
- Criar retencao inicial.
- Evitar conteudo sensivel por padrao.

Padroes/decisoes:

- Analytics local.
- Eventos sem telemetria remota.

Testes/verificacoes:

- Schema valido.
- Evento gravado.
- Workspace scope.

Fechamento:

- Fluxos principais emitem eventos locais.

### `feat/analytics-dashboard-base`

Objetivo:

- Criar dashboard local minimo.

Realizacao:

- Agregar falhas, latencia, runs, consultas e feedback.
- UI com filtros basicos.
- Estado vazio.

Padroes/decisoes:

- Dashboard operacional, nao decorativo.
- Dados locais do workspace.

Testes/verificacoes:

- Agregacao com fixture.
- UI estado vazio e com dados.

Fechamento:

- Usuario enxerga qualidade e falhas do RAG.

### `feat/api-rag-publica`

Objetivo:

- Publicar API HTTP de consulta RAG.

Realizacao:

- Endpoint autenticado.
- Workspace scope.
- Limites.
- Resposta com chunks/contexto/citacoes.
- Eventos de analytics.

Padroes/decisoes:

- API versionada.
- Permissoes separadas.
- Erros padronizados.

Testes/verificacoes:

- Contrato.
- Autorizacao.
- Rate/limite basico quando definido.
- Isolamento por workspace.

Fechamento:

- Clientes externos conseguem consultar RAG com seguranca minima.

### `feat/mcp-tools-base`

Objetivo:

- Criar primeiras ferramentas MCP.

Realizacao:

- Implementar `search_chunks`.
- Implementar `get_chunk`.
- Implementar `expand_context`.
- Implementar `ask_rag` se resposta estiver habilitada.
- Registrar analytics.

Padroes/decisoes:

- Ferramentas guiadas.
- Schemas explicitos.
- Menor privilegio.
- Sem SQL livre.
- TypeScript/Node 24 em `apps/mcp`.
- MCP server chama contratos do backend e nao acessa Postgres/Qdrant diretamente.

Testes/verificacoes:

- Schema de tool.
- Permissoes.
- Workspace scope.
- Limites e falhas.

Fechamento:

- Agentes conseguem investigar bases por ferramentas controladas.

## Grupo 7: endurecimento do MVP

### `test/e2e-mvp-ingestao-recuperacao`

Objetivo:

- Criar e2e do fluxo principal do MVP.

Realizacao:

- Subir stack via compose e2e.
- Executar Playwright.
- Usar fixtures pequenas.
- Cobrir workspace, colecao, upload, ingestao, chunks, busca e laboratorio.

Padroes/decisoes:

- E2E curto.
- Sem rede externa.
- Sem LLM real por padrao.

Testes/verificacoes:

- Pipeline e2e passa em ambiente limpo.
- Falhas deixam logs suficientes.

Fechamento:

- MVP tem prova ponta a ponta.

### `perf/ingestao-e-contexto`

Objetivo:

- Medir e ajustar gargalos reais.

Realizacao:

- Medir tempos de ingestao, chunking, embeddings, Qdrant e context builder.
- Identificar gargalos.
- Corrigir apenas gargalos observados.

Padroes/decisoes:

- Medir antes de otimizar.
- Registrar antes/depois.

Testes/verificacoes:

- Benchmark pequeno.
- Comparativo documentado.

Fechamento:

- Performance melhora ou risco e documentado com dados.

### `docs/execucao-local-mvp`

Objetivo:

- Documentar execucao local do MVP.

Realizacao:

- Comandos de setup.
- Variaveis.
- Compose.
- Testes.
- Troubleshooting.

Padroes/decisoes:

- Novo usuario deve conseguir subir o projeto.
- Docs devem refletir comandos reais.

Testes/verificacoes:

- Validar comandos em ambiente limpo quando possivel.
- Conferir links.

Fechamento:

- README/documentacao permite executar e testar localmente.
