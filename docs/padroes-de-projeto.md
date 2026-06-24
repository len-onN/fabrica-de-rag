# Padroes de Projeto e Desenvolvimento

## Visao

Este documento registra os padroes que precisam ser decididos antes e durante a implementacao. Ele nao tenta congelar tudo agora. A estrategia e decidir com detalhe no momento certo, usando fontes oficiais, prototipos pequenos quando necessario e ADRs para escolhas relevantes.

## Principios gerais

- Decisoes locais documentadas prevalecem sobre recomendacoes genericas.
- Padroes devem reduzir ambiguidade, nao criar burocracia.
- Cada stack deve seguir suas convencoes idiomaticas.
- Contratos entre stacks devem ser explicitos.
- Alteracoes de banco devem passar por migracoes versionadas.
- Testes devem cobrir contratos e comportamento, nao apenas linhas.
- Observabilidade e analytics local devem nascer junto dos fluxos principais.
- Usuarios, workspaces e autorizacao devem existir como fronteira de dados desde o inicio.
- Design visual deve usar violeta como assinatura, verde como sinal semantico de confianca e neutros como base de trabalho.
- Commits devem seguir Conventional Commits, com mensagens em portugues.
- Cada branch deve registrar consideracoes tecnicas especificas antes de implementar areas sensiveis.
- Algoritmos centrais devem privilegiar leitura clara, complexidade conhecida, testes de caso-limite e metricas observaveis.
- Colateralidades de dados, permissoes, performance, analytics e contratos devem ser avaliadas como parte do fechamento da branch.
- Branches devem ativar skills e fontes oficiais por contexto, registrando o que foi consultado quando isso influenciar a decisao tecnica.
- Agentes devem operar por ferramentas guiadas, com menor privilegio, validacao, guardrails, limites e analytics local.

## Areas a decidir

### Frontend Angular

Decisoes pendentes:

- estrutura de pastas;
- estrategia de estado;
- uso de signals, resources e RxJS;
- padrao de componentes;
- detalhamento do design system sobre componentes proprios + Angular CDK;
- formularios;
- roteamento;
- testes;
- acessibilidade;
- comunicacao com API;
- tratamento de erros e loading states.

Direcao inicial:

- Angular 22.x com standalone components;
- Node 24 LTS;
- signals para estado local e derivado;
- services para comunicacao e regras de frontend;
- componentes proprios com Angular CDK como base de acessibilidade;
- componentes focados em fluxos reais, sem landing page inicial;
- UI densa e utilitaria, voltada para engenharia de conhecimento;
- tema claro/escuro com tokens CSS;
- paleta minimalista violeta + verde + neutros;
- cor usada principalmente como estado, foco e hierarquia.

### Backend Spring Boot

Decisoes pendentes:

- arquitetura de modulos/pacotes;
- separacao entre application, domain e infrastructure;
- padrao de controllers e DTOs;
- validacao;
- tratamento de erros;
- seguranca;
- jobs;
- integracao com Python worker;
- integracao com Qdrant;
- event logging local.
- autenticacao e autorizacao.

Direcao inicial:

- Spring Boot como orquestrador transacional;
- Spring Boot 4.1.x com Java 21 LTS;
- PostgreSQL como fonte da verdade;
- Flyway para migracoes;
- Qdrant como indice vetorial derivado;
- Python worker separado para ML/documentos;
- APIs explicitas entre stacks.

### Usuarios e acesso

Decisoes pendentes:

- estrategia de login no MVP;
- forma de bootstrap do primeiro admin;
- roles finais;
- permissoes por recurso;
- tokens/chaves de API;
- identidade de agentes;
- convites;
- isolamento por workspace;
- auditoria de acoes sensiveis;
- provedores externos futuros.

Direcao inicial:

- modo local com usuario unico/admin bootstrap;
- modelo de workspaces desde o inicio;
- roles simples por workspace;
- autorizacao no backend, nao apenas no frontend;
- toda colecao, documento, job, analytics e chave deve pertencer a um workspace;
- service accounts/agentes devem ter identidade propria e permissoes restritas.

### Banco de dados

Decisoes pendentes:

- convencoes de nomeacao;
- uso de UUID vs bigint;
- estrategia de auditoria;
- soft delete;
- historico de jobs;
- JSONB vs tabelas normalizadas;
- indices;
- constraints;
- multi-workspace.

Direcao inicial:

- schema relacional explicito para documentos, paginas, elementos, assets, chunks e relacoes;
- migrations versionadas com Flyway;
- evitar esconder contratos importantes em JSONB;
- usar JSONB para metadados variaveis e payloads extensivos;
- preservar proveniencia desde o primeiro MVP.

### Python worker

Decisoes pendentes:

- quando migrar de HTTP interno para fila;
- estrutura de jobs;
- cache de modelos;
- processamento de PDFs;
- OCR;
- extracao de imagens;
- observabilidade;
- limites de memoria e tempo.

Direcao inicial:

- container separado;
- Python 3.13.x;
- FastAPI para API interna simples no MVP;
- Pydantic para contratos;
- evoluir para fila quando jobs longos justificarem;
- contratos JSON versionados;
- logs e eventos locais por etapa.

### Vector DB

Decisoes pendentes:

- nomes de colecoes;
- payload minimo;
- named vectors;
- estrategia de delete/upsert;
- filtros obrigatorios;
- reindexacao;
- versao de embedding;
- suporte futuro a conectores.

Direcao inicial:

- Qdrant interno como default;
- SQL guarda a verdade documental;
- vector DB guarda pontos derivados;
- cada embedding deve registrar modelo, dimensao, metrica e versao.

### RAG e context builder

Decisoes pendentes:

- tamanho de chunks;
- chunking por estrutura;
- relacoes entre chunks;
- politicas de expansao;
- reranking;
- budget de tokens;
- formato de citacao;
- avaliacao de resposta.

Direcao inicial:

- busca vetorial encontra ancoras;
- context builder monta contexto;
- expansao por vizinhos entra no MVP;
- expansao por secao/relacoes entra depois;
- citacoes devem carregar pagina fisica e impressa.

### MCP

Decisoes pendentes:

- ferramentas iniciais;
- permissoes;
- limites;
- guardrails;
- versionamento de ferramentas;
- logs locais;
- integracao com analytics local.

Direcao inicial:

- TypeScript/Node 24 em `apps/mcp`;
- MCP server como adaptador fino sobre contratos do backend;
- ferramentas guiadas, nao SQL livre;
- search_chunks, get_chunk, expand_context e ask_rag como candidatos;
- schemas explicitos para entradas e saidas de ferramentas;
- identidade propria para agentes e service accounts;
- confirmacao humana para acoes sensiveis ou destrutivas;
- registrar chamadas, limites e falhas no analytics local.

## Decisoes a transformar em ADRs

Proximas ADRs provaveis:

- estrutura de pacotes Spring Boot;
- padrao de contratos REST;
- schema inicial documental;
- estrategia inicial de chunking;
- estrutura de colecoes Qdrant;
- refinamento do design system Angular;
- tokens de tema claro/escuro;
- convencao de branches e commits;
- modelo de usuarios, workspaces e roles.
- politica de skills e boas praticas para agentes.
