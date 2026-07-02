# Fabrica de RAG

Aplicacao web para criar, curar, testar e servir bancos de conhecimento vetoriais como RAGs, com foco em pesquisa, estudo, desenvolvimento assistido, controle de ingestao, rastreabilidade das fontes e uso por agentes via API e MCP.

Repositorio remoto planejado:

https://github.com/len-onN/fabrica-de-rag

## Estado atual

Este repositorio esta na fundacao tecnica testavel. A camada operacional, o plano de branches, a estrategia de testes, o baseline tecnico inicial, os contratos do MVP e a estrutura raiz do monorepo ja estao registrados; o marco atual e `build/dev-runtime-compose`, responsavel por criar o runtime local inicial com Postgres e Qdrant.

## Trigger de implementacao agentica

Antes de qualquer agente ou sessao iniciar implementacao, seguir o [Trigger de implementacao agentica](docs/trigger-implementacao-agentica.md).

Esse trigger estabelece o rito de retomada:

1. ler documentacao viva: README, registro de bordo, diario, estrategia operacional, plano de branches, `branches-plan`, plano tecnico, padroes, testes, skills e ADRs;
2. checar estado Git: branch atual, mudancas locais, remoto, upstream e sincronizacao;
3. sincronizar com a remota quando for seguro, usando `git fetch --prune` e `git pull --ff-only`;
4. continuar a branch ativa ou criar a proxima branch planejada a partir de `develop`;
5. abrir o plano especifico da branch em `branches-plan`;
6. ativar contexto e skills relevantes;
7. aplicar o gate SOLID/OCP-LSP-IoC antes de editar codigo;
8. atualizar registro de bordo, implementar em passos pequenos, testar e registrar resultados.

Se o Git bloquear a checagem por `dubious ownership`, o agente nao deve contornar silenciosamente. Deve pedir autorizacao para configurar `safe.directory` ou orientar execucao manual pelo usuario.

## Documentacao

- [Proposta principal](docs/document-cemetery/proposta-principal.md)
- [Proposito e casos de uso](docs/document-cemetery/proposito-e-casos-de-uso.md)
- [Features planejadas](docs/document-cemetery/features.md)
- [MVP: interface e API](docs/document-cemetery/mvp-interface-e-api.md)
- [Modelo de dados e contratos do MVP](docs/modelo-dados-contratos-mvp.md)
- [Arquitetura de ingestao e RAG](docs/arquitetura-ingestao-rag.md)
- [Seguranca e permissoes do MVP](docs/seguranca-permissoes-mvp.md)
- [Algoritmos RAG do MVP](docs/algoritmos-rag-mvp.md)
- [Analytics e Observabilidade do MVP](docs/analytics-observabilidade-mvp.md)
- [Interpretacao de Imagens e Tabelas em PDFs](docs/interpretacao-imagens-tabelas-pdf.md)
- [Design visual](docs/design-visual.md)
- [Estrategia de base de conhecimento](docs/base-de-conhecimento.md)
- [Padroes de projeto e desenvolvimento](docs/padroes-de-projeto.md)
- [Git e commits](docs/git-e-commits.md)
- [Trigger de implementacao agentica](docs/trigger-implementacao-agentica.md)
- [Estrategia operacional de desenvolvimento](docs/estrategia-operacional.md)
- [Plano de branches e escopos](docs/plano-de-branches.md)
- [Cobertura do MVP pelo plano de branches](docs/cobertura-mvp-branches.md)
- [Revisao de coesao documental](docs/revisao-coesao-documental.md)
- [Branches plan](branches-plan/README.md)
- [Escopos detalhados das branches](docs/escopos-detalhados-branches.md)
- [Plano tecnico do MVP](docs/plano-tecnico-mvp.md)
- [Estrategia de testes](docs/estrategia-de-testes.md)
- [Skills e boas praticas para agentes](docs/skills-e-boas-praticas-para-agentes.md)
- [Registro de bordo](docs/registro-de-bordo.md)
- [Diario de bordo](docs/diario-de-bordo.md)
- [Usuarios, workspaces e acesso](docs/usuarios-e-acesso.md)
- [Fontes e integracoes futuras](docs/fontes-e-integracoes-futuras.md)
- [Decisoes de arquitetura](docs/decisoes-arquiteturais.md)
- [Conectores de bancos vetoriais](docs/conectores-vector-store.md)
- [Analytics local de RAG](docs/analytics-local-rag.md)
- [Planejamento](docs/document-cemetery/planejamento.md)

## Estrutura inicial e Execucao Local

O projeto e divido em 4 aplicacoes principais e componentes de infraestrutura:

```text
.
+-- apps
|   +-- api       (Spring Boot / Java 21)
|   +-- mcp       (Node.js / TypeScript)
|   +-- web       (Angular 22 / Node 24)
|   +-- worker    (Python 3.13 / FastAPI)
+-- docs          (Base de conhecimento)
+-- infra         
|   +-- compose   (Docker Compose da stack dependente)
+-- scripts       (Utilitarios operacionais)
+-- tests         (Fixtures e testes globais)
```

### 1. Dependencias Base

Suba os servicos auxiliares (Postgres, Qdrant) usando Docker Compose:

```powershell
.\scripts\compose-up.ps1 -Profile dev
```
*(Para desligar: `.\scripts\compose-down.ps1 -Profile dev`)*

### 2. Backend (API)

Requisitos: Java 21.

```powershell
cd apps\api
.\mvnw spring-boot:run
```
*A API roda por padrao na porta `8080`.*

### 3. Worker (Extracao PDF/ML)

Requisitos: Python 3.13 e `uv`.

```powershell
cd apps\worker
uv sync
uv run uvicorn src.rag_worker.main:app --reload
```
*O Worker roda por padrao na porta `8000`. Swagger em `http://127.0.0.1:8000/docs`.*

### 4. Frontend (Web)

Requisitos: Node.js 24 LTS.

```powershell
cd apps\web
npm install
npm run start
```
*O Web App roda por padrao na porta `4200`.*

### 5. Servidor MCP (Opcional - Ferramentas para Agentes)

Requisitos: Node.js 24 LTS.

```powershell
cd apps\mcp
npm install
npm run build
npm run start
```

### Validacao de Ambiente e Testes

O repositorio contem scripts PowerShell para validar e orquestrar as pecas.

```powershell
# Valida estrutura raiz, contratos e formato Compose
.\scripts\check.ps1

# Testes E2E via Playwright (necessario subir compose.e2e primeiro)
.\scripts\test-e2e.ps1
```

> **Troubleshooting Comum:**
> - Se `check.ps1` falhar com `docker compose config`, verifique se o Docker Desktop esta aberto.
> - Se o backend apontar "Connection refused" para o Qdrant, garanta que ele esteja de pe executando `curl http://localhost:6333/healthz`.
> - Erro de porta `8080` em uso: certifique-se que o backend Spring Boot nao esteja rodando duplicado.

## Stacks escolhidas ate aqui

- Frontend: Angular 22.x, Node.js 24 LTS, npm/package-lock, componentes proprios e Angular CDK.
- Backend principal: Spring Boot 4.1.x, Java 21 LTS, Maven wrapper e Flyway.
- Worker de ML/documentos: Python 3.13.x, FastAPI, Pydantic e container separado.
- MCP server: TypeScript/Node 24 em `apps/mcp`, como adaptador fino sobre contratos do backend.
- Banco relacional: PostgreSQL 18.x.
- Banco vetorial inicial: Qdrant 1.18.x interno via Docker Compose.
- Fonte documental inicial: PDF.
- Interpretacao de imagens/tabelas em PDFs: extracao de assets/elementos e descricao textual por adapter visual/LLM quando habilitado.
- Conectores vetoriais futuros: Qdrant externo, Pinecone, Weaviate, Milvus e pgvector.
- Armazenamento de arquivos: filesystem local no desenvolvimento; MinIO/S3 como caminho natural para ambientes maiores.
- Integracao com agentes: API HTTP no MVP funcional e MCP basico como trilho nao bloqueante.
- Testabilidade: unitarios, contratos, integracao, smoke local, Compose e2e e Playwright para fluxos criticos.
- Analytics local: eventos e auditoria para o usuario avaliar e melhorar seus proprios RAGs.
- Base de conhecimento do desenvolvimento: colecoes ativaveis por stack, fonte oficial/primaria e decisoes versionadas em ADRs.
- Usuarios e acesso: modo local com usuario unico, mas arquitetura preparada para workspaces, roles e deploy multiusuario.
- Design visual: minimalista, com modo claro/escuro, violeta como identidade e verde como sinal de confianca/sucesso.

## Principios do produto

- O usuario nao deve precisar editar vetores diretamente.
- A engenharia de conhecimento deve acontecer por ingestao, curadoria, relacoes, politicas de contexto e avaliacao.
- O sistema deve preservar proveniencia: documento, pagina fisica, pagina impressa, bbox, elemento, chunk e evidencia visual.
- PDFs, incluindo texto e imagens internas, devem ser tratados como fontes estruturaveis, nao apenas como texto plano.
- Imagens, diagramas e tabelas em PDFs devem virar elementos interpretaveis, com source locator, texto derivado e possibilidade de citacao.
- A recuperacao vetorial deve encontrar ancoras; a montagem final do contexto deve ser controlada por politicas previsiveis.
- A geracao com LLM deve ampliar pesquisa, estudo e desenvolvimento sem romper o vinculo com fontes reais.
- O analytics local deve ajudar o usuario a entender qualidade, falhas, uso de contexto e comportamento de agentes sem enviar dados externos por padrao.
- O desenvolvimento do projeto deve ser guiado por uma base de conhecimento propria, com fontes confiaveis e ativacao contextual.
- O acesso a colecoes, documentos, agentes, analytics e configuracoes deve ser sempre escopado por workspace e permissao.
- O MVP deve focar em PDF, mas o modelo deve permitir expansao natural para novas fontes e conectores.
- A interface deve ser minimalista, densa e legivel, usando cor como sinal semantico e nao como decoracao excessiva.
