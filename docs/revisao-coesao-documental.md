# Revisao de Coesao Documental

Data: 2026-06-24

## Objetivo

Verificar se a documentacao do projeto esta coesa entre si antes de iniciar os proximos portoes de planejamento e a implementacao.

Esta revisao procura:

- informacoes conflitantes;
- documentos antigos falando como se decisoes ja tomadas ainda estivessem abertas;
- direcionamentos distintos para o mesmo tema;
- escopos MVP marcados de forma divergente;
- diferencas entre a ideia do produto, o plano tecnico, o plano de branches e os arquivos em `branches-plan`.

## Conclusao

A ideia do projeto esta coesa: a Fabrica de RAG e uma aplicacao local/self-hosted para criar, inspecionar, testar e servir bases RAG com proveniencia forte, foco inicial em PDF, SQL como fonte da verdade, Qdrant como indice derivado, analytics local, workspaces/roles desde cedo e agentes por API/MCP controlado.

Foram encontrados alguns desalinhamentos textuais, principalmente por documentos iniciais ainda usarem linguagem de discussao ou candidatura. Eles foram corrigidos para refletir a versao vigente.

## Versao vigente por tema

| Tema | Decisao vigente |
| --- | --- |
| Fonte documental do MVP | Apenas PDF como fonte suportada. Outros formatos ficam para futuro. |
| Imagens/tabelas em PDF | MVP inclui camada de extracao e interpretacao textual derivada para imagens, diagramas e tabelas, usando vision interpreter por adapter quando habilitado. |
| OCR | Opcional por pagina no MVP, com configuracao e sem OCR pesado no e2e por padrao. |
| Perfis de ingestao | `rapido` e `balanceado` no MVP funcional; visual, forense e personalizado ficam para evolucao. |
| Vector store | Qdrant interno no MVP. pgvector e demais providers ficam para futuro/conectores. |
| SQL | PostgreSQL como fonte da verdade documental; vector DB e indice derivado. |
| MCP | Runtime e tools basicas ficam no trilho do MVP como extensao nao bloqueante; MCP completo/admin fica fora. |
| API HTTP | Entra no MVP funcional para consulta RAG com auth, workspace scope, limites e citacoes. |
| Analytics | Analytics local entra no MVP; telemetria remota fica fora e so pode ser futura, opt-in e sem conteudo. |
| Usuarios/acesso | Usuario/admin bootstrap, workspaces, memberships e roles basicas entram desde o modelo inicial. |
| Service accounts | Identidade/role `agent` prevista; service accounts completas ficam fora do MVP. |
| RLS | Fora do MVP; hardening futuro. |
| Stack | Angular 22/Node 24, Spring Boot 4.1/Java 21, Python 3.13, PostgreSQL 18, Qdrant 1.18, Docker Compose, MCP TypeScript/Node. |
| Processo | `develop -> branch -> PR`, com trigger agentico antes de implementar. |
| Gates | SOLID/OCP-LSP-IoC, contratos explicitos, workspace scope, testes de contrato e colateralidades por branch. |

## Ajustes realizados

### Planejamento vivo

`docs/planejamento.md` foi atualizado para deixar de listar como "questoes em discussao" pontos que ja foram decididos ou encaminhados.

Agora o documento separa:

- estado das decisoes;
- MVP vigente;
- proximas especificacoes com branch dona;
- futuro pos-MVP.

### Features

`docs/features.md` recebeu o status `MVP nao bloqueante` para itens que estao no trilho do MVP, mas nao bloqueiam o fechamento do MVP funcional.

Tambem foi corrigido:

- perfis de ingestao do MVP: `rapido` e `balanceado`;
- analytics API separado de analytics MCP;
- MCP server/tools como `MVP nao bloqueante`.

### Interface/API

`docs/mvp-interface-e-api.md` foi ajustado para:

- incluir API HTTP de consulta RAG como parte do MVP funcional;
- deixar claro que MCP completo/admin fica fora;
- registrar MCP basico como extensao nao bloqueante.

### Proposta e fontes futuras

`docs/proposta-principal.md` e `docs/fontes-e-integracoes-futuras.md` foram ajustados para diferenciar interpretacao textual derivada de imagens/tabelas, que entra no MVP, de embeddings visuais e busca multimodal por imagem, que ficam fora.

### Branches e grupos

`docs/escopos-detalhados-branches.md` foi alinhado com `docs/plano-de-branches.md` usando:

- Grupo 1A: portoes de planejamento do MVP;
- Grupo 1B: fundacao tecnica testavel.

### Analytics e MCP

`docs/analytics-local-rag.md` e `branches-plan/01e-docs-analytics-observabilidade-mvp.md` foram ajustados para deixar claro que o schema de MCP pode ser previsto, mas os eventos de MCP so existem quando `feat/mcp-tools-base` entrar.

### ADR

`docs/decisoes-arquiteturais.md` recebeu ADR propria para a camada de interpretacao de imagens e tabelas em PDFs, com vision interpreter por adapter e testes mockados.

## Pontos observados sem conflito

- `docs/plano-tecnico-mvp` continua vigente mesmo com `branches-plan/01-docs-plano-tecnico-mvp.md` marcado como incorporado em `planejamento/documentacao`.
- `docs/base-de-conhecimento.md` pode citar pgvector, Pinecone, Weaviate e Milvus como fontes de estudo porque isso nao implica entrada no MVP.
- A role `agent` existir no modelo inicial nao contradiz service accounts completas ficarem fora do MVP.
- Eventos com origem MCP podem ser previstos em schema, desde que nao sejam criterio bloqueante do MVP funcional.
- O trigger de implementacao agentica complementa, nao substitui, a estrategia operacional.
- Embeddings visuais ficarem fora do MVP nao contradiz interpretacao textual por LLM/VLM: a interpretacao gera texto derivado citavel; embeddings visuais e busca por imagem continuam futuro.

## Risco residual

O repositorio ainda esta em fase documental. Como ainda nao ha codigo, algumas decisoes continuam deliberadamente em nivel de contrato conceitual e serao fechadas nos portoes:

- IDs, entidades e OpenAPI;
- state machine de ingestao;
- algoritmos e budgets;
- taxonomia final de eventos;
- contrato do vision interpreter, provider visual e politicas de privacidade/budget;
- biblioteca final de OpenAPI;
- ferramenta de lock do worker Python.

Essas pendencias nao sao contradicoes; elas tem branch dona.

## Criterio de validade

Se uma branch futura encontrar conflito entre documentos, a ordem de resolucao deve ser:

1. `docs/registro-de-bordo.md` para estado vivo.
2. `docs/revisao-coesao-documental.md` para decisao consolidada desta revisao.
3. ADRs aceitos em `docs/decisoes-arquiteturais.md`.
4. `docs/plano-tecnico-mvp.md` e `docs/plano-de-branches.md`.
5. Arquivo especifico da branch em `branches-plan`.
6. Documento de dominio afetado.

Quando a decisao mudar, atualizar os documentos afetados na mesma branch e registrar no diario de bordo.
