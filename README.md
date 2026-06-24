# Fabrica de RAG

Aplicacao web para criar, curar, testar e servir bancos de conhecimento vetoriais como RAGs, com foco em pesquisa, estudo, desenvolvimento assistido, controle de ingestao, rastreabilidade das fontes e uso por agentes via API e MCP.

Repositorio remoto planejado:

https://github.com/len-onN/fabrica-de-rag

## Estado atual

Este repositorio esta na fase de concepcao e planejamento tecnico do MVP. A camada operacional, o plano de branches, a estrategia de testes, o baseline tecnico inicial e a cobertura do MVP ja estao registrados; o proximo marco e fechar os portoes de planejamento antes de iniciar `chore/workspace-fundacao`.

## Documentacao

- [Proposta principal](docs/proposta-principal.md)
- [Proposito e casos de uso](docs/proposito-e-casos-de-uso.md)
- [Features planejadas](docs/features.md)
- [MVP: interface e API](docs/mvp-interface-e-api.md)
- [Design visual](docs/design-visual.md)
- [Estrategia de base de conhecimento](docs/base-de-conhecimento.md)
- [Padroes de projeto e desenvolvimento](docs/padroes-de-projeto.md)
- [Git e commits](docs/git-e-commits.md)
- [Estrategia operacional de desenvolvimento](docs/estrategia-operacional.md)
- [Plano de branches e escopos](docs/plano-de-branches.md)
- [Cobertura do MVP pelo plano de branches](docs/cobertura-mvp-branches.md)
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
- [Planejamento](docs/planejamento.md)

## Stacks escolhidas ate aqui

- Frontend: Angular 22.x, Node.js 24 LTS, npm/package-lock, componentes proprios e Angular CDK.
- Backend principal: Spring Boot 4.1.x, Java 21 LTS, Maven wrapper e Flyway.
- Worker de ML/documentos: Python 3.13.x, FastAPI, Pydantic e container separado.
- MCP server: TypeScript/Node 24 em `apps/mcp`, como adaptador fino sobre contratos do backend.
- Banco relacional: PostgreSQL 18.x.
- Banco vetorial inicial: Qdrant 1.18.x interno via Docker Compose.
- Fonte documental inicial: PDF.
- Conectores vetoriais futuros: Qdrant externo, Pinecone, Weaviate, Milvus e pgvector.
- Armazenamento de arquivos: filesystem local no desenvolvimento; MinIO/S3 como caminho natural para ambientes maiores.
- Integracao com agentes: API HTTP e MCP.
- Testabilidade: unitarios, contratos, integracao, smoke local, Compose e2e e Playwright para fluxos criticos.
- Analytics local: eventos e auditoria para o usuario avaliar e melhorar seus proprios RAGs.
- Base de conhecimento do desenvolvimento: colecoes ativaveis por stack, fonte oficial/primaria e decisoes versionadas em ADRs.
- Usuarios e acesso: modo local com usuario unico, mas arquitetura preparada para workspaces, roles e deploy multiusuario.
- Design visual: minimalista, com modo claro/escuro, violeta como identidade e verde como sinal de confianca/sucesso.

## Principios do produto

- O usuario nao deve precisar editar vetores diretamente.
- A engenharia de conhecimento deve acontecer por ingestao, curadoria, relacoes, politicas de contexto e avaliacao.
- O sistema deve preservar proveniencia: documento, pagina fisica, pagina impressa, bbox, elemento, chunk e evidencia visual.
- PDF e imagem devem ser tratados como fontes estruturaveis, nao apenas como texto plano.
- A recuperacao vetorial deve encontrar ancoras; a montagem final do contexto deve ser controlada por politicas previsiveis.
- A geracao com LLM deve ampliar pesquisa, estudo e desenvolvimento sem romper o vinculo com fontes reais.
- O analytics local deve ajudar o usuario a entender qualidade, falhas, uso de contexto e comportamento de agentes sem enviar dados externos por padrao.
- O desenvolvimento do projeto deve ser guiado por uma base de conhecimento propria, com fontes confiaveis e ativacao contextual.
- O acesso a colecoes, documentos, agentes, analytics e configuracoes deve ser sempre escopado por workspace e permissao.
- O MVP deve focar em PDF, mas o modelo deve permitir expansao natural para novas fontes e conectores.
- A interface deve ser minimalista, densa e legivel, usando cor como sinal semantico e nao como decoracao excessiva.
