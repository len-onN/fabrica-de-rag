# Estrategia de Base de Conhecimento

## Visao

A Fabrica de RAG deve usar uma base de conhecimento propria para guiar seu desenvolvimento. Essa base deve reunir documentacao local do projeto, fontes oficiais das stacks, referencias sobre RAG/vector DBs e padroes de arquitetura decididos ao longo do caminho.

A ideia nao e carregar todo o conhecimento em todo momento. A ideia e manter colecoes disponiveis e ativar apenas as relevantes para a tarefa atual.

## Principios

- Preferir fontes oficiais ou primarias.
- Registrar versao/data das fontes.
- Separar conhecimento por stack e dominio.
- Evitar misturar documentacao de versoes diferentes.
- Ativar colecoes por contexto de trabalho.
- Manter decisoes do projeto em ADRs e documentos locais.
- Tratar a base local do projeto como fonte mais autoritativa para decisoes ja tomadas.

## Colecoes iniciais

### projeto-local

Conteudo:

- README;
- proposta principal;
- ADRs;
- planejamento;
- features;
- padroes de projeto;
- contratos;
- codigo e migracoes, quando existirem.

Uso:

- Sempre que uma decisao precisar respeitar o que ja foi combinado.
- Sempre que um agente atuar no repositorio.

### angular-moderno

Fontes iniciais:

- Angular docs: https://angular.dev/
- Angular `llms.txt`: https://angular.dev/llms.txt
- Angular Build with AI: https://angular.dev/ai
- Angular Agent Skills: https://angular.dev/ai/agent-skills
- Angular CLI MCP: https://angular.dev/ai/mcp

Uso:

- Criacao do frontend.
- Componentes, signals, forms, router, HTTP, accessibility, testing e estilos.
- Configuracao de agentes para Angular.

Observacao:

A documentacao oficial do Angular disponibiliza `llms.txt` e uma area "Build with AI". Tambem lista skills oficiais mantidas pelo time Angular, incluindo `angular-developer` e `angular-new-app`.

### spring-boot

Fontes iniciais:

- Spring Boot docs: https://docs.spring.io/spring-boot/index.html
- Spring Framework docs: https://docs.spring.io/spring-framework/reference/
- Spring Data docs: https://spring.io/projects/spring-data
- Spring Security docs: https://docs.spring.io/spring-security/reference/

Uso:

- API principal.
- Organizacao de pacotes.
- Configuracao, profiles, validacao, seguranca, testes e deploy.

### spring-ai

Fontes iniciais:

- Spring AI reference: https://docs.spring.io/spring-ai/reference/

Uso:

- Avaliar integracao Java com modelos, embeddings, vector stores, MCP e RAG.
- Decidir se Spring AI entra no backend principal ou se a orquestracao de IA fica mais separada.

Observacao:

Spring AI documenta RAG, ETL, vector databases, MCP, observability, tool calling e model evaluation.

### python-worker

Fontes iniciais:

- Python docs: https://docs.python.org/3/
- FastAPI docs: https://fastapi.tiangolo.com/
- Pydantic docs: https://docs.pydantic.dev/latest/
- PyMuPDF docs: https://pymupdf.readthedocs.io/
- Unstructured docs: https://docs.unstructured.io/

Uso:

- Worker de processamento documental.
- OCR, parsing, PDF, imagens, embeddings locais, batch jobs e APIs internas.

### postgres

Fontes iniciais:

- PostgreSQL docs: https://www.postgresql.org/docs/current/
- pgvector: https://github.com/pgvector/pgvector

Uso:

- Modelo relacional.
- Consultas, indices, transacoes, JSONB, migrations, auditoria e analytics local.
- Avaliar pgvector como alternativa ou suporte complementar.

### banco-de-dados-boas-praticas

Fontes iniciais:

- PostgreSQL docs: https://www.postgresql.org/docs/current/
- Flyway docs: https://documentation.red-gate.com/fd
- Liquibase docs: https://docs.liquibase.com/

Uso:

- Migracoes.
- Nomeacao.
- Integridade referencial.
- Estrategias de indices.
- Evolucao de schema.
- Auditoria e historico.

### vector-store

Fontes iniciais:

- Qdrant docs: https://qdrant.tech/documentation/
- pgvector: https://github.com/pgvector/pgvector
- Weaviate docs: https://docs.weaviate.io/
- Pinecone docs: https://docs.pinecone.io/
- Milvus docs: https://milvus.io/docs

Uso:

- Colecoes vetoriais.
- Payloads.
- Named vectors.
- Filtros.
- Conectores futuros.
- Busca textual, multimodal e hibrida.

### rag-engineering

Fontes iniciais:

- Spring AI RAG: https://docs.spring.io/spring-ai/reference/
- LlamaIndex RAG guide: https://developers.llamaindex.ai/python/framework/understanding/rag/
- LangChain docs: https://docs.langchain.com/
- Paper original de RAG: https://arxiv.org/abs/2005.11401

Uso:

- Chunking.
- Retrieval.
- Reranking.
- Context builder.
- Avaliacao.
- Grounding e citacoes.

### mcp-agentes

Fontes iniciais:

- Model Context Protocol docs: https://modelcontextprotocol.io/docs/getting-started/intro
- Spring AI MCP: https://docs.spring.io/spring-ai/reference/
- Angular CLI MCP: https://angular.dev/ai/mcp

Uso:

- Ferramentas MCP.
- Contratos de agentes.
- Limites, seguranca e tool calling.

### seguranca-e-acesso

Fontes iniciais:

- Spring Security docs: https://docs.spring.io/spring-security/reference/
- Spring Security OAuth2 Resource Server: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html
- Spring Security Method Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
- OWASP ASVS: https://owasp.org/www-project-application-security-verification-standard/
- OWASP Authorization Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html
- PostgreSQL Row Security Policies: https://www.postgresql.org/docs/current/ddl-rowsecurity.html

Uso:

- Autenticacao.
- Autorizacao.
- Roles e permissoes.
- Workspaces.
- Chaves de API.
- Identidades de agentes.
- Hardening futuro com Row Level Security.

### padroes-de-projeto

Conteudo:

- decisoes internas;
- padroes de codigo;
- padroes de arquitetura;
- convencoes de banco;
- estilo de API;
- padroes de testes;
- padroes de documentacao.

Uso:

- Sempre que a implementacao comecar em uma area nova.
- Sempre que uma decisao local precisar prevalecer sobre uma recomendacao generica.

## Politica de ativacao

Ativar apenas o necessario:

```text
Frontend Angular
-> projeto-local
-> angular-moderno
-> padroes-de-projeto

Backend Spring
-> projeto-local
-> spring-boot
-> seguranca-e-acesso
-> postgres
-> padroes-de-projeto

Worker Python
-> projeto-local
-> python-worker
-> rag-engineering
-> padroes-de-projeto

Vector DB
-> projeto-local
-> vector-store
-> rag-engineering

MCP
-> projeto-local
-> mcp-agentes
-> seguranca-e-acesso
-> spring-ai
```

## Ingestao futura

Quando a aplicacao tiver o pipeline de ingestao funcionando, poderemos criar colecoes reais para essas fontes.

Regras:

- registrar URL, versao e data de coleta;
- evitar copiar fontes com licenca inadequada;
- preferir links e resumos quando o conteudo nao puder ser redistribuido;
- separar documentacao oficial de artigos e opinioes;
- revisar colecoes quando versoes principais mudarem;
- manter uma colecao "project-local" sempre atualizada a partir do repositorio.

## Ordem recomendada

1. Catalogar fontes.
2. Definir padroes de projeto minimos.
3. Implementar MVP.
4. Ingerir documentacao local do proprio projeto.
5. Ingerir Angular e Spring Boot.
6. Ingerir Postgres, Qdrant e RAG.
7. Usar o proprio produto para consultar suas decisoes e referencias.
