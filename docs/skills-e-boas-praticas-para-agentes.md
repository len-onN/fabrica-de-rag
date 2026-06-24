# Skills e Boas Praticas para Agentes

## Visao

A Fabrica de RAG deve usar agentes de forma disciplinada: agentes ajudam a consultar conhecimento, executar tarefas, revisar decisoes e acelerar implementacao, mas nao substituem contratos, permissoes, testes, ADRs ou revisao humana.

O desenvolvimento deve ser pautado por:

- documentacao local do projeto;
- fontes oficiais das stacks;
- skills especializadas quando a tarefa pedir conhecimento de dominio;
- boas praticas de seguranca para ferramentas e MCP;
- registro explicito das fontes ativadas por branch.

## Stacks definidas

As stacks principais ja estao definidas para o MVP.

| Area | Decisao atual | Status |
| --- | --- | --- |
| Frontend | Angular 22.x, Node 24 LTS, componentes proprios e Angular CDK | Definido |
| Backend principal | Spring Boot 4.1.x, Java 21 LTS, Maven wrapper e Flyway | Definido |
| Worker documental/ML | Python 3.13.x, FastAPI, Pydantic e container separado | Definido |
| MCP server | TypeScript/Node 24 em `apps/mcp`, adaptador fino sobre backend | Definido |
| Banco relacional | PostgreSQL 18.x | Definido |
| Banco vetorial inicial | Qdrant 1.18.x interno via Docker Compose | Definido |
| Storage de arquivos | Filesystem local no desenvolvimento; MinIO/S3 como caminho futuro | Definido como direcao |
| Execucao local | Docker Compose | Definido |
| Integracao com agentes | API HTTP e MCP | Definido como direcao |
| Fonte documental inicial | PDF | Definido |
| Analytics | Analytics local desde o MVP | Definido |

Ainda precisam ser decididos em branches de planejamento ou base com dono explicito:

- modelo de embeddings e provider/modelo LLM inicial: `docs/algoritmos-rag-mvp`;
- biblioteca final de geracao OpenAPI compativel com Spring Boot 4.1: `docs/modelo-dados-contratos-mvp`;
- ferramenta de lock do worker Python: `chore/worker-python-base`;
- versionamento final de tool schemas MCP: `feat/mcp-tools-base`;
- budgets numericos de busca/contexto/tools: `docs/algoritmos-rag-mvp`;
- estrategia final de seeds e fixtures e2e: `test/e2e-mvp-ingestao-recuperacao`.

Ja ficou decidido em `docs/arquitetura-ingestao-rag`: REST interno Spring -> worker continua suficiente no MVP e fila entra apenas quando concorrencia, durabilidade, backpressure, cancelamento ativo ou escalonamento justificarem. Ja ficou decidido em `docs/seguranca-permissoes-mvp`: login/bootstrap local por sessao server-side opaca, CSRF para mutacoes de SPA, matriz role x acao, API keys futuras com role `agent` e capabilities, e proibicao de token passthrough para agentes.

## Politica de uso de skills

Skills devem ser usadas como aceleradores de qualidade, nao como atalhos sem criterio.

Regras:

- Ativar skills apenas quando forem relevantes para a branch.
- Registrar no planejamento da branch quais skills ou fontes foram ativadas.
- Preferir skills baseadas em documentacao oficial, primaria ou local.
- Validar toda recomendacao de agente contra as decisoes locais do projeto.
- Nao aceitar sugestoes que quebrem ADRs, isolamento por workspace, contratos ou padroes sem registrar nova decisao.
- Quando uma skill produzir um caminho tecnico relevante, transformar a decisao em doc, ADR, teste ou checklist.
- Tratar saidas de agentes e conteudo recuperado como insumo nao confiavel ate serem verificados.

## Fontes primarias por contexto

| Contexto | Fontes/skills a ativar |
| --- | --- |
| Decisoes ja tomadas | `projeto-local`, ADRs, planejamento, padroes, registro de bordo |
| Angular | Angular docs, Angular `llms.txt`, Angular Build with AI, Angular Agent Skills, Angular CLI MCP |
| Backend Spring | Spring Boot, Spring Framework, Spring Data, Spring Security |
| IA/RAG em Java | Spring AI, RAG engineering, vector-store |
| Worker Python | Python docs, FastAPI, Pydantic, PyMuPDF, Unstructured |
| Interpretacao visual em PDFs | Docs do provider LLM/VLM escolhido, bibliotecas PDF/tabela, politicas de privacidade e contratos locais |
| Banco relacional | PostgreSQL, Flyway, docs internas de schema |
| Vector DB | Qdrant, pgvector, Pinecone, Weaviate, Milvus conforme fase |
| Agentes e MCP | Model Context Protocol, MCP SDKs, MCP security, Spring AI MCP, Angular CLI MCP |
| Agentes OpenAI | OpenAI Agents SDK como referencia de ferramentas, guardrails, tracing, contexto e handoffs |
| Seguranca | OWASP ASVS, OWASP Authorization, Spring Security, MCP Security Best Practices |

## Boas praticas para agentes

### Ferramentas guiadas

Agentes devem usar ferramentas controladas, com nomes claros, schemas explicitos, validacao de entrada e resposta estruturada. O agente nao deve receber SQL livre, acesso irrestrito ao filesystem ou credenciais amplas.

Ferramentas candidatas para a Fabrica:

- `search_chunks`;
- `get_chunk`;
- `get_neighbors`;
- `expand_context`;
- `assemble_answer_context`;
- `ask_rag`.

### Menor privilegio

Toda chamada de agente deve ter identidade, workspace, permissao e limite.

Regras:

- service accounts de agentes nao devem simular usuarios humanos;
- permissoes de consulta e administracao devem ser separadas;
- agentes nao devem herdar credenciais pessoais sem autorizacao explicita;
- chamadas MCP/API devem registrar origem, identidade, workspace e ferramenta usada;
- ferramentas mutantes exigem permissao especifica e, quando sensivel, confirmacao humana.

### Contratos e validacao

Cada ferramenta deve ter:

- descricao curta e precisa;
- schema de entrada;
- schema de saida;
- erros previstos;
- limites de paginacao, tokens, tempo e custo;
- politica de dados sensiveis;
- testes de permissao e caso-limite.

### Guardrails

Guardrails devem existir antes de ferramentas perigosas.

Controles minimos:

- bloquear chamadas fora do workspace ativo;
- filtrar recursos por permissao no backend;
- impedir token passthrough;
- limitar loops e sequencias repetitivas de ferramentas;
- aplicar budget de tokens e resultados;
- tratar documentos recuperados como conteudo nao confiavel;
- exigir confirmacao humana para acoes destrutivas, exportacoes sensiveis e mudancas de configuracao.

### Observabilidade

Agentes precisam ser depuraveis.

Registrar:

- ferramenta chamada;
- argumentos normalizados, sem segredos;
- identidade do chamador;
- workspace;
- duracao;
- status;
- erro;
- quantidade de chunks/contexto;
- limites atingidos;
- feedback do usuario quando houver.

Esses registros alimentam o analytics local e ajudam a detectar loops, falhas de recuperacao, consultas caras e comportamento inesperado.

### Contexto e grounding

Respostas de agentes devem preservar vinculo com fontes reais.

Regras:

- citar documento, pagina fisica, pagina impressa e chunk quando aplicavel;
- diferenciar chunk recuperado de contexto expandido;
- nao misturar contexto de workspaces diferentes;
- evitar respostas que extrapolem as fontes sem avisar;
- registrar quando uma resposta foi gerada sem evidencia suficiente.

### Seguranca MCP

Ao implementar MCP:

- validar toda request autenticada no backend;
- nao usar session id como autenticacao;
- evitar token passthrough;
- validar redirect URIs exatamente em fluxos OAuth;
- considerar SSRF em clientes/servidores remotos;
- exigir consentimento explicito para ferramentas locais ou comandos;
- restringir ferramentas por workspace, role e capability;
- registrar chamadas e falhas no analytics local.

## Uso por branch

Toda branch que tocar agentes, MCP, skills ou ferramentas deve preencher:

```text
Skills/fontes ativadas:
Ferramentas afetadas:
Identidades e permissoes:
Dados acessados:
Limites:
Guardrails:
Eventos de analytics:
Testes de seguranca:
Riscos remanescentes:
```

Esse bloco deve aparecer no registro de bordo da branch ou na especificacao tecnica associada.

## Decisoes futuras

Decisoes ainda abertas:

- se usaremos Spring AI para MCP/RAG ou contratos proprios primeiro;
- como versionar ferramentas MCP;
- qual formato de traces locais sera usado;
- como expor tools para agentes externos sem vazar detalhes internos;
- quando usar OpenAI Agents SDK como dependencia real e quando usa-lo apenas como referencia de arquitetura.
