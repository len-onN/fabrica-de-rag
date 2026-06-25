# Planejamento

## Fase atual: concepcao em transicao para planejamento tecnico

Objetivos:

- Registrar a proposta principal.
- Registrar o proposito: pesquisa, estudo, geracao com fontes reais, desenvolvimento assistido e agentes guiados por conhecimento.
- Listar features candidatas.
- Registrar decisoes de arquitetura.
- Criar estrategia de base de conhecimento do desenvolvimento.
- Discutir UX de engenharia de conhecimento.
- Definir criterios do MVP.
- Registrar a camada operacional de desenvolvimento por branches, PRs, registro de bordo e diario de bordo.

## Estado das decisoes

As discussoes iniciais abaixo ja foram consolidadas ou encaminhadas para branches donas. A versao vigente e:

- O MVP foca em PDF como unica fonte documental suportada.
- O suporte visual do MVP inclui render de paginas, preview de fonte, OCR opcional, extracao de imagens/tabelas e interpretacao textual por LLM/VLM quando habilitada.
- Embeddings visuais, busca multimodal por imagem e curadoria profunda de regioes ficam fora do MVP funcional.
- O MCP nasce como runtime TypeScript/Node 24 em `apps/mcp`, adaptador fino sobre contratos do backend.
- Qdrant interno e o vector store inicial; pgvector fica como possibilidade futura.
- Conectores externos de vector store ficam fora do MVP funcional, mas o modelo deve preparar bindings futuros.
- A taxonomia final de analytics, retencao, export/delete e logs esta definida em `docs/analytics-observabilidade-mvp`.
- O laboratorio de recuperacao entra no MVP funcional.
- Usuarios, workspaces, memberships e roles basicas entram desde o modelo inicial.
- Decisoes estruturais de modelo, ingestao, seguranca, algoritmos, analytics e interpretacao de imagens/tabelas em PDFs foram encaminhadas por portoes `docs/*` antes do codigo.
- O desenvolvimento deve seguir o gate SOLID/OCP-LSP-IoC e o trigger de implementacao agentica.

## MVP vigente

O primeiro MVP usavel deve conter:

- Upload de PDF.
- Escopo inicial restrito a PDF como fonte documental.
- Perfil de ingestao rapido/balanceado.
- Extracao de texto nativo.
- OCR opcional por pagina.
- Interpretacao de imagens, diagramas e tabelas em PDFs, gerando texto estruturado derivado.
- Mapa de paginas com pagina fisica e pagina impressa.
- Ancora manual de numeracao impressa.
- Chunking por bloco semantico.
- Embeddings textuais.
- Qdrant para busca vetorial.
- Qdrant interno via Docker Compose como vector store padrao.
- PostgreSQL para metadados.
- Modelo de usuarios, workspaces, memberships e roles basicas.
- API HTTP para consulta RAG.
- Tela de laboratorio mostrando pergunta, chunks recuperados, contexto expandido e citacoes.
- Analytics local de RAG com eventos versionados, auditoria, feedback e dashboards de qualidade.
- Categorias amplas e opcionais de finalidade do workspace: estudo, pesquisa, escrita, codigo, agente ou geral.
- Docker Compose com Angular, Spring Boot, Python worker, Postgres e Qdrant.
- Catalogo inicial da base de conhecimento do desenvolvimento.
- Mapa de fontes e integracoes futuras sem implementacao de conectores no MVP.

## Fase 1: desenho funcional

Entregaveis:

- Fluxos de usuario.
- Casos de uso prioritarios: estudo, pesquisa, escrita, codigo e agentes.
- Modelo conceitual de dados.
- Mapa de telas.
- Contrato inicial de interface e API do MVP.
- Taxonomia inicial de eventos de analytics local.
- Politica de retencao e privacidade local.
- Estrategia de base de conhecimento e fontes por stack.
- Primeira versao dos padroes de projeto.
- Estrategia operacional de desenvolvimento.
- Politica de skills e boas praticas para agentes.
- Registro de bordo inicial.
- Diario de bordo inicial.
- Modelo funcional de usuarios, roles e permissoes.
- Contrato conceitual para fontes futuras: source connector, source item e source locator.
- Criterios de aceite do MVP.
- Backlog priorizado.

## Fase 2: planejamento tecnico do MVP

Entregaveis:

- Estrutura dos repositorios/pacotes.
- Contratos REST entre Angular, Spring Boot e Python worker.
- Rotas de frontend, estados de tela e endpoints publicos/internal API.
- Esquema inicial do banco.
- Estrutura das colecoes Qdrant.
- Contrato de payload vetorial para futuros conectores.
- Especificacao dos jobs de ingestao.
- Estrategia de testes.
- Design do coletor/agregador local de analytics.
- Plano de ingestao da base de conhecimento tecnica.
- Esquema inicial de usuarios, workspaces, memberships e autorizacao.
- Modelo preparado para registrar origem/localizador de fonte sem depender apenas de paginas PDF.
- Docker Compose inicial.
- Mapa de skills/fontes oficiais por branch tecnica.
- Plano de implementacao por branches, com escopo, criterios de aceite e PR para `develop`.
- Plano de fundacao minima testavel e fatias verticais de desenvolvimento.
- Plano tecnico do MVP com estrutura de repo, padroes de projeto, contratos e decisoes de teste.
- Estrategia de testes com unitarios, contratos, integracao, smoke e e2e via Docker Compose.
- Checklist de colateralidades por branch: dados, permissoes, workspace, analytics, performance, contratos e testes.
- Especificacao inicial dos algoritmos criticos antes de implementacao.

## Fase 3: implementacao do MVP

Entregaveis:

- Aplicacao Angular inicial.
- API Spring Boot.
- Worker Python.
- Pipeline minimo de ingestao.
- Busca RAG basica.
- Laboratorio de recuperacao.
- Analytics local de RAG.
- Base local do projeto alimentada com docs, ADRs e padroes.
- Modo local com usuario unico e estrutura multiworkspace pronta.
- Documentacao de execucao local.
- Registro de bordo atualizado ao fim de cada branch.
- Diario de bordo atualizado ao fim de cada etapa ou antes de pausas longas.

## Portoes especificados antes do codigo

Estas frentes ja tem especificacao detalhada para orientar as branches de implementacao:

- Modelo de dados documental: `docs/modelo-dados-contratos-mvp`.
- Mapa de paginas, numeracao impressa, chunking, contexto e citacoes: `docs/algoritmos-rag-mvp`.
- Ingestao, retry, cancelamento, storage e reindexacao: `docs/arquitetura-ingestao-rag`.
- Auth, sessao/token, roles, workspace scope e guardrails: `docs/seguranca-permissoes-mvp`.
- Analytics, retencao, export/delete, logs e dashboard minimo: `docs/analytics-observabilidade-mvp`.
- Interpretacao de imagens/tabelas em PDFs, assets, elementos e vision interpreter: `docs/interpretacao-imagens-tabelas-pdf`.
- Trigger, branch, PR, skills e gates: `docs/trigger-implementacao-agentica.md` e `docs/estrategia-operacional.md`.

Continuam como futuro pos-MVP:

- conectores externos de fontes;
- conectores externos de vector store;
- telemetria remota opt-in;
- analytics maduro com ciencia de dados;
- embeddings visuais e busca multimodal por imagem;
- ingestao automatizada de documentacao oficial por versao.
