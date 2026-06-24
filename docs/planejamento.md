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

## Questoes em discussao

- Quais controles de ingestao entram no MVP?
- Qual sera o primeiro nivel de suporte visual em PDFs?
- O MVP deve incluir apenas texto/OCR ou tambem extracao de imagens em PDFs?
- Qual sera o formato inicial do MCP server?
- Vamos iniciar com Qdrant apenas, ou tambem pgvector?
- Quando entram conectores para bancos vetoriais do usuario?
- Qual contrato minimo uma colecao externa precisa seguir?
- Qual sera a taxonomia inicial de eventos de analytics local?
- Quais dashboards locais de qualidade do RAG entram no MVP?
- Quais finalidades amplas de uso devem existir como categorias declaradas?
- O laboratorio de recuperacao entra no primeiro MVP ou em seguida?
- Quais fontes entram primeiro na base de conhecimento do desenvolvimento?
- Quais padroes de projeto precisam ser decididos antes de iniciar codigo?
- Qual o minimo de usuarios, workspaces e roles que entra no MVP sem virar produto enterprise?
- Quais campos do modelo precisam existir agora para permitir conectores de fontes no futuro?

## MVP candidato

Um primeiro MVP usavel poderia conter:

- Upload de PDF.
- Escopo inicial restrito a PDF como fonte documental.
- Perfil de ingestao rapido/balanceado.
- Extracao de texto nativo.
- OCR opcional por pagina.
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
- Mapa de fontes e integracoes futuras sem implementacao no MVP.

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

## Backlog de discussoes proximas

- Modelo de dados documental.
- Mapa de paginas e numeracao impressa.
- Perfis de ingestao.
- Estrategias de chunking.
- Politicas de contexto.
- Tratamento de imagens em PDF.
- Mapa de integracoes futuras: Notion, Jira, Confluence, GitHub/GitLab, Drive/Docs e websites.
- API publica de consulta.
- Usuarios, workspaces, roles e chaves de API.
- MCP server.
- Conectores de vector store do usuario.
- Telemetria remota opt-in para possivel deploy em nuvem.
- Analytics maduro com ciencia de dados.
- Ingestao automatizada de documentacao oficial por versao.
- Avaliacao de qualidade do RAG.
- Sequencia detalhada de branches de implementacao.
- Criterios de abertura e fechamento de PRs para `develop`.
- Teste de fumaca inicial da stack local.
