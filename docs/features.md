# Features Planejadas

Este documento registra ideias de funcionalidades. Os status iniciais sao apenas marcadores de planejamento.

## Status

- Proposto: ideia registrada, ainda nao detalhada tecnicamente.
- Planejado: escopo razoavelmente definido para uma fase.
- MVP: escopo do primeiro produto usavel.
- MVP nao bloqueante: planejado no trilho do MVP, mas nao bloqueia o MVP funcional de ingestao, recuperacao e citacoes.
- Futuro: util, mas fora do primeiro ciclo.

## Ingestao de documentos

| Feature | Status | Observacoes |
| --- | --- | --- |
| Upload de PDF | MVP | Entrada principal do produto. |
| Upload de Markdown, HTML e TXT | Futuro | Mapear como fontes futuras, sem entrar no MVP. |
| Upload de DOCX/EPUB/MOBI | Futuro | Possivel por conversao/extracao estrutural, mas fora do MVP. |
| Perfis de ingestao | MVP | Rapido e balanceado no MVP funcional; visual, forense e personalizado ficam para evolucao planejada. |
| Runs de ingestao auditaveis | MVP | Registrar parametros, modelos, versoes e resultados. |
| Reprocessamento por perfil | Planejado | Permitir comparar resultados entre estrategias. |

## PDF, paginas e numeracao

| Feature | Status | Observacoes |
| --- | --- | --- |
| Mapa de paginas fisicas do PDF | MVP | Base para citacoes e navegacao. |
| Pagina impressa detectada/inferida | MVP | Separar pagina fisica e pagina humana. |
| Edicao manual de ancora de numeracao | MVP | Ex.: PDF p. 14 = impresso p. 2. |
| Segmentos de numeracao | MVP | Corpo, prefacio, anexos, reinicios. |
| Pular paginas vazias na sequencia | Planejado | Paginas podem nao contar na numeracao impressa. |
| Marcar paginas ignoradas no RAG | MVP | Capa, paginas vazias, separadores, ruido. |

## Extracao e elementos

| Feature | Status | Observacoes |
| --- | --- | --- |
| Extracao de texto nativo | MVP | Primeiro caminho para PDFs digitais. |
| OCR por pagina | MVP | Manual/automatico quando texto nativo for insuficiente. |
| Deteccao de tabelas | MVP | Tabelas nao devem ser quebradas como texto comum. |
| Extracao de imagens embutidas | MVP | Associar imagem, pagina, bbox e texto proximo. |
| Render de paginas | MVP | Base para mapa de paginas, OCR opcional e preview de citacoes. |
| Interpretacao visual por LLM/VLM | MVP | Gerar texto estruturado derivado para imagens, diagramas e tabelas quando habilitado. |
| Crops de regioes relevantes | Futuro | Evidencia visual granular. |

## Chunking e curadoria

| Feature | Status | Observacoes |
| --- | --- | --- |
| Chunking por bloco semantico | MVP | Cortar preferencialmente em fim de paragrafo. |
| Heading path herdado | MVP | Cada chunk deve carregar contexto de titulo/secao. |
| Overlap configuravel | MVP | Valor controlado por perfil. |
| Preservar tabelas e legendas | MVP | Evitar quebrar unidades atomicas. |
| Edicao/mescla/divisao manual de chunks | Futuro | Recurso forte de curadoria. |
| Relacoes entre chunks/elementos | MVP | previous, next, same_section, caption_of_image, table_continuation. |

## Vetorizacao e busca

| Feature | Status | Observacoes |
| --- | --- | --- |
| Embeddings textuais | MVP | Base inicial do RAG. |
| Qdrant como vector DB | MVP | Indice vetorial principal. |
| Qdrant externo | Planejado | Primeiro conector para banco vetorial do usuario. |
| pgvector como alternativa | Futuro | Pode simplificar setups menores e setups all-in-Postgres. |
| Embeddings visuais | Futuro | CLIP/SigLIP ou similar para busca multimodal. |
| Busca hibrida | Planejado | Vetorial + filtros + possivel busca lexical. |
| Reranking | Planejado | Melhorar precisao antes do context builder. |

## Conectores de vector store

| Feature | Status | Observacoes |
| --- | --- | --- |
| Registro de conexoes vetoriais | Planejado | Endpoint, provider, credenciais e capacidades. |
| Validacao de conexao | Planejado | Testar autenticacao, permissao de escrita, busca e filtros. |
| Binding entre colecao e indice remoto | Planejado | Relacionar colecao da Fabrica com colecao/index externo. |
| Contrato de payload padronizado | Planejado | Garantir chunk_id, document_id, modelo, dimensao e proveniencia minima. |
| Modo managed_by_app | Planejado | A Fabrica cria/valida schema e controla escrita. |
| Escopo personal/workspace para conexoes | Planejado | Separar conexoes pessoais de conexoes compartilhadas no workspace. |
| Permissao de uso vs gerenciamento | Planejado | Usuario pode usar uma conexao sem ver ou administrar credenciais. |
| Modo external_existing | Futuro | Colecoes existentes, inicialmente com restricoes. |
| Suporte Pinecone | Futuro | Adapter apos Qdrant externo. |
| Suporte Weaviate | Futuro | Adapter apos Qdrant externo. |
| Suporte Milvus | Futuro | Adapter apos Qdrant externo. |
| Modo search-only | Futuro | Consulta limitada em colecoes que nao seguem o contrato completo. |

## Fontes e integracoes futuras

| Feature | Status | Observacoes |
| --- | --- | --- |
| Contrato comum de fonte | Planejado | Preparar `source_connector`, `source_item` e `source_locator`. |
| Importacao de pasta local | Futuro | Sincronizar PDFs de diretorios locais. |
| Notion | Futuro | Paginas, databases, permissoes, blocos e atualizacoes incrementais. |
| Jira | Futuro | Issues, comentarios, anexos, projetos, filtros JQL e permissoes. |
| GitHub/GitLab | Futuro | Repositorios, issues, PRs, wiki e documentacao. |
| Confluence | Futuro | Espacos, paginas, anexos e hierarquia. |
| Google Drive/Docs | Futuro | Documentos e permissoes externas. |
| Websites/sitemaps | Futuro | Crawling controlado, robots, canonical URLs e atualizacao. |
| Email/exportacoes | Futuro | Somente com regras fortes de privacidade e escopo. |
| Conversao para PDF | Futuro | Apenas como representacao visual, nao como unica fonte semantica. |

## Context builder

| Feature | Status | Observacoes |
| --- | --- | --- |
| Expansao por vizinhos | MVP | Incluir chunks antes/depois conforme politica. |
| Expansao por secao | Planejado | Entregar secao quando multiplas ancoras indicarem necessidade. |
| Expansao por relacoes | MVP | Tabela, imagem, legenda, referencia cruzada quando houver relacao direta e budget. |
| Orcamento de tokens | MVP | Montagem controlada do pacote de contexto. |
| Citacoes com pagina fisica e impressa | MVP | Evitar confusao de fonte. |

## Laboratorio de recuperacao

| Feature | Status | Observacoes |
| --- | --- | --- |
| Perguntar contra uma colecao | MVP | Testar RAG antes de publicar. |
| Ver chunks recuperados | MVP | Transparencia basica. |
| Ver contexto expandido | MVP | Mostrar o que foi enviado ao modelo. |
| Marcar relevante/irrelevante | MVP | Feedback local para avaliacao e melhoria. |
| Comparar perfis/modelos | Futuro | Benchmark interno de configuracoes. |

## Analytics local de RAG

| Feature | Status | Observacoes |
| --- | --- | --- |
| Taxonomia versionada de eventos locais | MVP | Eventos com schema rigido e origem UI/API/worker/MCP. |
| Auditoria de ingestao | MVP | Perfis, etapas, duracao, erros, OCR, paginas revisadas e reprocessamentos. |
| Analytics do laboratorio de recuperacao | MVP | Consultas, chunks recuperados, contexto expandido, latencia e feedback local. |
| Feedback de relevancia | MVP | Marcar chunk relevante/irrelevante e resposta util/inutil dentro da propria instancia. |
| Analytics API | MVP | Chamadas de API, limites atingidos, erros e latencia. |
| Analytics MCP | MVP nao bloqueante | Ferramentas chamadas, sequencias, limites atingidos, erros e loops detectados quando `feat/mcp-tools-base` entrar. |
| Dashboard local de qualidade do RAG | MVP | Visao para o usuario melhorar colecoes, documentos e politicas. |
| Exportacao de analytics local | MVP | Exportar dados locais basicos para estudo proprio. |
| Retencao configuravel | MVP | Usuario decide quanto historico local manter. |
| Telemetria remota opt-in | Futuro | Apenas se houver deploy/distribuicao, desligada por padrao e sem conteudo. |
| Ciencia de dados local/remota | Futuro | Coortes, anomalias, recomendacoes de presets e estudos agregados. |

## API e MCP

| Feature | Status | Observacoes |
| --- | --- | --- |
| API HTTP de consulta RAG | MVP | Servir respostas para aplicacoes externas. |
| API de administracao de colecoes | MVP | Criar, listar, reindexar e consultar status. |
| MCP server | MVP nao bloqueante | Ferramentas para agentes investigarem colecoes; nao bloqueia o MVP funcional se ficar como extensao planejada. |
| Ferramenta MCP search_chunks | MVP nao bloqueante | Busca controlada. |
| Ferramenta MCP expand_context | MVP nao bloqueante | Expansao segura de contexto. |
| Ferramenta MCP ask_rag | MVP nao bloqueante | Pergunta completa com citacoes. |

## Usuarios, workspaces e acesso

| Feature | Status | Observacoes |
| --- | --- | --- |
| Modo usuario unico local | MVP | Primeiro uso simples, com usuario/admin bootstrap local. |
| Modelo de workspaces | MVP | Toda colecao, documento, job e analytics pertence a um workspace. |
| Membership por workspace | MVP | Usuario participa de workspace com role definida. |
| Roles basicas | MVP | owner, admin, curator, member, viewer e agent. |
| Autorizacao por permissao | MVP | Checar acao e recurso, nao apenas esconder botoes no frontend. |
| Convites de usuarios | Planejado | Necessario para servidor multiusuario. |
| Chaves de API por workspace | Planejado | Uso por automacoes e clientes externos. |
| Service accounts para agentes | Planejado | Identidade propria para MCP/API sem simular usuario humano. |
| Workspaces pessoais automaticos | Planejado | Cada usuario pode ter bases e conectores proprios sem criar equipe. |
| Conexoes externas pessoais | Futuro | Usuario conecta seu proprio vector DB sem compartilhar credenciais. |
| Provedor externo OIDC/OAuth2 | Futuro | Keycloak, Auth0, Google, GitHub ou outro provedor. |
| Row Level Security no Postgres | Futuro | Camada extra de isolamento para deploys mais sensiveis. |

## Base de conhecimento do desenvolvimento

| Feature | Status | Observacoes |
| --- | --- | --- |
| Catalogo de fontes oficiais por stack | MVP | Angular, Spring Boot, Python, Postgres, Qdrant, RAG e MCP. |
| Colecoes ativaveis por fase | MVP | Ativar apenas o conhecimento relevante para a tarefa atual. |
| Colecao local do projeto | MVP | Docs, ADRs, backlog, contratos e codigo gerado. |
| Registro de versoes das fontes | MVP | Evitar mistura de documentacao antiga e nova. |
| Politica de ingestao de documentacao | Planejado | Frequencia, licenca, chunking, atualizacao e verificacao. |
| Base de padroes de projeto | Planejado | Arquitetura, codigo, banco, backend, worker Python e vector DB. |

## Design visual

| Feature | Status | Observacoes |
| --- | --- | --- |
| Tema claro | MVP | Fundo claro violeta-neutro, superficies brancas e bordas suaves. |
| Tema escuro | MVP | Fundo violeta profundo, superficies escuras e contraste legivel. |
| Tokens de cor | MVP | Primary, success, warning, danger, background, surface, border e text. |
| Preferencia de tema | MVP | Sistema, claro ou escuro. |
| Violeta como identidade | MVP | Acoes principais, foco, links importantes e selecao. |
| Verde como confianca | MVP | Sucesso, validado, pronto, chunk relevante e qualidade boa. |
| Neutros operacionais | MVP | Base de leitura, tabelas, paineis e superficies de trabalho. |

## Operacao

| Feature | Status | Observacoes |
| --- | --- | --- |
| Docker Compose local | MVP | Angular, Spring Boot, Python worker, Postgres, Qdrant. |
| Jobs de ingestao observaveis | MVP | Runs com status, etapas, cancelamento/retry e logs; fila fica para quando necessario. |
| Observabilidade basica | MVP | Logs, status de jobs, erros por pagina. |
| Controle de custos | Futuro | Importante quando houver modelos via API. |
