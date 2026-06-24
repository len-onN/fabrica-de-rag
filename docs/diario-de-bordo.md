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

Na revisao de cobertura do MVP, o plano de branches foi comparado contra o criterio funcional de fechamento do MVP. A cobertura principal estava presente, mas havia lacunas que poderiam virar improviso durante a implementacao: configuracoes do workspace, operacao completa de runs de ingestao, render/OCR basico, orquestracao de ingestao ate indexacao, navegador de chunks e abertura da fonte original de uma citacao. Essas lacunas foram transformadas em branches proprias. Tambem ficou decidido que decisoes estruturais complexas devem ser fechadas em branches `docs/*` antes do codigo: modelo de dados/contratos, arquitetura de ingestao, seguranca/permissoes, algoritmos RAG e analytics/observabilidade. Com isso, o proximo passo deixa de ser diretamente `chore/workspace-fundacao` e passa a ser fechar esses portoes de planejamento, comecando por `docs/modelo-dados-contratos-mvp`.

## Modelo de entrada futura

```text
Data - Titulo da etapa

Texto corrido descrevendo o contexto, o que foi feito, por que foi feito, quais decisoes foram tomadas, quais arquivos ou areas foram afetados, quais verificacoes ocorreram e qual e a proxima frente de trabalho.
```
