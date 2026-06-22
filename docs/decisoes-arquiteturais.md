# Decisoes de Arquitetura

Este documento registra decisoes em formato leve. As decisoes podem evoluir conforme o projeto amadurecer.

## ADR-001: Separacao de stacks

Status: aceito.

Decisao:

- Frontend em Angular moderno.
- Backend principal em Java com Spring Boot.
- Worker de processamento documental e ML em Python.
- Cada stack deve rodar em container proprio desde cedo.

Motivo:

O backend Java deve cuidar de API, autorizacao, orquestracao, estado transacional, jobs e publicacao. O Python deve concentrar OCR, parsing avancado, embeddings, captions e modelos locais. Essa separacao reduz acoplamento, facilita evolucao de dependencias ML e deixa claro onde cada responsabilidade vive.

## ADR-002: SQL como fonte da verdade documental

Status: aceito.

Decisao:

Usar banco relacional para representar documentos, paginas, elementos, assets, chunks, relacoes e runs de ingestao. O vector DB deve ser tratado como indice derivado.

Motivo:

RAG confiavel depende de proveniencia e relacoes. Vetores sozinhos nao explicam pagina, bbox, imagem, tabela, ordem de leitura, numeracao impressa ou contexto estrutural.

## ADR-003: Object storage para arquivos e evidencias

Status: aceito.

Decisao:

Guardar PDF original, imagens extraidas, renders de pagina, crops e thumbnails em storage separado. Em desenvolvimento, filesystem local e suficiente; para ambientes maiores, MinIO/S3.

Motivo:

O banco relacional deve guardar metadados e ponteiros, nao binarios pesados. Isso facilita cache, preview, reprocessamento e escalabilidade.

## ADR-004: Pagina fisica e pagina impressa sao conceitos diferentes

Status: aceito.

Decisao:

O sistema deve guardar pagina fisica do PDF e pagina impressa/logica separadamente. Citacoes devem mostrar ambas quando houver risco de ambiguidade.

Motivo:

PDFs frequentemente possuem capa, sumario, prefacio, anexos, paginas vazias e reinicios de numeracao. Misturar esses conceitos cria citacoes confusas e reduz confianca no RAG.

## ADR-005: Numeracao por ancoras e segmentos

Status: proposto.

Decisao:

Permitir que o usuario defina ancoras como "PDF p. 14 = impresso p. 2". A partir disso, o sistema infere a sequencia dentro de um segmento. Paginas podem ser marcadas para nao contar na numeracao.

Motivo:

Uma unica ancora confiavel pode resolver muitas paginas, mas documentos reais possuem quebras. Segmentos tornam a inferencia controlavel e auditavel.

## ADR-006: Chunk recuperavel e contexto de resposta sao coisas diferentes

Status: aceito.

Decisao:

Chunks usados para recuperacao podem ser menores. O contexto enviado ao modelo deve ser montado por um context builder, que expande ancoras conforme politicas.

Motivo:

A busca vetorial encontra candidatos. A resposta correta frequentemente exige vizinhos, secao completa, tabela relacionada, imagem, legenda ou referencias cruzadas.

## ADR-007: Acesso de agentes por ferramentas guiadas

Status: proposto.

Decisao:

Agentes devem acessar a base por ferramentas controladas, especialmente via MCP, em vez de receber SQL livre como interface primaria.

Ferramentas candidatas:

- search_chunks
- get_chunk
- get_neighbors
- get_related_chunks
- expand_context
- assemble_answer_context
- ask_rag

Motivo:

O agente pode investigar, mas o backend deve controlar permissao, budget, deduplicacao, ordenacao, filtros e proveniencia.

## ADR-008: Conectores de bancos vetoriais do usuario

Status: proposto.

Decisao:

Permitir, em fases posteriores ao MVP inicial, que o usuario conecte bancos vetoriais proprios. O primeiro modo suportado deve ser "managed_by_app": a Fabrica cria ou valida a colecao/index remoto, escreve os pontos e mantem o contrato de payload.

Modos previstos:

- managed_by_app: a aplicacao controla schema, payload e escrita.
- external_existing: a aplicacao se conecta a uma colecao existente que segue o contrato esperado.
- search_only: a aplicacao consulta uma colecao arbitraria com capacidades limitadas.

Providers candidatos:

- Qdrant externo.
- Pinecone.
- Weaviate.
- Milvus.
- PostgreSQL com pgvector.

Motivo:

Integrar bancos vetoriais do usuario aumenta a utilidade do produto e reduz lock-in. Ao mesmo tempo, RAG confiavel depende de metadados, proveniencia e relacoes que nem sempre existem em colecoes arbitrarias. Por isso, o suporte deve comecar por bancos externos controlados pela aplicacao e evoluir para importacao/search-only com validacoes explicitas.

## ADR-009: Analytics local de RAG como parte do MVP

Status: aceito.

Decisao:

Incluir analytics local de RAG no MVP. Os eventos e metricas devem ficar na propria instalacao do usuario e servir para avaliar qualidade de ingestao, recuperacao, contexto, respostas, fluxos de estudo e comportamento de agentes.

Telemetria remota de produto fica fora do MVP. Ela pode ser discutida em fases futuras, sempre opt-in, desligada por padrao e sem conteudo.

Escopo do MVP:

- taxonomia versionada de eventos;
- eventos de UI, API, ingestao, laboratorio de recuperacao e MCP;
- armazenamento local dos eventos;
- dashboard local de qualidade do RAG;
- feedback de relevancia de chunks e respostas;
- auditoria de execucoes de ingestao;
- historico local de consultas, quando habilitado pelo usuario;
- metricas de ferramentas MCP, limites e loops;
- retencao configuravel.

Fora do MVP:

- telemetria remota;
- coortes avancadas;
- experimentos/A-B testing;
- recomendacoes automaticas;
- modelos de deteccao de anomalia;
- analise preditiva de comportamento.
- perfilamento individual de usuarios.

Motivo:

O MVP sera majoritariamente self-host/local. Nesse contexto, telemetria remota tem pouco valor imediato e adiciona custo, risco e complexidade. Analytics local, por outro lado, ajuda diretamente o usuario a estudar seus RAGs, descobrir falhas, comparar configuracoes, entender comportamento de agentes e melhorar sua propria base de conhecimento.

## ADR-010: Base de conhecimento ativavel para desenvolvimento

Status: aceito.

Decisao:

Criar e manter uma base de conhecimento do proprio projeto, organizada por colecoes ativaveis. Essa base deve combinar documentacao local do projeto, fontes oficiais/primarias das stacks e decisoes arquiteturais versionadas.

Colecoes iniciais:

- projeto-local;
- angular-moderno;
- spring-boot;
- spring-ai;
- python-worker;
- postgres;
- banco-de-dados-boas-praticas;
- vector-store;
- rag-engineering;
- mcp-agentes;
- seguranca-e-acesso;
- padroes-de-projeto.

Motivo:

O projeto nasce como uma ferramenta de RAG e tambem pode se beneficiar da propria abordagem. Uma base de conhecimento ativavel ajuda a evitar decisoes por memoria solta, reduz mistura de versoes e permite que agentes consultem referencias confiaveis apenas quando forem relevantes para a tarefa.

## ADR-011: Padroes de projeto decididos progressivamente

Status: aceito.

Decisao:

Nao congelar todos os padroes de arquitetura antes do MVP. Registrar decisoes progressivamente, antes da implementacao de cada camada relevante, usando documentos de padroes e ADRs.

Areas de padrao:

- frontend Angular;
- backend Spring Boot;
- contratos API;
- modelo relacional e migracoes;
- worker Python;
- processamento de PDFs;
- vector DB;
- RAG/context builder;
- MCP;
- testes;
- observabilidade e analytics local.

Motivo:

Ha complexidade real demais para decidir tudo de uma vez com qualidade. O melhor caminho e manter principios claros, pesquisar fontes confiaveis, prototipar quando necessario e transformar decisoes maduras em padroes documentados.

## ADR-012: Usuarios, workspaces e roles desde o modelo inicial

Status: aceito.

Decisao:

Incluir uma camada de usuarios e workspaces no modelo inicial, mesmo que o primeiro uso local possa rodar em modo usuario unico. Toda colecao, documento, job, asset, analytics local, chave de API e configuracao relevante deve pertencer a um workspace.

Conexoes com bancos vetoriais externos devem ter escopo explicito: pessoal ou do workspace. O binding entre uma colecao e um indice remoto deve ser uma entidade propria, com permissoes de uso separadas das permissoes de gerenciamento da conexao.

O MVP deve suportar:

- bootstrap de usuario/admin local;
- workspaces;
- memberships;
- roles basicas por workspace;
- checagem de autorizacao no backend;
- escopo de consultas por workspace;
- separacao conceitual entre colecoes, conexoes vetoriais externas, bindings e segredos.

Roles iniciais:

- owner: controla workspace, membros, configuracoes e exclusoes;
- admin: administra colecoes, ingestao, conexoes e usuarios, sem transferir propriedade;
- curator: cria e ajusta bases, documentos, chunks, paginas e politicas de contexto;
- member: consulta bases e usa laboratorio conforme permissao;
- viewer: leitura e consulta sem alterar configuracao;
- agent: identidade restrita para MCP/API.

Fora do MVP:

- convites completos;
- provedores externos OIDC/OAuth2;
- SSO;
- MFA;
- politicas avancadas por recurso;
- Row Level Security no Postgres;
- auditoria enterprise.

Motivo:

O produto deve continuar simples para uso local, mas tambem precisa permitir deploy em servidor para multiplas pessoas. Se o isolamento por workspace nao existir desde cedo, sera dificil corrigir depois sem migracoes amplas e risco de vazamento entre colecoes. A mesma logica vale para conectores externos: credenciais pessoais ou de workspace nao podem se misturar com permissoes de colecoes e agentes. Roles simples reduzem complexidade inicial, enquanto o modelo de membership prepara a aplicacao para controles mais detalhados.

## ADR-013: PDF como unica fonte documental no MVP

Status: aceito.

Decisao:

O MVP deve aceitar apenas PDF como fonte documental. Outros formatos, como DOCX, EPUB, MOBI, Markdown, HTML e TXT, ficam fora do MVP e entram no mapa de integracoes futuras.

Conversao de outros formatos para PDF pode ser feita pelo usuario fora da aplicacao. Nesse caso, o PDF enviado passa a ser a fonte canonica para paginas, citacoes e evidencias.

Motivo:

PDF ja oferece complexidade e valor suficientes: paginas, numeracao impressa, layout, imagens, OCR, tabelas, chunks, contexto e citacoes. Aceitar muitos formatos cedo aumentaria o debito tecnico e desviaria foco do nucleo do produto.

## ADR-014: Contrato extensivel para fontes futuras

Status: proposto.

Decisao:

Mesmo focando em PDF, o modelo deve prever uma abstracao futura de fontes conectaveis. A arquitetura deve separar:

- source_connector;
- source_item;
- source_locator;
- document_element;
- chunk;
- asset;
- embedding.

Cada tipo de fonte deve preservar seu localizador proprio. PDF usa pagina/bbox; Notion usaria page/block; Jira usaria issue/comment/attachment; Git usaria repo/path/commit/linha; websites usariam URL/canonical/heading.

Motivo:

Integracoes futuras como Notion, Jira, Confluence, GitHub/GitLab, Google Drive e websites tem valor alto, mas cada uma possui semantica, permissoes, atualizacao incremental e localizadores proprios. Prever o contrato agora evita que o sistema fique preso a "pagina PDF" como unica forma de proveniencia, sem inflar o MVP com conectores.

## ADR-015: Design visual minimalista com violeta e verde

Status: aceito.

Decisao:

Usar uma proposta visual minimalista com suporte a modo claro e modo escuro. A identidade principal sera violeta, enquanto verde sera usado como cor semantica de sucesso, confianca, validacao e relevancia.

Papeis:

- violeta: marca, foco, selecao, acoes primarias e links importantes;
- verde: sucesso, pronto, validado, qualidade boa e chunks relevantes;
- neutros: fundos, superficies, textos, tabelas e paineis de trabalho;
- ambar/vermelho: alertas e erros.

Motivo:

A aplicacao e uma ferramenta de engenharia de conhecimento. Ela deve ter personalidade visual, mas precisa ser legivel, calma e utilitaria para uso prolongado. Violeta oferece identidade; verde comunica confianca e progresso; neutros preservam densidade e clareza.
