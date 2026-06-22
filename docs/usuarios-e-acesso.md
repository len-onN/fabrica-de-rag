# Usuarios, Workspaces e Acesso

## Visao

A Fabrica de RAG deve ser simples para uso local, mas preparada para rodar em um servidor com multiplos usuarios. Para isso, o modelo inicial deve incluir usuarios, workspaces, memberships e roles.

O objetivo nao e criar um sistema enterprise no MVP. O objetivo e evitar que colecoes, documentos, analytics, agentes e chaves nascam sem dono, sem escopo e sem isolamento.

O ponto mais delicado e que cada usuario pode criar suas proprias bases, e no futuro tambem conectar bancos vetoriais externos com credenciais proprias. Por isso, o sistema precisa separar claramente:

```text
usuario
workspace
colecao de conhecimento
conexao vetorial externa
binding entre colecao e indice remoto
segredo/credencial
```

## Modos de uso

### Local single-user

Modo principal do MVP.

Caracteristicas:

- instancia local ou self-hosted pequena;
- bootstrap de um usuario/admin;
- um workspace padrao criado automaticamente;
- login simples ou modo dev/local, a decidir no planejamento tecnico;
- sem convites complexos no primeiro ciclo.

### Servidor multiusuario

Evolucao preparada pelo modelo.

Caracteristicas:

- varios usuarios;
- varios workspaces;
- membros com roles diferentes;
- convites;
- chaves de API;
- service accounts para agentes;
- provedor externo de identidade no futuro.

## Escopos de propriedade

Nem tudo deve pertencer diretamente ao usuario. A regra inicial deve ser:

```text
Usuarios possuem memberships.
Workspaces possuem recursos.
Conexoes externas podem ser pessoais ou compartilhadas no workspace.
Segredos nunca sao expostos depois de criados.
```

### Workspace pessoal

Cada usuario pode ter um workspace pessoal criado automaticamente.

Uso:

- estudos individuais;
- bases privadas;
- testes;
- conectores pessoais;
- agentes pessoais.

### Workspace compartilhado

Um workspace compartilhado permite colaboracao.

Uso:

- bases de equipe;
- documentos compartilhados;
- conectores administrados pelo owner/admin;
- agentes de um grupo;
- analytics local do workspace.

### Conexao externa pessoal

Uma conexao criada por um usuario para uso proprio.

Regras:

- so o dono pode gerenciar;
- pode ser usada por colecoes em workspaces onde ele tenha permissao, se a politica permitir;
- nao deve revelar credenciais a outros membros;
- pode ser revogada pelo proprio usuario.

### Conexao externa do workspace

Uma conexao criada para uso do workspace.

Regras:

- owner/admin gerenciam;
- curators podem usar se tiverem permissao;
- viewers/members nao devem ver segredo;
- service accounts podem usar apenas se explicitamente permitido.

## Modelo conceitual

```text
users
- id
- display_name
- email
- status
- created_at

auth_identities
- id
- user_id
- provider
- provider_subject
- password_hash
- created_at

workspaces
- id
- name
- slug
- owner_user_id
- created_at

workspace_memberships
- id
- workspace_id
- user_id
- role
- status
- created_at

api_keys
- id
- workspace_id
- created_by_user_id
- name
- key_hash
- role
- expires_at
- revoked_at

service_accounts
- id
- workspace_id
- name
- purpose
- role
- created_at

vector_connections
- id
- owner_scope: user | workspace
- owner_user_id
- workspace_id
- provider
- endpoint
- auth_secret_ref
- visibility
- created_at

vector_index_bindings
- id
- workspace_id
- knowledge_collection_id
- vector_connection_id
- remote_collection_name
- remote_namespace
- created_at
```

Toda entidade principal deve ser escopada por workspace, diretamente ou por relacionamento claro:

```text
knowledge_collections
documents
document_pages
document_elements
assets
chunks
ingest_runs
analytics_events
vector_index_bindings
mcp_sessions
```

## Roles iniciais

```text
owner
- controla workspace, membros, configuracoes e exclusoes criticas

admin
- administra colecoes, ingestao, conexoes e usuarios

curator
- cria e ajusta bases, documentos, chunks, paginas e politicas de contexto

member
- consulta bases, usa laboratorio e pode gerar respostas conforme permissao

viewer
- leitura e consulta sem alterar configuracao

agent
- identidade restrita para MCP/API, com limites e escopo definidos
```

Roles sao por workspace, nao globais, com excecao futura de `instance_admin` para administracao da instalacao.

## Permissoes por dominio

Exemplos de permissoes que podem ser derivadas das roles:

```text
workspace.manage
members.invite
members.remove
collection.create
collection.update
collection.delete
document.upload
document.reprocess
document.delete
page_map.edit
chunk.curate
rag.query
rag.configure
analytics.view
api_key.create
vector_connection.create
vector_connection.use
vector_connection.manage
vector_binding.create
mcp.use
mcp.manage
```

A UI pode esconder acoes, mas a regra real deve ser validada no backend.

## Autorizacao

Direcao inicial:

- toda request autenticada carrega user_id e workspace_id ativo;
- endpoints sempre validam permissao no backend;
- queries filtram por workspace_id;
- conexoes externas validam permissao de uso e permissao de gerenciamento separadamente;
- service accounts e API keys recebem permissoes restritas;
- agentes MCP nao devem herdar permissoes ilimitadas de um usuario humano.

Exemplo conceitual:

```text
Usuario pergunta ao RAG
-> backend autentica usuario
-> valida membership no workspace
-> valida permissao rag.query
-> filtra colecao por workspace
-> valida se a colecao pode usar o indice vetorial vinculado
-> registra analytics local no mesmo workspace
```

## API keys e agentes

Agentes e integracoes devem usar identidade propria.

Isso evita:

- agente operando como admin humano sem limite;
- dificuldade de auditar chamadas;
- impossibilidade de revogar acesso especifico;
- mistura de comportamento humano e comportamento automatico.

Direcao:

```text
API key ou service account
-> role agent
-> permissoes explicitas
-> limites de chamadas
-> limites de contexto
-> logs/analytics com origem agent
```

## Banco de dados e isolamento

No MVP, o isolamento principal deve ser por aplicacao:

- entidades com workspace_id;
- colecoes e conexoes externas separadas por binding;
- repositories/queries sempre filtrando por workspace;
- testes para evitar vazamento cross-workspace;
- constraints e indices compostos quando necessario.

Futuro:

- Row Level Security no PostgreSQL para ambientes mais sensiveis;
- politicas por recurso;
- auditoria imutavel de acoes administrativas;
- segregacao fisica por workspace, se um dia fizer sentido.

## MVP proposto

Entram no MVP:

- users;
- auth_identities simples;
- workspaces;
- workspace_memberships;
- roles basicas;
- bootstrap do primeiro usuario/admin;
- workspace padrao;
- autorizacao no backend;
- workspace_id nas entidades principais;
- analytics local escopado por workspace.
- estrutura para diferenciar conexao vetorial pessoal e conexao vetorial do workspace.

Ficam para depois:

- convites completos;
- recuperacao de senha;
- MFA;
- OIDC/OAuth2 externo;
- SSO;
- instance_admin completo;
- Row Level Security;
- politicas customizadas por recurso;
- compartilhamento refinado de conexoes pessoais;
- painel administrativo avancado.

## Riscos

Riscos de adicionar cedo:

- mais tabelas;
- mais checagens em endpoints;
- mais testes;
- mais estados de UI;
- mais cuidado em jobs e worker Python.

Riscos de adicionar tarde:

- migracao dificil;
- dados sem dono;
- analytics misturado;
- colecoes sem isolamento;
- credenciais externas compartilhadas por acidente;
- conexoes pessoais usadas por agentes sem permissao explicita;
- agentes com permissoes ambiguas;
- risco de vazamento entre usuarios em deploy servidor.

Conclusao:

```text
Adicionar o modelo cedo.
Manter a experiencia local simples.
Adiar recursos enterprise.
```

## Fontes de referencia

- Spring Security: https://docs.spring.io/spring-security/reference/
- OAuth2 Resource Server no Spring Security: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html
- Method Security no Spring Security: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
- OWASP ASVS: https://owasp.org/www-project-application-security-verification-standard/
- OWASP Authorization Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html
- PostgreSQL Row Security Policies: https://www.postgresql.org/docs/current/ddl-rowsecurity.html
