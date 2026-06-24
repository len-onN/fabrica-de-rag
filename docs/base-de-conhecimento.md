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
- Registrar skills/fontes ativadas quando uma branch depender de conhecimento especializado.
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
- branches-plan;
- estrategia operacional;
- plano tecnico do MVP;
- cobertura do MVP pelo plano de branches;
- plano de branches e escopos;
- estrategia de testes;
- skills e boas praticas para agentes;
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
- Model Context Protocol SDKs: https://modelcontextprotocol.io/docs/sdk
- Model Context Protocol architecture: https://modelcontextprotocol.io/docs/learn/architecture
- Model Context Protocol security best practices: https://modelcontextprotocol.io/docs/tutorials/security/security_best_practices
- Model Context Protocol build server: https://modelcontextprotocol.io/docs/develop/build-server
- Spring AI MCP: https://docs.spring.io/spring-ai/reference/
- Angular CLI MCP: https://angular.dev/ai/mcp

Uso:

- Ferramentas MCP.
- Contratos de agentes.
- Limites, seguranca e tool calling.

### openai-agents-referencia

Fontes iniciais:

- OpenAI Agents SDK: https://openai.github.io/openai-agents-python/
- OpenAI Agents SDK tools: https://openai.github.io/openai-agents-python/tools/
- OpenAI Agents SDK guardrails: https://openai.github.io/openai-agents-python/guardrails/
- OpenAI Agents SDK tracing: https://openai.github.io/openai-agents-python/tracing/
- OpenAI Agents SDK context management: https://openai.github.io/openai-agents-python/context/

Uso:

- Referencia de arquitetura para agentes com ferramentas.
- Guardrails.
- Tracing e depuracao de fluxos agenticos.
- Context management.
- Handoffs e agentes como ferramentas.

Observacao:

Essa colecao nao significa decisao de dependencia obrigatoria. Ela serve como fonte primaria para boas praticas de agentes quando a branch envolver ferramentas, orquestracao, guardrails, traces ou execucao assistida.

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

### skills-e-boas-praticas-para-agentes

Conteudo:

- politica local de uso de skills;
- fontes oficiais por contexto;
- boas praticas para agentes;
- guardrails;
- menor privilegio;
- contratos de ferramentas;
- observabilidade de chamadas agenticas;
- seguranca MCP.

Uso:

- Sempre que uma branch envolver agentes, MCP, ferramentas, prompts, contexto, skills ou execucao assistida.
- Sempre que uma recomendacao de agente precisar ser validada contra os limites do projeto.
- Sempre que uma ferramenta nova for exposta para uso por agentes.

### testes-e-qualidade

Fontes iniciais:

- Docker Compose multiple files: https://docs.docker.com/compose/how-tos/multiple-compose-files/
- Docker Compose profiles: https://docs.docker.com/compose/how-tos/profiles/
- Spring Boot testing: https://docs.spring.io/spring-boot/reference/testing/
- Spring Boot Testcontainers: https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
- Angular testing: https://angular.dev/guide/testing
- FastAPI testing: https://fastapi.tiangolo.com/tutorial/testing/
- pytest good practices: https://docs.pytest.org/en/stable/explanation/goodpractices.html
- Playwright Docker: https://playwright.dev/docs/docker

Uso:

- Sempre que uma branch criar contrato, infraestrutura, teste de integracao, teste de fumaca ou e2e.
- Sempre que a estrategia de teste de uma feature nao estiver clara.
- Sempre que uma branch tocar Docker Compose ou ambiente local.

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
-> testes-e-qualidade
-> padroes-de-projeto

Worker Python
-> projeto-local
-> python-worker
-> rag-engineering
-> testes-e-qualidade
-> padroes-de-projeto

Vector DB
-> projeto-local
-> vector-store
-> rag-engineering

MCP
-> projeto-local
-> mcp-agentes
-> openai-agents-referencia
-> seguranca-e-acesso
-> spring-ai

Agentes e skills
-> projeto-local
-> skills-e-boas-praticas-para-agentes
-> mcp-agentes
-> openai-agents-referencia
-> seguranca-e-acesso

Testes e e2e
-> projeto-local
-> testes-e-qualidade
-> padroes-de-projeto
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
7. Ingerir referencias de testes, Compose e e2e.
8. Ingerir MCP, seguranca e referencias de agentes.
9. Usar o proprio produto para consultar suas decisoes e referencias.
