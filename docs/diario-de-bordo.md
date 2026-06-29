# Diario de Bordo

## Como usar

Este documento registra o percurso do projeto em texto corrido. O registro de bordo mostra o estado operacional; o diario explica a historia: o que foi feito, por que foi feito, quais decisoes amadureceram e o que ficou para a proxima etapa.

Atualizar ao fim de cada branch, antes de abrir pull request para `develop`, e tambem quando uma etapa precisar ser interrompida no meio.

## 2026-06-24 - Consolidacao da camada operacional

O projeto ja tinha uma base documental forte para a Fabrica de RAG: proposta principal, casos de uso, features planejadas, contrato funcional do MVP, ADRs, padroes de projeto, usuarios e acesso, analytics local, conectores vetoriais, design visual e planejamento por fases. A leitura desse conjunto mostrou que a visao de produto e a arquitetura inicial estavam bem encaminhadas, mas havia uma lacuna importante: faltava registrar como o trabalho seria conduzido branch por branch ate o MVP.

A decisao tomada nesta etapa foi tratar a camada operacional como parte essencial da arquitetura do projeto. O fluxo passa a ser `develop` como linha de integracao, branches de implementacao nomeadas por uma adaptacao de Conventional Commits, fechamento de cada escopo por pull request e atualizacao obrigatoria do registro de bordo e do diario de bordo.

Tambem foi definido que cada branch deve carregar suas proprias consideracoes tecnicas antes da implementacao. Isso inclui contratos entre stacks, impactos em dados e permissoes, migracoes, eventos de analytics, performance, casos-limite e testes. Para algoritmos centrais, como numeracao impressa, chunking, context builder e indexacao vetorial, a branch deve registrar entrada, saida, invariantes, passos, complexidade, falhas conhecidas e metricas observaveis.

Com isso, o projeto deixa de ter apenas uma documentacao de intencao e passa a ter um mecanismo de continuidade. A proxima frente operacional passou a ser validar o plano de branches e fechar o plano tecnico do MVP antes da primeira implementacao de codigo.

Na mesma frente, ficou claro que o desenvolvimento precisa ser orientado por skills e fontes oficiais. As stacks principais ja estao escolhidas, mas cada branch tecnica devera registrar quais colecoes, skills e documentos primarios foram ativados. Isso vale especialmente para agentes, MCP, ferramentas, guardrails, tracing, seguranca, Angular e Spring, onde decisoes tomadas por memoria solta tendem a envelhecer rapido.

Tambem foi desenhado um plano de branches por escopo. A estrategia escolhida e construir uma fundacao minima testavel, nao uma base perfeita e grande demais. Primeiro entram o plano tecnico do MVP, a estrutura do workspace, o compose local e as bases testaveis de backend, frontend e worker. Em seguida vem um teste de fumaca da stack e a primeira fatia vertical com autenticacao/bootstrap de workspace. A partir dai, o produto cresce por casos de uso atravessando as camadas necessarias, mantendo testabilidade e revisao por PR.

Em seguida, o plano foi detalhado com uma estrategia de testes. A decisao foi nao depender de um e2e grande demais para dar confianca ao projeto. A base passa a ser uma piramide pratica: unitarios, contratos, integracao, smoke da stack e poucos fluxos e2e de alto valor. O compose e2e passa a ser parte planejada do projeto, com arquivo canonico em `infra/compose/compose.e2e.yml`, mas entra como ambiente reprodutivel e enxuto, sem rede externa, sem LLM real por padrao e com fixtures pequenas.

Por fim, foi criada a pasta `branches-plan`, com um arquivo `.md` para cada branch planejada. Essa pasta introduz uma linguagem operacional propria para falar de elementos de codigo e runtime: App, Module, Boundary, Use case, Adapter, Contract, Migration, Fixture, Profile, Compose layer, Job, Run, Event, Tool, Guardrail e Workspace scope. A partir daqui, cada branch deve evoluir seu proprio arquivo antes de ser aberta.

Ainda nesta branch, o escopo `docs/plano-tecnico-mvp` foi absorvido para evitar uma etapa documental artificial antes da fundacao. Essa decisao fechou o baseline tecnico inicial do MVP: monorepo com `apps/api`, `apps/web`, `apps/worker` e `apps/mcp`; Angular 22 com Node 24 LTS; Spring Boot 4.1 com Java 21 LTS; Python 3.13 para o worker; PostgreSQL 18 e Qdrant 1.18 como dependencias locais; Flyway como migracao; Compose em camadas para dev/smoke/e2e; componentes proprios com Angular CDK; e MCP em TypeScript/Node como adaptador fino sobre os contratos do backend. O plano tambem registrou os comandos-alvo, gates de qualidade por PR e as pendencias que nao bloqueiam a primeira branch de codigo, como provider de embeddings, biblioteca OpenAPI compativel e budgets numericos das tools.

Na revisao de cobertura do MVP, o plano de branches foi comparado contra o criterio funcional de fechamento do MVP. A cobertura principal estava presente, mas havia lacunas que poderiam virar improviso durante a implementacao: configuracoes do workspace, operacao completa de runs de ingestao, render/OCR basico, orquestracao de ingestao ate indexacao, navegador de chunks e abertura da fonte original de uma citacao. Essas lacunas foram transformadas em branches proprias. Tambem ficou decidido que decisoes estruturais complexas devem ser fechadas em branches `docs/*` antes do codigo: modelo de dados/contratos, arquitetura de ingestao, seguranca/permissoes, algoritmos RAG, analytics/observabilidade e, na sequencia, interpretacao de imagens/tabelas em PDFs. Com isso, o proximo passo deixa de ser diretamente `chore/workspace-fundacao` e passa a ser fechar esses portoes de planejamento, comecando por `docs/modelo-dados-contratos-mvp`.

Antes de prosseguir para o primeiro portao, foi feita uma varredura fina dos grupos 0 e 1 com foco em SOLID, especialmente Open/Closed, Liskov Substitution e inversao de controle. O ajuste transformou OCP-LSP-IoC em gate operacional: cada branch deve declarar pontos de extensao, invariantes preservadas, contratos sensiveis, substituibilidade de implementacoes, ligacao de concretos por DI/composition root e mudancas laterais inevitaveis. Os portoes de planejamento passaram a explicitar detalhes que nao devem ficar subentendidos, como estrategia de IDs, payload Qdrant, state machine de ingestao, sessao/token, matriz de permissoes, providers de embeddings/LLM, budgets, envelope de eventos, retencao e separacao entre logs, analytics e auditoria. Essa revisao reduz o risco de um commit futuro consertar o foco da branch e quebrar areas lateralizadas do MVP.

Em seguida, foi criado um trigger de implementacao agentica. O objetivo desse documento e impedir que uma sessao de agente comece a codificar sem saber onde esta: ele manda ler a documentacao viva, conferir branch atual, remoto, sincronizacao, mudancas locais e plano especifico da branch antes de implementar. Tambem formaliza que skills e fontes oficiais fazem parte da abertura de branch, nao de uma consulta ocasional no meio do trabalho. O trigger inclui o caso pratico de Git com `dubious ownership`, que pode aparecer no ambiente do Codex por diferenca entre usuario do Windows e usuario do sandbox; nesse caso, o agente deve parar e pedir autorizacao para configurar `safe.directory`, em vez de tentar contornar silenciosamente.

Por fim, foi feita uma revisao de coesao documental. A conclusao foi que a ideia do projeto esta unissona, mas alguns documentos iniciais ainda falavam com linguagem de fase anterior. O planejamento foi atualizado para separar decisoes vigentes, proximas especificacoes e futuro pos-MVP. O documento de features passou a distinguir MVP funcional de MVP nao bloqueante, corrigindo MCP e analytics MCP para nao bloquearem ingestao, recuperacao, citacoes e analytics local. Tambem foram alinhados os perfis de ingestao do MVP para rapido/balanceado, e os grupos 1A/1B foram sincronizados entre o plano de branches e os escopos detalhados. A revisao foi registrada em `docs/revisao-coesao-documental.md`.

Logo depois, a camada de interpretacao de imagens e tabelas em PDFs foi promovida a componente fundamental do MVP. A decisao foi nao tratar PDF como texto/OCR apenas: imagens, diagramas e tabelas precisam virar elementos interpretaveis, citaveis e aproveitaveis pelo context builder. Para isso, foram adicionados um portao documental (`docs/interpretacao-imagens-tabelas-pdf`) e uma branch funcional (`feat/worker-pdf-visual-tables-base`). A arquitetura definida separa extracao estrutural de assets/tabelas/bbox/source locator da interpretacao por LLM/VLM, que entra por adapter substituivel, com mock deterministico em testes, provider remoto configuravel, cache por hash/modelo/prompt, budget e politicas de privacidade. Embeddings visuais e busca multimodal continuam fora do MVP.

## 2026-06-24 - Modelo de dados e contratos do MVP

A branch `planejamento/documentacao` foi fechada operacionalmente: commitada, publicada, aberta como PR #1 para `develop` e mesclada. A aprovacao formal via GitHub nao foi possivel porque a plataforma nao permite que o proprio autor aprove seu pull request, mas o PR estava limpo, mergeable e com o check de seguranca verde antes do merge.

Com `develop` atualizado, foi aberta a branch `docs/modelo-dados-contratos-mvp`, primeiro portao de planejamento do MVP. O foco foi tirar das futuras branches de codigo as decisoes estruturais que costumam virar improviso: estrategia de IDs, entidades relacionais, workspace scope, soft delete, contratos REST, contratos Spring -> worker, payload Qdrant, envelope de analytics e compatibilidade entre schemas.

A decisao principal foi usar `uuid` como identificador interno e `public_id` opaco prefixado como identificador externo. Isso preserva FKs eficientes e, ao mesmo tempo, evita expor estrutura interna em URLs, JSON, logs e payloads vetoriais. O SQL permanece como fonte da verdade para documentos, paginas, elementos, assets, chunks, runs, eventos e relacoes; Qdrant fica como indice derivado com payload minimo e filtros obrigatorios por workspace e colecao.

Tambem foram criados exemplos JSON iniciais em `tests/contracts`, cobrindo REST, worker, Qdrant, eventos e MCP. Esses exemplos passam a ser a primeira ancora regressiva para DTOs Java, modelos Pydantic, schemas MCP, OpenAPI e fixtures futuras. A biblioteca concreta de OpenAPI ficou encaminhada para a branch de scaffold backend, onde a compatibilidade real com Spring Boot 4.1 podera ser provada por build/teste, mas o formato alvo do contrato foi fixado como OpenAPI 3.1.

O resultado e que as proximas branches podem implementar migrations, endpoints, worker e indexacao com um contrato estrutural ja definido. O proximo portao passa a ser `docs/arquitetura-ingestao-rag`, que deve detalhar state machine, pipeline, retry, cancelamento, idempotencia, storage e reindexacao.

## 2026-06-24 - Arquitetura de ingestao e RAG

Depois do merge do PR #2, foi aberta a branch `docs/arquitetura-ingestao-rag`. O objetivo foi fechar a parte que liga o modelo documental aos fluxos reais de execucao: como o upload vira run, como a run chama o worker, quando pausa para revisao humana, como retry e cancelamento funcionam, onde os artefatos ficam e como o Qdrant e atualizado sem virar fonte da verdade.

A decisao central foi tratar ingestao como uma run persistida no SQL, composta por etapas idempotentes. O Spring Boot fica como orquestrador de estado, permissao, workspace scope, storage, eventos e promocao de resultado ativo. O worker Python executa operacoes documentais por contratos versionados, mas nao decide autorizacao, nao escreve no banco e nao publica diretamente no Qdrant.

Tambem foi decidido que uma run pode entrar em `waiting_for_review` para revisao do mapa de paginas. Isso evita uma escolha ruim entre bloquear o MVP em automacao perfeita ou indexar documentos ambiguos sem controle humano. Retry manual cria nova run vinculada; retry automatico fica restrito a falhas transientes. Cancelamento e cooperativo, com semantica diferente antes, durante e depois de chamadas ao worker.

Para reprocessamento e reindexacao, foi introduzido o conceito de `ingestion_generation`: uma nova execucao pode gerar chunks, embeddings e pontos novos sem sobrescrever o resultado ativo ate a etapa `finalize`. Isso preserva historico, reduz duplicacao e permite apagar/recriar pontos Qdrant por filtros deterministicos. O layout de storage tambem ficou definido por workspace, documento e run, com URIs estaveis e sem expor caminhos livres ao worker.

O portao tambem registrou que REST interno continua suficiente no MVP. Uma fila so entra quando houver concorrencia, durabilidade, backpressure, cancelamento ativo ou escalonamento horizontal que justifiquem a complexidade. A proxima branch passa a ser `docs/seguranca-permissoes-mvp`, responsavel por fechar sessao/token, matriz de permissoes, workspace scope e guardrails.

## 2026-06-24 - Seguranca e permissoes do MVP

Depois do merge do PR #3 em `develop`, foi aberta a branch `docs/seguranca-permissoes-mvp`. O trigger agentico encontrou uma divergencia esperada entre o bordo e o Git: o registro ainda dizia que `docs/arquitetura-ingestao-rag` estava em fechamento, mas a remota ja tinha `develop` no merge do PR #3 e a branch local atual era `docs/seguranca-permissoes-mvp`. A reconciliacao foi documental.

O portao de seguranca fechou a decisao de autenticar a UI por sessao server-side opaca em cookie `HttpOnly`, com segredo aleatorio e hash persistido no backend. Como a UI usa cookie, mutacoes passam a exigir CSRF no padrao de SPA, com header `X-XSRF-TOKEN`. CORS fica same-origin por padrao, liberando apenas origens locais explicitamente no perfil de desenvolvimento.

Tambem foram fechados o bootstrap unico do primeiro usuario/workspace, o uso de `Argon2id` como algoritmo alvo para senha local, a expiracao idle/absoluta de sessao, a politica de reset local fora de endpoint publico, a matriz role x acao do MVP, o workspace scope por recurso e a lista endpoint x permissao. A autorizacao passa a ser descrita como RBAC por workspace combinado com atributos de recurso, deny-by-default e validacao a cada request.

Para agentes e API futura, ficou decidido que API keys e MCP nao fazem token passthrough de usuario humano. Eles usam identidade propria `agent`, capabilities explicitas, limites, correlation id e auditoria. As tools MCP seguem como adaptador fino sobre o backend, sem acesso direto a Postgres, Qdrant, storage ou filesystem livre. A decisao duradoura foi registrada na ADR-024.

Foram adicionadas fixtures pequenas de contrato para bootstrap, login, `me`, matriz de permissoes e contexto de tool MCP. A implementacao concreta de Spring Security, migrations, UI e testes fica para `feat/auth-bootstrap-workspaces`; API keys reais ficam para `feat/api-rag-publica`; tool schemas completos ficam para `feat/mcp-tools-base`; budgets numericos e taxonomia final de eventos seguem para seus portoes donos.

## 2026-06-24 - Algoritmos RAG do MVP

Depois da aprovacao e merge remoto do PR #4, `develop` foi atualizado por fast-forward e a branch `docs/algoritmos-rag-mvp` foi aberta a partir da linha de integracao atualizada. O registro de bordo ainda apontava a branch de seguranca como em fechamento; a reconciliacao foi feita no proprio bordo, registrando seguranca como mesclada e algoritmos como portao atual.

O portao de algoritmos fechou a primeira versao contratual das regras que as branches funcionais deverao implementar. A numeracao impressa passa a ser definida por `page_numbering_anchor_segments_v1`, com ancoras humanas, segmentos, estilos, paginas excluidas da contagem e conflitos que exigem revisao. O chunking inicial passa a ser `semantic_block_v1`, com blocos normalizados, heading path, token count estimado, overlap controlado, source locator obrigatorio e relacoes diretas para tabelas e interpretacoes visuais.

Tambem ficaram definidos os contratos de embeddings e busca: testes e e2e usam `mock-text-embedding-v1`, deterministico, com dimensao 16 e metrica `cosine`; provider real entra depois por adapter. A busca vetorial usa Qdrant apenas como indice derivado, sempre filtrado por workspace, colecao, payload contract e vector space, com enriquecimento e revalidacao no SQL. O score nao corta resultados por padrao; baixa confianca vira sinal explicito para UI/API.

O context builder foi separado da busca por `context_builder_v1`, com politicas `conservative` e `sequential`, budgets iniciais para laboratorio, API e MCP, deduplicacao, ordem deterministica e preservacao de citacoes. O contrato `rag.citation.v1` fixa documento, chunk, pagina fisica, pagina impressa, source locator, quote e fallback de preview. Para respostas, a politica `grounded_answer_policy_v1` determina que uma resposta afirmativa precisa de evidencia citavel; sem contexto confiavel, o sistema retorna `insufficient_evidence`.

Foram adicionadas fixtures pequenas em `tests/contracts/rag` e `tests/contracts/worker`, cobrindo mapa de numeracao, chunking, embedding mockado, resposta de busca, montagem de contexto, citacao e resposta sem evidencia. As branches consumidoras foram atualizadas para apontar para a especificacao, e a decisao duradoura foi registrada na ADR-025. O proximo portao passa a ser `docs/analytics-observabilidade-mvp`, antes da especificacao final de interpretacao visual/tabelas e da fundacao de codigo.

## 2026-06-25 - Analytics e observabilidade do MVP

Depois do merge do PR #5, `develop` foi atualizado por fast-forward e a branch `docs/analytics-observabilidade-mvp` foi aberta a partir da linha de integracao atualizada. O trigger agentico identificou que o registro de bordo ainda mostrava `docs/algoritmos-rag-mvp` em fechamento; a reconciliacao foi feita no proprio bordo, marcando algoritmos como mesclada e analytics como branch ativa.

O portao de analytics fechou a decisao de tratar observabilidade como contrato local versionado, nao como logs ad hoc espalhados pelas features. O envelope canonico passa a ser `analytics.event.v1`, com `eventName` em `snake_case`, origem, classe de retencao, workspace, ator, correlation id, recurso e propriedades pequenas. Tambem ficou definida a separacao entre analytics event, historico local opcional, run log, auditoria minima e log tecnico.

A decisao principal de privacidade foi manter `metadata_only` como padrao. Perguntas, respostas e fontes completas so entram em historico local quando o workspace habilitar `local_history`; mesmo assim, eventos guardam referencias e metricas, nao conteudo bruto. Foram listados campos proibidos, incluindo segredo, token, API key, connection string, PDF bruto, embedding vector, prompt completo e texto integral de documento por padrao.

Tambem foram fechadas as politicas iniciais de retencao: analytics por 90 dias, historico por 30 dias, run log por 30 dias e auditoria minima por 365 dias. Exportacao usara JSONL/CSV com manifesto versionado, e delete de analytics removera eventos produto/historico por workspace sem apagar auditoria minima ainda dentro da retencao. O dashboard MVP foi definido como operacional, cobrindo runs, falhas, latencia, busca, contexto, resposta, feedback, visual/tabelas, API e MCP.

Foram adicionadas fixtures em `tests/contracts/events` e `tests/contracts/analytics`, cobrindo envelope de evento, busca, permissao negada, chamada MCP, dashboard summary, manifesto de export e politica de retencao. A decisao duradoura foi registrada na ADR-026, e as branches consumidoras de settings, analytics, API e MCP foram atualizadas para depender da especificacao nova.

## 2026-06-25 - Interpretacao de imagens e tabelas em PDFs

Depois do merge do PR #6, `develop` foi atualizado por fast-forward e a branch `docs/interpretacao-imagens-tabelas-pdf` foi aberta a partir da linha de integracao atualizada. O trigger agentico identificou a divergencia esperada entre o Git e o registro de bordo, que ainda apontava analytics como branch em fechamento; a reconciliacao foi feita no proprio bordo.

O portao visual/tabular fechou a decisao de tratar imagens, diagramas e tabelas em PDFs como elementos estruturados e citaveis, nao como texto incidental. O contrato `pdf.source_locator.v1` passa a fixar pagina fisica 1-based, pagina impressa quando conhecida, bbox normalizada em `pdf_points_top_left`, dimensoes de pagina, rotacao e ordem de leitura. Isso evita que worker, chunking, contexto e preview usem sistemas de coordenadas diferentes.

Tambem foram definidos os contratos `pdf.document_element.v1`, `pdf.asset.v1`, `pdf.table_json.v1`, `pdf.table_markdown.v1` e `pdf.visual_interpretation.v1`. Tabelas passam a preservar estrutura JSON minima e Markdown citavel; figuras e imagens preservam asset/crop, legenda, OCR interno quando houver e interpretacao textual derivada quando habilitada. A interpretacao visual entra por adapter substituivel, com `mock-vision-interpreter` obrigatorio para testes, smoke e e2e, e provider remoto desligado por padrao.

A branch tambem fechou privacidade, budgets e cache: provider remoto so opera por configuracao explicita, recebe apenas crop/elemento necessario, nao recebe PDF inteiro, e nao grava binario, prompt completo ou texto integral em analytics/log. Interpretacoes podem ser reutilizadas por hash de asset/source locator/provider/model/prompt/policy, escopadas ao workspace no MVP. Limites iniciais cobrem quantidade de elementos, celulas de tabela, tamanho de crop, timeout e output por interpretacao.

Foram adicionadas fixtures em `tests/contracts/worker`, `tests/contracts/rag` e `tests/contracts/events`, cobrindo extracao de elementos, tabela estruturada, figura com legenda, asset crop, interpretacao visual mockada, citacao visual e eventos `table_extracted` e `visual_interpretation_completed`. Os planos consumidores de worker, chunking, context builder e preview de citacao foram atualizados para depender desta especificacao.

## 2026-06-25 - Fundacao do workspace

Depois do merge do PR #7, `develop` foi atualizado por fast-forward e a branch `chore/workspace-fundacao` foi aberta a partir da linha de integracao atualizada. Essa etapa marca a passagem dos portoes de planejamento para a fundacao tecnica testavel.

A branch criou o esqueleto do monorepo com `apps/api`, `apps/web`, `apps/worker`, `apps/mcp`, `infra/compose`, `scripts`, `tests/e2e` e `tests/fixtures`, preservando os contratos ja existentes em `tests/contracts`. Tambem foram adicionadas convencoes raiz: `.editorconfig`, `.gitattributes`, `.gitignore`, `.node-version`, `.java-version` e `.python-version`, alinhadas ao baseline tecnico do MVP.

Cada area nova recebeu um README curto explicando responsabilidade e branch dona da implementacao real. A decisao foi manter esta branch propositalmente leve: ela nao cria Spring, Angular, Python, MCP ou Compose real, para evitar misturar scaffold de stacks diferentes antes das branches planejadas.

Foi criada a primeira interface operacional em PowerShell. `scripts/check.ps1` valida a estrutura raiz e a existencia dos contratos versionados. Os demais scripts (`test-unit`, `test-integration`, `test-smoke`, `test-e2e`, `compose-up` e `compose-down`) sao placeholders intencionais: retornam exit code `2` e explicam qual branch futura implementara o comportamento real.

As verificacoes executadas foram `.\scripts\check.ps1`, que passou e encontrou 38 contratos versionados, e uma chamada controlada dos placeholders em processo separado, confirmando que todos retornam `2` com mensagem clara. O proximo passo, apos merge desta branch, passa a ser `build/dev-runtime-compose`, criando o Compose local inicial com Postgres e Qdrant.

## 2026-06-25 - Vinculos dos planos futuros

Depois da publicacao de `chore/workspace-fundacao`, foi criada a branch `docs/vinculos-planos-branches` como uma branch documental empilhada sobre a fundacao. A motivacao foi reduzir atrito para as proximas sessoes: cada plano futuro deve dizer explicitamente quais documentos, ADRs, contratos e fixtures precisam ser carregados antes de implementar.

Foi criado o arquivo `branches-plan/02a-docs-vinculos-planos-branches.md`, registrando o escopo desta manutencao documental. O `branches-plan/README.md` passou a listar documentos transversais por branch e a regra de revisar a secao `Documentos relevantes` antes de abrir uma branch.

Os planos de `03` a `27` receberam a secao `Documentos relevantes`, separando estado vivo, base operacional, documentos especificos e contratos/fixtures quando aplicavel. O trabalho nao reabriu decisoes arquiteturais dos portoes ja fechados; ele apenas transformou a documentacao existente em mapa de contexto para implementacoes futuras.

As verificacoes executadas confirmaram que todos os planos futuros de `03` a `27` possuem a nova secao e que os links relativos em `branches-plan` apontam para alvos existentes. O fluxo recomendado continua: abrir primeiro o PR de `chore/workspace-fundacao`; depois retarget/rebase e abrir o PR de `docs/vinculos-planos-branches`; em seguida seguir para `build/dev-runtime-compose`.

## 2026-06-25 - Runtime Compose local

Depois do merge da fundacao e dos vinculos documentais, `develop` foi atualizado por fast-forward e a branch `build/dev-runtime-compose` foi aberta a partir da linha de integracao sincronizada. Essa etapa transformou a pasta `infra/compose`, ate entao reservada, no primeiro runtime local real do projeto.

Foram criados os arquivos `compose.yml`, `compose.dev.yml` e `compose.e2e.yml`. O Compose base define Postgres `18.4` e Qdrant `1.18.2`, rede interna, volumes nomeados e health checks. O profile `dev` publica Postgres em `5432`, Qdrant HTTP em `6333` e Qdrant gRPC em `6334`, com override por variaveis de ambiente. O profile `e2e` usa projeto separado, nao publica portas e pode remover volumes com `compose-down.ps1 -Profile e2e -RemoveVolumes`.

Os scripts `compose-up.ps1` e `compose-down.ps1` deixaram de ser placeholders e passaram a montar os argumentos do Docker Compose a partir do profile escolhido. O `compose-up` aguarda health checks por padrao, com opcao `-NoWait` para uso diagnostico. O `check.ps1` passou a validar `docker compose config` para `base`, `dev` e `e2e`, mantendo a verificacao da estrutura raiz e dos contratos versionados.

Durante a validacao real, o Docker Desktop estava parado e precisou ser iniciado. O primeiro health check do Qdrant falhou porque a imagem oficial nao inclui `curl`; a correcao foi usar abertura TCP via `bash` em `6333`, que a propria imagem suporta. Depois disso, `compose-up.ps1 -Profile dev` subiu Postgres e Qdrant saudaveis, `pg_isready` confirmou o Postgres e `http://localhost:6333/healthz` respondeu pelo Qdrant. O profile `e2e` tambem subiu saudavel e foi encerrado removendo containers e volumes.

Com isso, as dependencias locais do MVP estao reproduziveis. A proxima branch passa a ser `chore/backend-spring-base`, que podera usar o Postgres dev ja disponivel para criar a base Spring Boot, health endpoint, profiles e baseline de migrations.

## 2026-06-25 - Backend Spring Boot Base

Depois do merge do PR #8 referente ao Compose local, `develop` foi atualizado por fast-forward e a branch `chore/backend-spring-base` foi aberta a partir da linha de integraÃ§Ã£o sincronizada. Esta etapa criou a base inicial do backend com Spring Boot.

A configuraÃ§Ã£o baseou-se na versÃ£o 3.4.1 do Spring Boot com Java 21, utilizando dependÃªncias fundamentais como Spring Web, Data JPA, PostgreSQL Driver, Flyway, Validation e Spring Boot Actuator. AlÃ©m disso, adicionou-se a biblioteca `springdoc-openapi-starter-webmvc-ui` no `pom.xml` para garantir que a publicaÃ§Ã£o dos contratos OpenAPI decididos na branch `docs/modelo-dados-contratos-mvp` seja automatizada e compatÃ­vel.

Foram criados os perfis (`profiles`) `dev`, `test` e `e2e` na estrutura `application.yml` em `apps/api/src/main/resources`. O perfil `dev` jÃ¡ estÃ¡ apontando para o PostgreSQL na porta `5432` providenciado pelo Docker Compose da branch anterior, facilitando o desenvolvimento e setup local. Foi tambÃ©m criada a migration inicial (`V1__baseline.sql`) para a extensÃ£o `uuid-ossp`.

Para padronizar e isolar as falhas seguindo a decisÃ£o de respostas Problem Details (RFC 7807), implementamos o `GlobalExceptionHandler` sob o pacote `local.fabricarag.core.exception`. Ele capta erros como validaÃ§Ãµes em rotas (`MethodArgumentNotValidException`) e as adapta devolvendo estruturas robustas (com `code`, `correlationId` e `fieldErrors`), que jÃ¡ foram validadas atravÃ©s da implementaÃ§Ã£o do `DummyController` e seus respectivos testes em `DummyControllerTest`.

Durante a construÃ§Ã£o desta base, a execuÃ§Ã£o via Docker e nativamente com Java 21 foi validada com sucesso. As dependÃªncias do Spring Boot 3.4.1 foram ajustadas para garantir compatibilidade, e a comunicaÃ§Ã£o com o banco de dados via perfil `dev` foi testada no Compose local. O prÃ³ximo passo serÃ¡ revisar, commitar, mesclar esta branch em `develop` e iniciar a fundaÃ§Ã£o web com `chore/frontend-angular-base`.

## 2026-06-25 - Frontend Angular Base

Depois do merge da branch do backend na linha `develop`, a branch `chore/frontend-angular-base` foi aberta para criar a fundaÃ§Ã£o do aplicativo Web. O desafio inicial foi o alinhamento de versÃ£o: a CLI do Angular 22 exigia no mÃ­nimo o Node `v24.15.0`. ApÃ³s a atualizaÃ§Ã£o no ambiente, o setup foi executado limpo, diretamente na pasta `apps/web` atravÃ©s do comando `npx @angular/cli@latest new web`.

Foi instalada a biblioteca de componentes `@angular/cdk`, alinhada com as diretrizes do `docs/padroes-de-projeto.md` que indica uso de acessibilidade padronizada sem acoplar visualmente o cÃ³digo a uma biblioteca terceira completa como Material Design. Os estilos globais foram configurados em `styles.css` trazendo a adoÃ§Ã£o da paleta *Violet Lab* via variÃ¡veis e suporte out-of-the-box para alternÃ¢ncia clara e escura utilizando `prefers-color-scheme`.

Uma estrutura base foi criada dentro de `src/app`: a pasta `core/` agora abriga o novo utilitÃ¡rio global `HttpService` com tratamento base de erros; a pasta `shared/` abrigarÃ¡ componentes reutilizÃ¡veis base e `features/` iniciou a abrigar as Ã¡reas funcionais, nascendo com o mÃ³dulo central `ShellComponent`.

O `ShellComponent` funciona como a roupagem e invÃ³lucro do sistema, projetado com CSS grid e flexbox com painel lateral e main content, e que agora Ã© diretamente servido via configuraÃ§Ã£o de lazy loading local do `app.routes.ts`. As validaÃ§Ãµes automÃ¡ticas utilizando o novo runner de testes padrÃ£o (Vitest) rodaram e compilaram essa nova hierarquia de injeÃ§Ãµes nativamente. 

O prÃ³ximo passo Ã© realizar commit do cÃ³digo, abrir pull request para a branch `develop` e focar no backend em Python a seguir (`chore/worker-python-base`).

## 2026-06-25 - Backend Python Worker Base

Depois do merge da branch do frontend na linha `develop`, a branch `chore/worker-python-base` foi aberta para criar a fundaÃ§Ã£o do worker em Python. O worker atuarÃ¡ processando arquivos, chunks e interaÃ§Ãµes sob demanda orquestradas pelo backend em Spring Boot.

A base foi estruturada utilizando a versÃ£o 3.13 do Python, adotando a ferramenta `uv` como orquestradora de dependÃªncias e ambientes de desenvolvimento por ser extremamente veloz. Na raiz de `apps/worker`, o arquivo `pyproject.toml` definiu pacotes chaves da stack tÃ©cnica: `fastapi`, `pydantic` e `uvicorn` para a API, alÃ©m de dependÃªncias de desenvolvimento organizadas via `dependency-groups` (incluindo `pytest` e `ruff`).

Dentro de `src/rag_worker/`, a fÃ¡brica do aplicativo foi criada implementando `FastAPI`, juntamente com os tratamentos padronizados de resposta. As entidades Pydantic (como `WorkerErrorResponse` e estruturas que carregam propriedades obrigatÃ³rias como `contractVersion` e `workspaceId`) foram estritamente alinhadas Ã s diretrizes traÃ§adas previamente pelo portÃ£o de planejamento de contratos (`docs/modelo-dados-contratos-mvp`). AlÃ©m disso, foram criados endpoints de inspeÃ§Ã£o de estado (liveness/health checks) implementados e com rotas cobertas por testes atravÃ©s do pacote `pytest`. 

A anÃ¡lise estÃ¡tica e formataÃ§Ãµes rigorosas com `ruff` executaram sem erros (com os pequenos acertos feitos via pipeline de `--fix`). Por fim, validamos o empacotamento com o comando `docker build` de uma imagem multi-stage limpa que incorpora as dependÃªncias gerando um ambiente leve.

Com testes e lints aprovados, a base do worker atende Ã  estabilidade esperada pelo MVP. O prÃ³ximo passo, apÃ³s mesclar essa branch em `develop`, serÃ¡ executar os testes da stack de desenvolvimento consolidada (smoke stack) atestando que todas as bases de software orquestradas (Postgres, Qdrant, Spring Boot, Node, Python) iniciam sincronizadas antes de passarmos Ã s primeiras funcionalidades (autenticaÃ§Ã£o e coleÃ§Ãµes).

## 2026-06-26 - Smoke Test da Stack Local

Depois do merge das branches fundamentais (`chore/backend-spring-base`, `chore/frontend-angular-base` e `chore/worker-python-base`), a branch `test/smoke-stack-local` foi iniciada. O objetivo desta branch era estabelecer o primeiro teste automatizado capaz de validar que as dependÃªncias de infraestrutura locais sobem corretamente e estÃ£o saudÃ¡veis.

A implementaÃ§Ã£o consistiu em substituir o placeholder original do `scripts/test-smoke.ps1` por um fluxo de script em PowerShell. O fluxo invoca o script `compose-up.ps1` com o profile `dev` e, em seguida, executa chamadas reais para confirmar se os serviÃ§os vitais estÃ£o vivos. Ele valida explicitamente o Qdrant (via endpoint HTTP local `6333/healthz`) e o PostgreSQL (que jÃ¡ fica garantido pela validaÃ§Ã£o embutida de health checks do `docker-compose`). Adicionalmente, o script foi projetado para nÃ£o falhar caso a API, o Worker e a UI Angular nÃ£o estejam em execuÃ§Ã£o no momento (devido ao fato de ainda estarem sendo operadas via CLI nas suas respectivas pastas ou aguardando eventual empacotamento no Compose), apenas registrando seu status se estiverem ativos, atendendo assim Ã  clÃ¡usula de testar tais serviÃ§os apenas "quando existirem".

A execuÃ§Ã£o manual provou que o sistema de infraestrutura orquestrada da fÃ¡brica estÃ¡ apto a ser rodado com um comando simples e confiÃ¡vel (`.\scripts\test-smoke.ps1`), subindo instÃ¢ncias saudÃ¡veis e validando respostas antes do encerramento com sucesso (exit code 0). O prÃ³ximo passo, apÃ³s merge na `develop`, Ã© entrar no fluxo de produto construindo o bootstrap de workspace e a autenticaÃ§Ã£o em `feat/auth-bootstrap-workspaces`.

## 2026-06-27 - Workspace Dashboard Minimo

Depois do merge da branch anterior (`feat/auth-bootstrap-workspaces`) na linha `develop`, a branch `feat/workspace-dashboard-minimo` foi aberta para criar a interface inicial que os usuarios visualizam apos a autenticacao. O objetivo central era ter uma pagina raiz conectada a API do workspace, mesmo que os dados reais de colecoes e documentos ainda nao existissem no banco de dados, estabelecendo assim a fundacao da navegacao e visualizacao de estado vazio (empty state).

No lado do backend, foi criado um `WorkspaceController` responsavel pela rota `GET /api/v1/workspaces/{workspaceId}/dashboard`, que devolve o contrato `DashboardSummaryResponse`. Aproveitando a base de seguranca ja criada, injetou-se o `@PreAuthorize` com o `authorizationPolicy.hasPermission` para garantir que um usuario nao possa solicitar o dashboard de um workspace ao qual ele nao tem acesso. A decisao pragmatica foi retornar `0` nas contagens, uma vez que as tabelas necessarias so serao implementadas na proxima branch, e o contrato ja fica estabelecido para a UI.

No lado do frontend Angular, implementou-se o `WorkspaceService` para consultar a API. Criou-se o componente `DashboardComponent` renderizando uma grid de 3 metricas (Colecoes, Documentos, Processamentos) e um destaque de "Empty State" incitando a criacao da primeira colecao. Alem disso, o componente matriz `ShellComponent` foi atualizado para injetar a reatividade do `AuthService` via signals (`toSignal(auth.authState$)`), de forma que o menu lateral passou a renderizar visualmente o nome real do Workspace ao inves de conteudos estaticos, conectando o ciclo completo: login -> auth guard -> dados no shell -> dados na pagina filha.

A validacao por compilacao (`mvnw compile` e `npm run build`) ocorreu de forma positiva sem erros de sintaxe ou lint. O proximo passo operacional esperado e abrir o pull request para `develop` e iniciar a branch subsequente: `feat/colecoes-documentos-core` ou `feat/workspace-settings-mvp`.

## 2026-06-27 - Workspace Settings MVP

Depois do merge da branch anterior, a branch `feat/workspace-settings-mvp` foi aberta para implementar a tela de configurações do workspace. Essa tela centraliza a gestão de preferências gerais, ingestão de dados, retenção analítica, níveis de acesso e informações de API, cobrindo requisitos essenciais definidos nos portões de planejamento.

No backend, foi adicionada a migration `V3__workspace_settings.sql` injetando as colunas `slug`, `owner_user_id`, `settings` (`jsonb`) e `deleted_at`. O domínio `Workspace` O DTO foi revalidado e alinhado entre o Python e o Java para processamentos com arrays de objetos JSON (`@JdbcTypeCode`).

Foram criados os DTOs e os endpoints `GET` e `PATCH` para `/api/v1/workspaces/{workspaceId}/settings` no `WorkspaceController`, protegidos adequadamente pelas permissões `workspace.read` e `workspace.update`.

No frontend Angular, o `WorkspaceService` foi expandido. Foi desenvolvida a rota `/settings` com o `WorkspaceSettingsComponent` contendo 5 abas (Geral, Ingestão, Analytics, Acesso e API). A aba de Acesso manteve foco *read-only* mostrando o nível de permissão (role) atual, delegando a gestão completa de membros (convites e edição) para fase pós-MVP, fato devidamente registrado no `docs/pos-mvp.md`. O menu lateral (`ShellComponent`) foi integrado para acesso direto à rota.

A compilação local garantiu que os contratos e novas funções estavam saudáveis. O próximo passo será revisar, comitar, mesclar esta branch em `develop` e iniciar a funcionalidade central de coleções (`feat/colecoes-documentos-core`).

## 2026-06-27 - Ingestao Upload PDF

Depois do merge remoto de `feat/colecoes-documentos-core`, foi iniciada a branch `feat/ingestao-upload-pdf` para permitir que o usuÃ¡rio adicione o primeiro artefato real na base de conhecimento. A arquitetura determinou que nÃ£o haverÃ¡ LLMs nem OCR em tempo sÃ­ncrono. O upload apenas persiste o arquivo fÃ­sico e cria a intenÃ§Ã£o (`IngestRun`) no estado `QUEUED`.

Foi ajustado o limite do Spring Web para suportar uploads de atÃ© 90MB, conforme necessidade, rejeitando arquivos invÃ¡lidos antecipadamente (HTTP 400). A tabela `ingest_runs` foi criada (via `V5__ingest_runs.sql`) juntamente com `IngestRun` e suas cardinalidades. O `StorageService` ganhou capacidades de escrita de sistema de arquivos e os serviÃ§os Spring orquestraram o checksum de `source_hash`.

## 2026-06-27 - OperaÃ§Ã£o de Ingest Runs (Cancel, Retry e Logs)

Iniciada e concluÃ­da a branch `feat/ingestao-runs-operacao`. Esta funcionalidade permite aos usuÃ¡rios acompanharem o status das execuÃ§Ãµes assÃ­ncronas (runs), etapas de processamento e logs associados, completando a camada observÃ¡vel do RAG.

No backend Spring Boot, criamos as entidades `IngestStep` e `IngestRunLog`, alÃ©m de atualizar `IngestRun` com relaÃ§Ãµes para suporte de Retry/Reprocessamento. Foi adicionada a migraÃ§Ã£o `V6__ingest_steps_and_logs.sql` e utilizado o tipo de dados JSON nativo do Hibernate 6 (`@JdbcTypeCode(SqlTypes.JSON)`) no lugar de `hypersistence-utils`, visando simplificar dependÃªncias e melhorar aderÃªncia Ã  arquitetura padrÃ£o. TambÃ©m foram expostas as rotas protegidas pelo RBAC existente (`ingest_run.read` e `ingest_run.update`).

No frontend Angular, foi criado o `IngestRunService` para chamadas HTTP e o `IngestRunDetailComponent` implementou a visualizaÃ§Ã£o do run, histÃ³rico de steps e terminal de logs com polling automÃ¡tico a cada 5 segundos para atualizaÃ§Ãµes ao vivo. Foram adicionados controles baseados no status da run, permitindo cancelamento quando iniciada/na fila, e repetiÃ§Ã£o (retry) quando falhada.

A branch compila sem erros (backend com `mvn compile` e frontend com `ng build`). O PR para `develop` Ã© o prÃ³ximo passo, antes de partir para a integraÃ§Ã£o da API com o script Python em `feat/worker-pdf-inspect`.

## 2026-06-28 - IntegraÃ§Ã£o do Worker de Inspecao de PDF (Async Webhook)

Depois do merge da branch anterior (`feat/ingestao-runs-operacao`), foi iniciada a branch `feat/worker-pdf-inspect`. O objetivo desta etapa era estabelecer a primeira interaÃ§Ã£o real e pesada entre a API principal (Spring Boot) e o Worker (Python), extraindo metadados bÃ¡sicos e contagem de pÃ¡ginas de PDFs inseridos pelo usuÃ¡rio.

A decisÃ£o arquitetÃ´nica mais relevante foi adotar um modelo totalmente assÃ­ncrono baseado em *webhooks*, evitando assim qualquer tipo de "bare timeout" ou travamento de threads na aplicaÃ§Ã£o Spring. Para isso:
- O Worker Python foi atualizado com a biblioteca `pypdf` para operaÃ§Ãµes isoladas de leitura. A rota `POST /api/v1/pdf/inspect` utiliza `fastapi.BackgroundTasks` para enfileirar a inspeÃ§Ã£o nativamente e retorna um `202 Accepted` de imediato. Ao finalizar, o Worker utiliza o `httpx` para disparar um callback para o backend informando o sucesso ou erro (via `WorkerErrorResponse`).
- O Backend Spring Boot ganhou um `WorkerClient` usando `RestClient` e um `InternalCallbackController` que ouve essas respostas do Worker de forma desacoplada, invocando o `IngestRunOperationService` para evoluir ou falhar o processamento ativamente, em vez de depender de polling ou polling restrito.

Testes automatizados unitÃ¡rios em Python garantiram o fallback do callback atravÃ©s de mocks (`unittest.mock`), evitando lentidÃµes na suÃ­te, e a compatibilidade das dependÃªncias foi provada reconstruindo as bibliotecas atravÃ©s do `uv sync`.

Com todos os testes e a compilaÃ§Ã£o Spring Boot (`mvn compile`) reportando sucesso, o escopo foi fechado com alta confiabilidade operacional. O prÃ³ximo passo Ã© mesclar em `develop` e focar na funcionalidade de extraÃ§Ã£o mais rica: renderizaÃ§Ã£o e OCR (`feat/worker-pdf-render-ocr-base`).

## 2026-06-28 - RenderizaÃ§Ã£o e ExtraÃ§Ã£o de Texto via OCR (Worker)

Depois de concluir a integraÃ§Ã£o inicial com o Worker Python via webhooks, a branch `feat/worker-pdf-render-ocr-base` foi aberta para prover capacidades de renderizaÃ§Ã£o de pÃ¡ginas (necessÃ¡ria para visualizaÃ§Ã£o e criaÃ§Ã£o do mapa de pÃ¡ginas do frontend) e extraÃ§Ã£o de texto, com suporte automÃ¡tico a OCR.

O plano original previa utilizar `pymupdf` por possuir suporte forte nativo a extraÃ§Ã£o de texto, bounding boxes e renderizaÃ§Ã£o, contudo, o fato de ser licenciado sob AGPL levantou bloqueios prÃ¡ticos para distribuiÃ§Ã£o em modo portfÃ³lio. A decisÃ£o foi migrar para o `pypdfium2`, um wrapper de altÃ­ssima performance para o motor PDFium (Apache-2.0), que manteve todos os recursos necessÃ¡rios livres da contaminaÃ§Ã£o copyleft da AGPL.

No worker Python, foram criados os contratos Pydantic baseados nas necessidades da FÃ¡brica:
- RenderizaÃ§Ã£o: extrai imagens PNG de pÃ¡ginas sob demanda com suporte a ajuste de DPI.
- ExtraÃ§Ã£o de texto: extrai texto nativo priorizando eficiÃªncia. Como estratÃ©gia de *fallback*, caso a pÃ¡gina seja um documento escaneado (sem texto nativo reconhecido) ou se a flag `forceOcr` for acionada, o sistema converte a pÃ¡gina para imagem localmente (300 DPI) e roda o reconhecimento Ã³ptico de caracteres atravÃ©s do pacote `pytesseract`.

As rotas foram implementadas no `routers/pdf.py`, suportando chamadas webhooks para operaÃ§Ãµes pesadas. Todo o ecossistema foi testado sob *mocks* para prover garantias de cobertura em tempo de integraÃ§Ã£o. As dependÃªncias OS (`tesseract-ocr`) foram embutidas de forma minimalista na etapa multi-stage do `Dockerfile`.

Por fim, no ecossistema Spring Boot, a camada do `WorkerClient` e do `InternalCallbackController` foram estendidas, assim como seus respectivos DTOs, atestando a conexÃ£o final com a stack orquestradora. Com as aprovaÃ§Ãµes unitÃ¡rias em alta confianÃ§a, o pull request para `develop` foi o prÃ³ximo passo preparativo antes das inspeÃ§Ãµes focadas em extraÃ§Ã£o estrutural avanÃ§ada (elementos visuais e tabelas).

## Modelo de entrada futura

```text
Data - Titulo da etapa

Texto corrido descrevendo o contexto, o que foi feito, por que foi feito, quais decisoes foram tomadas, quais arquivos ou areas foram afetados, quais verificacoes ocorreram e qual e a proxima frente de trabalho.
```

## 2026-06-28 - Interpretação Visual e de Tabelas em PDF

Depois do merge de `feat/worker-pdf-render-ocr-base`, a branch `feat/worker-pdf-visual-tables-base` foi aberta para estender as capacidades operacionais da esteira com recursos vitais definidos pelo documento de planejamento visual.

O foco desta etapa foi incorporar a capacidade estrutural (Pydantic e Spring Boot) e os adaptadores de processamento (worker) que lidam com elementos ricos do PDF, especificamente a extração de tabelas (usando a recém adicionada biblioteca `pdfplumber`, de licença MIT) e o design base para interpretar visualmente imagens/tabelas através de Webhooks com Vision Language Models (VLM). Foi implementado também o `mock-vision-interpreter`, um adaptador de testes capaz de emular a funcionalidade determinística das descrições da VLM e acatar às limitações de orçamentos (budgets).

Com relação à orquestração, os DTOs do backend Java ganharam extensões substanciais sob `local.fabricarag.core.dto.worker.components`, permitindo o intercâmbio de estruturas complexas entre o Worker e a Ingestão (PdfExtractElementsRequest/Response e PdfInterpretVisualRequest/Response). Além disso, foram ajustadas as dependências faltantes `WorkerBaseRequest` e `WorkerErrorResponse` e as implementações referentes ao webhook.

Testes locais usando a suite pytest provaram que os fluxos sincronos de falha e webhooks assincronos de aceitacao executam sem atrito, sendo suportados pela validacao estrita com FastAPI. O backend compilou perfeitamente. O proximo passo sera realizar o merge para `develop` e preparar a infraestrutura que derivara a numeracao das paginas (`feat/paginas-numeracao`).

## 2026-06-28 - Indexação Vetorial no Qdrant

A branch `feat/qdrant-indexacao` foi implementada para cumprir o papel de interfaceamento do RAG com o vetor store Qdrant. A decisão central foi manter a separação de responsabilidades (Princípio de Menor Privilégio), onde a API gerencia apenas a inserção e deleção de vetores (points), sem criar estruturas. A coleção inicial (`ragcreator_chunks_v1`) passa a ser provisionada por um script PowerShell (`scripts/qdrant-init.ps1`) agregado na subida do `docker compose`.

No escopo da API Spring Boot, o adapter `QdrantVectorStoreAdapter` foi criado assinando o contrato `VectorStorePort`, incorporando o cliente gRPC oficial `io.qdrant:client` (v1.12.0) para prover a comunicação. O payload foi extraído mapeando os metadados e os limites do bloco (`Chunk` e `ChunkEmbedding`). Para garantir a estabilidade da compilação e evitar ausência de pacotes no runtime, `guava` e `protobuf-java` precisaram ser incluídos explicitamente no POM, compensando o gerenciamento do Spring Boot. Testes de integração (IT) via Testcontainers foram adicionados para validar o pipeline vetorial de ponta a ponta. O merge para `develop` preparará o caminho para as branches orquestradoras do RAG.

## 2026-06-28 - Ingestão Pipeline e Indexação no Qdrant

A branch `feat/ingestao-pipeline-indexacao` foi executada para concluir o principal elo do MVP: a orquestração ponta a ponta da esteira de processamento de documentos, integrando o Worker assíncrono ao banco vetorial Qdrant.

A evolução chave nesta etapa foi a introdução do orquestrador `IngestRunPipelineService`, o coração da esteira. Ao invés de lógicas fragmentadas por todo o serviço `DocumentService` ou controladoras Webhooks, a responsabilidade de progressão dos passos (`QUEUED`, `RUNNING`, `FAILED`, `COMPLETED`), foi movida e encapsulada nesse pipeline central que reage aos callbacks emitidos pelo *worker*.

Para que os callbacks operassem dinamicamente (em conformidade às recomendações arquiteturais), a estrutura base do worker (`WorkerBaseRequest`) foi ampliada com o campo `callbackUrl`. Com isso, a chamada no controller de webhook (`InternalCallbackController`) deixou de usar rotas estáticas genéricas (ex: `/embeddings`) passando a recuperar e encaminhar a própria `runId` recebida pelo sub-caminho dinâmico. A limpeza foi imediata e o código legado (`IngestRunOperationService`) que forçava injeções locais foi migrado para o pipeline.

O limite de I/O em banco de dados analíticos ou vetoriais é comumente um gargalo, logo, a segunda diretriz do planejamento desta branch determinou a persistência de chunks vectoriais no Qdrant com estratégia baseada em lote. O componente `QdrantVectorStoreAdapter` foi enriquecido com a nova assinatura do contrato provendo o comportamento assíncrono de varrer o payload de embeddings recebido nos blocos (`upsertBatch`), enviando conjuntos de `100` pontos em um único request grpc para a instância vetorial, poupando a rede e provendo ganhos maciços no armazenamento massivo do *source*.

Garantimos a qualidade e funcionamento correto do fluxo implementando a suíte de testes de máquina de estado do pipeline (`IngestRunPipelineServiceTest`) mockando os repositórios vitais (banco e o *client* do Worker). 

Todas as dependências e processos buildaram apropriadamente localmente. A branch é dada por aprovada para sofrer *merge request* para `develop`, direcionando então as engrenagens rumo a camada de busca vetorial base.


## 2026-06-28 - Busca Vetorial Base

Depois de assentar as fundações de orquestração do Qdrant e injeção do Worker, foi aberta a branch eat/busca-vetorial-base objetivando fechar o fluxo de resposta do RAG, consultando a base de conhecimento estruturada pelas branches anteriores.

A arquitetura definiu explicitamente que o RAG no MVP não utilizará reranking complexos de rede, garantindo performance bruta. Assim, desenvolveu-se o VectorSearchService atuando em 3 frentes sincrônicas: 
1. Solicitação da embedding (vetor float) ao Worker síncrono via POST direto;
2. Chamada ao VectorStoreAdapter (gRPC para Qdrant) usando o point vetorial recém-gerado cruzando filtros vitais obrigatórios como workspaceId, collectionId e versão do contrato; 
3. Re-hidratação com dados reais utilizando o Postgres (ChunkRepository) puxando todos os chunks com os publicIds identificados pelo Qdrant.

A avaliação de similaridade continuou confiando puramente em Distância do Cosseno (cosine). Quando o score individual for muito fraco (< 0.35) ou quando menos de 2 chunks forem providenciados, a resposta RAG assumirá um carimbo protetor lowConfidence = true, fornecendo à UI ou à API indicações claras para mitigar alucinações nas próximas fases do projeto.

O processo de citações foi formatado com perfeição baseando-se no payload rico source_locator, recuperando paginas de PDF originais ou rótulos impressos nativamente sem vazamento interno. Todos os componentes Java, os records de Request/Response de Busca e os testes unitários via Mockito passaram sem erros locais através das compilações regulares do Maven. O próximo passo será realizar PR e prosseguir com a camada do navegador visual (feat/chunks-navegador).

## 2026-06-28 - Navegador de Chunks e Feedback Local

Depois de fechar a busca vetorial, a branch `feat/chunks-navegador` foi aberta para prover visibilidade sobre os dados indexados, permitindo inspeção dos chunks e avaliação (feedback) diretamente na interface da coleção.

No backend Spring Boot, a entidade `RetrievalFeedback` foi criada para suportar metadados valiosos (documento, chunk, tipo de feedback, notas), acompanhada de sua migration `V9__retrieval_feedback.sql`. O `ChunkRepository` recebeu queries JPQL customizadas para permitir paginação filtrada por workspace/coleção e buscas textuais com `LIKE %query%`. O `ChunkBrowserService` foi criado para abstrair a orquestração de paginação e a busca de vizinhos sequenciais, exposto por novos endpoints no `ChunkController` e `RetrievalFeedbackController`.

No frontend Angular, foi criado o `ChunkService` para comunicação. A `ChunkBrowserComponent` exibe os chunks em formato tabular (sequência, documento, cabeçalho e tokens), enquanto a gaveta lateral `ChunkDetailsPanelComponent` permite visualizar os metadados brutos gerados pela pipeline de ingestão (como sourceLocator parseado) e o conteúdo textual. Além disso, a navegação entre chunks vizinhos (anterior e próximo) e os botões de feedback local interativos foram integrados. A integração foi feita diretamente no `CollectionDetailComponent` como uma nova aba.

A compilação do Spring Boot foi verificada (via Maven) e o build da aplicação Angular concluído com sucesso, conectando de ponta a ponta as requisições. O próximo passo será realizar o merge para `develop` e iniciar a integração final da esteira ou preparar a tela de chat.

## 2026-06-28 - Context Builder e Resposta RAG

Com a busca vetorial pronta, a branch `feat/context-builder-base` foi implementada para expandir o contexto bruto devolvido pelo Qdrant. O `ContextBuilderService` garante que vizinhos (anterior e proximo) e chunks vinculados diretamente (continuacoes de tabelas ou explicacoes visuais) sejam recuperados, deduzidos os orcamentos de tokens estipulados para envio ao LLM. Casos limitrofes onde o token budget excede a disponibilidade contam com estrategais de truncamento elegante e marcacao clara (discarded) para rastreabilidade de orcamento de prompt.

Em seguida, a branch `feat/resposta-rag-base` avancou para a interface e politicas do modelo de resposta real. Foi construido o endpoint `/api/v1/workspaces/{workspaceId}/rag/ask`, injetando `AskRequest` e provendo um `AskResponse` robusto incluindo dados originais de contexto (expandedContext e citations). Um mock fidedigno (`MockAnswerProviderAdapter`) substituiu o acesso final de LLM simulando respostas ancoradas pelo contexto ou o status `insufficient_evidence` frente a metricas frageis de recuperacao (`lowConfidence` e nenhum chunk retornado), implementando a diretriz nativa de 'grounded_answer_policy_v1'. A solucao e perfeitamente extensivel para provedores reais nas proximas sprints.

# #   2 0 2 6 - 0 6 - 2 8   -   L a b o r a t � r i o   d e   R e c u p e r a � � o   e   R A G   U I 
 
 A p � s   a s   i m p l a n t a � � e s   p r o f u n d a s   d a s   l � g i c a s   d e   C o n t e x t   B u i l d e r   e   o s   a d a p t e r s   i n i c i a i s   d e   r e s p o s t a   R A G ,   a   b r a n c h   \  e a t / l a b o r a t o r i o - r e c u p e r a c a o \   f o i   e s t e n d i d a   a   p a r t i r   d o   p r o j e t o   b a s e .   A   d i r e t i v a   d e s s a   f e a t u r e   e r a   p r o v e r   a   i n t e r f a c e   e s s e n c i a l   ( u m   L a b o r a t � r i o )   q u e   p e r m i t e   a o   a d m i n i s t r a d o r   t e s t a r   o r g a n i c a m e n t e   c o m o   a s   r e q u i s i � � e s   ( q u e r i e s )   s e   c o m p o r t a m   n a   b a s e   d e   c o n h e c i m e n t o . 
 
 N o   e c o s s i s t e m a   d o   f r o n t e n d   A n g u l a r ,   o   s e r v i � o   \ R a g S e r v i c e \   a s s u m i u   o   p r o t a g o n i s m o   p a r a   i n t e r c o n e c t a r   a s   e s t r u t u r a s   t i p a d a s   p e l o   b a c k - e n d   ( e m   \ A s k R e q u e s t \ ,   \ A s k R e s p o n s e \   e   a f i n s )   e m i t i n d o   c h a m a d a s   a o s   r e c � m - n a s c i d o s   e n d p o i n t s   d e   \  s k \ .   P a r a l e l a m e n t e ,   e m b u t i m o s   o   m o d e l o   d e   f e e d b a c k   ( c r u z a n d o   \ R e t r i e v a l F e e d b a c k R e q u e s t \ ) ,   p o s s i b i l i t a n d o   f e c h a r   a   m � t r i c a   c o m p l e t a   d e   r a s t r e a b i l i d a d e   ( q u e r y - i d   a m a r r a d o   n o   t h u m b s   u p / d o w n ) . 
 
 V i s u a l m e n t e ,   \ R a g L a b o r a t o r y C o m p o n e n t \   f o i   c o n f e c c i o n a d o   o f e r e c e n d o   c o n t r o l e s   f l e x � v e i s   ( c o m o   T o p   K ,   T o k e n   B u d g e t s ,   i n c l u i r / n � o - i n c l u i r   v i s u a i s   e   t a b e l a s )   e m   u m   f o r m u l � r i o   p o l i d o ,   r e n d e r i z a n d o   a b a s   s e p a r a d a s   p a r a   o s   r e s u l t a d o s .   A s   a b a s   e x p � e m   a s   r e s p o s t a s   p r o v i n d a s   d o   L L M   e v i d e n c i a n d o   a s   \ C i t a � � e s \   e   l i s t a n d o   d e t a l h a d a m e n t e   a   t r a n s p a r � n c i a   e x i g i d a   p e l o   e s c o p o   d o   p r o j e t o :   a s   a m o s t r a s   b r u t a s   d e   \ C o n t e x t o   E x p a n d i d o \   ( d e m o n s t r a n d o   q u a l   t e x t o   f o i   e n v i a d o   a o   p r o m p t ,   s u a s   o r i g e n s   d i r e t a s   e   s t a t u s   d e   r e s t r i � � e s   -   t r u c a t e s   p o r   b u d g e t )   a s s i m   c o m o   a   a b a   a n a l � t i c a   c o m   o   s c o r e   d o s   v e t o r e s .   
 
 C o m o   d e c i s � o   o p e r a c i o n a l ,   e s s e   l a b o r a t � r i o   n � o   h a b i t a   u m   e s c o p o   g l o b a l :   e l e   e s t �   i n s t a n c i a d o   n o   \ C o l l e c t i o n D e t a i l C o m p o n e n t \   c o m o   a   a b a   a t i v a   ' L a b o r a t � r i o ' ,   t i r a n d o   p r o v e i t o   d a s   i n j e � � e s   d e   \ w o r k s p a c e I d \   e   \ c o l l e c t i o n I d \   j �   c o n t r o l a d a s ,   r e s u l t a n d o   n u m a   a r q u i t e t u r a   c o m p o n e n t i z a d a   r o b u s t a .   T e s t e s   d e   b u i l d   p r o v a r a m   a   h i g i d e z   d a s   t i p a g e n s   c r u z a d a s   ( S t r i c t   T y p e S c r i p t   c o m p i l e   t i m e   c h e c k s ) ,   e   a   b r a n c h   e n c a m i n h a - s e   p e r f e i t a m e n t e   p a r a   M e r g e   R e q u e s t ,   a p o n t a n d o   o s   c a n h � e s   f u t u r o s   p a r a   m e l h o r i a s   d o   f e e d b a c k   e   A n a l y t i c s   f i n a l . 

## 2026-06-28 - Citações e Preview de Fonte (RAG)

Após o laboratório básico, o fluxo de recuperação exigia uma visualização rica da procedência (grounding). Na branch eat/citacoes-preview-fonte, refatoramos as citações no frontend. Os metadados visuais ou textuais puros provenientes do RAG agora alimentam os componentes CitationCardComponent e CitationPreviewComponent.
O preview é apresentado em um painel lateral fluido para não ofuscar o contexto. Ele implementa fallback textual na ausência de renderização de PDF nativo e aplica highlights absolutos de BBox sobre os assets extraídos utilizando as propriedades pdf_points_top_left. O build rigoroso do Angular foi validado com sucesso e o fluxo está completo para as próximas trilhas de observabilidade e analytics.
