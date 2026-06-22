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

## Areas a decidir

### Frontend Angular

Decisoes pendentes:

- estrutura de pastas;
- estrategia de estado;
- uso de signals, resources e RxJS;
- padrao de componentes;
- design system;
- formularios;
- roteamento;
- testes;
- acessibilidade;
- comunicacao com API;
- tratamento de erros e loading states.

Direcao inicial:

- Angular moderno com standalone components;
- signals para estado local e derivado;
- services para comunicacao e regras de frontend;
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
- PostgreSQL como fonte da verdade;
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

- ferramenta de migracao;
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
- evitar esconder contratos importantes em JSONB;
- usar JSONB para metadados variaveis e payloads extensivos;
- preservar proveniencia desde o primeiro MVP.

### Python worker

Decisoes pendentes:

- framework HTTP ou fila;
- contratos Pydantic;
- estrutura de jobs;
- cache de modelos;
- processamento de PDFs;
- OCR;
- extracao de imagens;
- observabilidade;
- limites de memoria e tempo.

Direcao inicial:

- container separado;
- API interna simples no MVP;
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

- servidor em Spring, Node ou processo separado;
- ferramentas iniciais;
- permissoes;
- limites;
- logs locais;
- integracao com analytics local.

Direcao inicial:

- ferramentas guiadas, nao SQL livre;
- search_chunks, get_chunk, expand_context e ask_rag como candidatos;
- registrar chamadas, limites e falhas no analytics local.

## Decisoes a transformar em ADRs

Proximas ADRs provaveis:

- ferramenta de migracao de banco;
- estrutura de pacotes Spring Boot;
- padrao de contratos REST;
- framework do Python worker;
- schema inicial documental;
- estrategia inicial de chunking;
- estrutura de colecoes Qdrant;
- design system Angular;
- tokens de tema claro/escuro;
- convencao de branches e commits;
- formato do MCP server.
- modelo de usuarios, workspaces e roles.
