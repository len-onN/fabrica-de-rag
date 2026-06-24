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
- impactos em performance e custo.

Quando a decisao tiver efeito arquitetural duradouro, criar ou atualizar ADR.

### 3. Implementacao

Durante a implementacao:

- manter commits pequenos e coerentes;
- usar mensagens em portugues seguindo Conventional Commits;
- preservar contratos explicitos entre stacks;
- atualizar docs junto com mudancas de comportamento;
- evitar refatoracoes fora do escopo da branch.

### 4. Verificacao

Antes de fechar a branch:

- executar testes aplicaveis;
- verificar lint/format quando existir;
- revisar migracoes e rollback operacional;
- conferir isolamento por workspace quando houver dados;
- conferir tratamento de erro e estados vazios;
- conferir eventos de analytics local quando a feature gerar comportamento rastreavel;
- revisar documentacao alterada.

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
| 2 | `chore/workspace-fundacao` | Criar estrutura raiz e convencoes do repositorio. |
| 3 | `build/dev-runtime-compose` | Subir compose local com Postgres, Qdrant e servicos preparados. |
| 4 | `chore/backend-spring-base` | Criar base Spring Boot testavel. |
| 5 | `chore/frontend-angular-base` | Criar base Angular testavel. |
| 6 | `chore/worker-python-base` | Criar base Python worker testavel. |
| 7 | `test/smoke-stack-local` | Validar a stack local com testes de fumaca. |
| 8 | `feat/auth-bootstrap-workspaces` | Criar primeira fatia vertical de usuarios/workspaces. |

## Artefatos vivos

A camada operacional depende de dois artefatos vivos:

- [Registro de bordo](registro-de-bordo.md): tabela operacional, status das branches, sessao viva e proximos passos.
- [Diario de bordo](diario-de-bordo.md): narrativa cronologica do que foi feito, por que foi feito e o que mudou no entendimento do projeto.
- [Plano de branches e escopos](plano-de-branches.md): sequencia detalhada de branches, escopos, fora de escopo e testabilidade.
- [Cobertura do MVP pelo plano de branches](cobertura-mvp-branches.md): matriz de criterios do MVP, lacunas e branches responsaveis.
- [Branches plan](../branches-plan/README.md): pasta viva com um `.md` por branch.
- [Escopos detalhados das branches](escopos-detalhados-branches.md): mini-especificacao de execucao por branch.
- [Plano tecnico do MVP](plano-tecnico-mvp.md): decisoes tecnicas minimas antes da primeira branch de codigo.
- [Estrategia de testes](estrategia-de-testes.md): piramide de testes, compose e2e e criterios por tipo de branch.
