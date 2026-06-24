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

## Modelo de entrada futura

```text
Data - Titulo da etapa

Texto corrido descrevendo o contexto, o que foi feito, por que foi feito, quais decisoes foram tomadas, quais arquivos ou areas foram afetados, quais verificacoes ocorreram e qual e a proxima frente de trabalho.
```
