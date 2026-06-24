# Estrategia Operacional de Desenvolvimento

## Visao

A camada operacional define como o planejamento vira implementacao sem perder contexto, qualidade ou rastreabilidade.

O fluxo principal do projeto e:

```text
develop
-> branch de implementacao
-> pull request para develop
-> merge apos fechamento do escopo
```

Cada branch deve ter um escopo claro, nome alinhado a Conventional Commits, criterios de aceite, verificacoes tecnicas e registro atualizado no fim da etapa. Quando uma etapa ficar incompleta, o registro deve ser atualizado antes de uma pausa longa ou antes de o contexto operacional se perder.

## Objetivos

- Manter `develop` como linha principal de integracao.
- Trabalhar sempre em branches pequenas o bastante para revisar.
- Transformar decisoes tecnicas em documentos antes de codificar areas sensiveis.
- Registrar impactos colaterais de cada mudanca.
- Preservar historico de decisoes, progresso e pendencias.
- Facilitar retomada do trabalho por humanos ou agentes.
- Evitar que o MVP avance com lacunas invisiveis de seguranca, dados, performance ou contratos.
- Manter o codigo aberto a extensao e fechado a mudancas laterais sem justificativa.
- Garantir substituibilidade de implementacoes e inversao de controle nas dependencias de infraestrutura.

## Nome de branches

Branches de implementacao devem seguir uma adaptacao de Conventional Commits:

```text
tipo/escopo-descricao-curta
```

Exemplos:

```text
docs/estrategia-operacional
docs/plano-tecnico-mvp
chore/workspace-fundacao
build/dev-runtime-compose
feat/auth-bootstrap-workspaces
feat/ingestao-upload-pdf
feat/paginas-numeracao
feat/chunking-semantico
feat/qdrant-indexacao
feat/laboratorio-recuperacao
feat/analytics-eventos-base
feat/mcp-tools-base
fix/mapa-paginas
perf/context-builder
test/worker-pdf
```

Regras:

- `tipo` deve usar a mesma familia de Conventional Commits: `feat`, `fix`, `docs`, `chore`, `build`, `ci`, `test`, `refactor` ou `perf`.
- `escopo-descricao-curta` deve ser em kebab-case.
- A branch deve representar uma frente de trabalho revisavel.
- Uma branch pode conter documentacao associada a implementacao, desde que essa documentacao faca parte do mesmo escopo.
- Mudancas sem relacao devem ir para outra branch.

## Ciclo de uma branch

### 1. Abertura

Antes de implementar, registrar:

- nome da branch;
- objetivo;
- motivacao;
- escopo incluido;
- fora de escopo;
- documentos de referencia;
- skills/fontes oficiais ativadas;
- decisoes pendentes;
- riscos e colateralidades esperadas;
- criterios de aceite.

### 2. Planejamento tecnico local

Antes de codificar areas relevantes, detalhar:

- contratos de API ou eventos;
- modelo de dados e migracoes;
- contratos entre Angular, Spring Boot, Python worker, Postgres e Qdrant;
- documentacao oficial ou skill especializada consultada;
- algoritmos envolvidos;
- invariantes;
- casos-limite;
- estrategia de testes;
- impactos em analytics local;
- impactos em seguranca e autorizacao;
- impactos em performance e custo;
- pontos de extensao esperados;
- contratos de substituibilidade para adapters, providers, policies e repositories;
- composition root ou mecanismo de injecao de dependencias da stack afetada;
- contratos e invariantes que nao podem quebrar;
- estrategia de compatibilidade quando um contrato existente precisar mudar.

Quando a decisao tiver efeito arquitetural duradouro, criar ou atualizar ADR.

### 3. Implementacao

Durante a implementacao:

- manter commits pequenos e coerentes;
- usar mensagens em portugues seguindo Conventional Commits;
- preservar contratos explicitos entre stacks;
- atualizar docs junto com mudancas de comportamento;
- evitar refatoracoes fora do escopo da branch;
- preferir adicionar um adapter, provider, policy, strategy, schema ou use case novo a modificar fluxos centrais que ja estejam cobertos por testes.
- evitar `new`/instanciacao direta de infraestrutura em use cases ou dominio; ligar concretos por IoC/DI.

### 4. Verificacao

Antes de fechar a branch:

- executar testes aplicaveis;
- verificar lint/format quando existir;
- revisar migracoes e rollback operacional;
- conferir isolamento por workspace quando houver dados;
- conferir tratamento de erro e estados vazios;
- conferir eventos de analytics local quando a feature gerar comportamento rastreavel;
- revisar documentacao alterada;
- conferir aderencia ao gate SOLID/OCP-LSP-IoC e registrar qualquer quebra intencional.

### 5. Fechamento

Ao fim do escopo:

- atualizar o registro de bordo;
- adicionar entrada no diario de bordo;
- listar testes executados;
- listar riscos remanescentes;
- abrir pull request para `develop`.

Se a etapa parar no meio:

- atualizar a sessao viva do registro de bordo;
- registrar proximo passo concreto;
- registrar arquivos/decisoes tocados;
- registrar comandos de verificacao ja executados ou pendentes.

## Definition of Ready

Uma branch esta pronta para comecar quando:

- o objetivo esta claro;
- o escopo cabe em uma revisao;
- as dependencias conhecidas estao listadas;
- os documentos de referencia foram identificados;
- os riscos principais foram nomeados;
- existe criterio de aceite verificavel;
- decisoes arquiteturais bloqueantes foram tomadas ou marcadas como pendentes explicitas.

## Definition of Done

Uma branch esta pronta para pull request quando:

- o escopo combinado foi implementado ou explicitamente reduzido;
- testes aplicaveis foram executados ou a ausencia deles foi justificada;
- docs afetados foram atualizados;
- registro de bordo foi atualizado;
- diario de bordo recebeu uma entrada narrativa;
- riscos remanescentes foram registrados;
- o PR aponta para `develop`.

## Colateralidades e conformidade

Toda branch deve avaliar seus impactos colaterais. A profundidade da avaliacao depende do risco, mas os topicos abaixo devem ser considerados.

| Area | Pergunta minima |
| --- | --- |
| Dados e privacidade | A mudanca cria, move, duplica, expira ou expoe dados do usuario? |
| Workspace e isolamento | Alguma query, evento, arquivo, job ou vetor precisa de `workspace_id`? |
| Autorizacao | Existe nova acao que exige permissao no backend? |
| Segredos | A mudanca toca credenciais, API keys, conectores ou variaveis sensiveis? |
| Banco relacional | Ha migracao, indice, constraint, historico ou risco de dados inconsistentes? |
| Vector DB | Ha alteracao em payload, filtros, embeddings, delete, upsert ou reindexacao? |
| Worker Python | Ha risco de memoria, tempo, OCR pesado, arquivo malformado ou dependencia nativa? |
| API e contratos | A mudanca quebra endpoint, DTO, erro, status ou compatibilidade? |
| Frontend | Existem estados de loading, erro, vazio, permissao negada e responsividade? |
| Analytics local | A mudanca deve registrar evento, falha, latencia, feedback ou auditoria? |
| Performance | Existe risco de N+1, payload grande, job longo, consulta lenta ou custo de embedding? |
| Observabilidade | Logs, status de job e mensagens de erro ajudam a diagnosticar falhas? |
| Agentes e skills | A mudanca envolve ferramenta, MCP, skill, prompt, contexto, guardrail ou chamada agentica? |
| Testes | Ha teste de contrato, comportamento, autorizacao, migracao ou caso-limite? |
| Documentacao | O README, ADR, API, padroes ou diario precisam mudar? |
| OCP | A mudanca expande por contrato/adapter/policy ou altera fluxo central fora do escopo? |
| LSP | Uma nova implementacao preserva pre-condicoes, pos-condicoes, erros, filtros, workspace scope, idempotencia e ordenacao esperada? |
| IoC/DI | Use cases/domain continuam dependendo de abstracoes locais, com concretos ligados no composition root da stack? |
| Compatibilidade | Algum consumidor existente de API, evento, schema, tool, migration ou payload pode quebrar? |

## Gate SOLID/OCP-LSP-IoC

Antes de fechar uma branch, responder:

- Qual comportamento novo foi adicionado e qual comportamento existente permaneceu intacto?
- O ponto de variacao ficou explicito por contrato, port, adapter, policy, strategy, evento ou schema?
- A nova implementacao e substituivel pela anterior sem mudar semantica observavel?
- Existe suite de contrato compartilhada para implementations da mesma familia?
- O core application/domain depende apenas de abstracoes locais?
- Onde os concretos sao ligados: Spring configuration, Angular provider, FastAPI dependency, factory/composition root ou setup MCP?
- A mudanca exigiu editar uma area lateral ao escopo? Se sim, por que era inevitavel?
- Ha teste que protege o contrato antigo e o novo comportamento?
- Algum dado existente, workspace, evento, vetor, arquivo ou tool schema precisa de migracao ou compatibilidade?
- A implementacao introduziu condicionais globais ou acoplamento entre stacks que deveriam estar atras de uma abstracao local?

Quando uma resposta indicar risco lateral, a branch deve registrar esse risco no bordo e adicionar teste de regressao ou ajuste documental antes do PR.

## Algoritmos e performance

Branches que implementarem algoritmos devem registrar uma especificacao curta antes do codigo.

Modelo recomendado:

```text
Nome:
Objetivo:
Entrada:
Saida:
Invariantes:
Passos:
Complexidade esperada:
Casos-limite:
Falhas conhecidas:
Testes:
Metricas observaveis:
```

Diretrizes:

- Preferir algoritmos legiveis antes de micro-otimizacoes.
- Medir antes de otimizar quando o gargalo nao for obvio.
- Manter funcoes pequenas, com nomes que expliquem intencao.
- Separar decisao de dominio de detalhe de infraestrutura.
- Registrar complexidade quando houver loop sobre paginas, elementos, chunks, vetores ou eventos.
- Evitar processamento sincrono longo quando a experiencia pedir job assincromo.

Areas que exigem especificacao propria:

- inferencia de numeracao impressa;
- chunking semantico;
- montagem de contexto;
- deduplicacao de chunks;
- filtros e ranking de recuperacao;
- upsert/delete/reindexacao no Qdrant;
- retencao e agregacao de analytics local;
- processamento de OCR e paginas pesadas.

## Sequencia candidata de implementacao

Esta sequencia resumida mostra o inicio do fluxo. A sequencia completa e os arquivos individuais por branch vivem em [Branches plan](../branches-plan/README.md). O detalhamento por escopo, fora de escopo e criterios de testabilidade tambem aparece em [Plano de branches e escopos](plano-de-branches.md).

| Ordem | Branch candidata | Objetivo |
| --- | --- | --- |
| 0 | `planejamento/documentacao` | Consolidar documentacao inicial e camada operacional. |
| 1 | `docs/plano-tecnico-mvp` | Incorporada em `planejamento/documentacao`; fechou decisoes tecnicas minimas antes do codigo. |
| 1.1 | `docs/modelo-dados-contratos-mvp` | Fechar modelo de dados, contratos e OpenAPI. |
| 1.2 | `docs/arquitetura-ingestao-rag` | Fechar state machine, pipeline e reindexacao. |
| 1.3 | `docs/seguranca-permissoes-mvp` | Fechar auth, permissoes, workspace scope e guardrails. |
| 1.4 | `docs/algoritmos-rag-mvp` | Especificar algoritmos centrais e budgets. |
| 1.5 | `docs/analytics-observabilidade-mvp` | Fechar eventos, logs, retencao e metricas. |
| 1.6 | `docs/interpretacao-imagens-tabelas-pdf` | Fechar imagens/tabelas em PDFs, assets, elementos e vision interpreter. |
| 2 | `chore/workspace-fundacao` | Criar estrutura raiz e convencoes do repositorio. |
| 3 | `build/dev-runtime-compose` | Subir compose local com Postgres, Qdrant e servicos preparados. |
| 4 | `chore/backend-spring-base` | Criar base Spring Boot testavel. |
| 5 | `chore/frontend-angular-base` | Criar base Angular testavel. |
| 6 | `chore/worker-python-base` | Criar base Python worker testavel. |
| 7 | `test/smoke-stack-local` | Validar a stack local com testes de fumaca. |
| 8 | `feat/auth-bootstrap-workspaces` | Criar primeira fatia vertical de usuarios/workspaces. |

## Artefatos vivos

A camada operacional depende de dois artefatos vivos:

- [Trigger de implementacao agentica](trigger-implementacao-agentica.md): ponto de partida para retomar ambiente, Git, branch, plano especifico, skills e gates antes de implementar.
- [Registro de bordo](registro-de-bordo.md): tabela operacional, status das branches, sessao viva e proximos passos.
- [Diario de bordo](diario-de-bordo.md): narrativa cronologica do que foi feito, por que foi feito e o que mudou no entendimento do projeto.
- [Plano de branches e escopos](plano-de-branches.md): sequencia detalhada de branches, escopos, fora de escopo e testabilidade.
- [Cobertura do MVP pelo plano de branches](cobertura-mvp-branches.md): matriz de criterios do MVP, lacunas e branches responsaveis.
- [Revisao de coesao documental](revisao-coesao-documental.md): fotografia das decisoes vigentes e ajustes feitos para remover divergencias documentais.
- [Branches plan](../branches-plan/README.md): pasta viva com um `.md` por branch.
- [Escopos detalhados das branches](escopos-detalhados-branches.md): mini-especificacao de execucao por branch.
- [Plano tecnico do MVP](plano-tecnico-mvp.md): decisoes tecnicas minimas antes da primeira branch de codigo.
- [Estrategia de testes](estrategia-de-testes.md): piramide de testes, compose e2e e criterios por tipo de branch.
