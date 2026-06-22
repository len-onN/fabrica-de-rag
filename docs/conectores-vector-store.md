# Conectores de Bancos Vetoriais

## Visao

A Fabrica de RAG deve comecar com um banco vetorial padrao controlado pela propria aplicacao, inicialmente Qdrant via Docker Compose. Em fases posteriores, a aplicacao podera integrar bancos vetoriais do usuario.

Essa integracao e interessante, mas precisa de limites claros. O objetivo nao e aceitar qualquer colecao vetorial arbitraria como se ela ja tivesse todo o conhecimento documental necessario. O objetivo e permitir que o usuario escolha onde os vetores sao armazenados sem perder proveniencia, controle de contexto e confiabilidade.

Em um deploy multiusuario, conectores tambem carregam uma preocupacao de acesso: uma conexao vetorial pode ser pessoal, criada por um usuario, ou compartilhada no workspace. O sistema deve separar a conexao externa, o segredo e o binding entre uma colecao da Fabrica e um indice remoto.

## Principio

O SQL documental continua sendo a fonte da verdade.

```text
SQL
- documentos
- paginas
- elementos
- assets
- chunks
- relacoes
- runs de ingestao
- bindings de indices vetoriais

Vector store
- vetores
- ids
- payload minimo para busca e filtros
```

O banco vetorial e um indice derivado. Se ele for apagado, pode ser reconstruido a partir do SQL, storage e configuracoes de ingestao.

## Niveis de suporte

```text
Nivel 1: sincronizacao/exportacao
- A Fabrica mantem seu indice principal.
- O usuario tambem envia vetores para um banco externo.
- Baixo risco e bom para integracoes.

Nivel 2: backend vetorial externo gerenciado
- A colecao usa um banco externo como indice principal.
- A Fabrica cria ou valida schema, dimensao, metrica e payload.
- Melhor primeiro alvo pos-MVP.

Nivel 3: colecao existente compativel
- O usuario aponta para uma colecao existente.
- A aplicacao valida se ela segue o contrato minimo.
- Pode permitir busca e RAG completo se os metadados forem suficientes.

Nivel 4: colecao arbitraria/search-only
- A aplicacao consulta vetores existentes sem garantir RAG completo.
- Sem proveniencia completa, citacoes e context builder ficam limitados.
```

## Providers candidatos

Ordem sugerida:

1. Qdrant externo.
2. PostgreSQL com pgvector.
3. Pinecone.
4. Weaviate.
5. Milvus.

Qdrant externo e o primeiro candidato porque o MVP ja usara Qdrant local, reduzindo a distancia tecnica do adapter.

## Modelo conceitual

```text
vector_connections
- id
- name
- owner_scope: user | workspace
- owner_user_id
- workspace_id
- provider
- endpoint
- auth_secret_ref
- visibility
- mode
- capabilities
- created_at
- updated_at

vector_index_bindings
- id
- workspace_id
- knowledge_collection_id
- vector_connection_id
- remote_collection_name
- remote_namespace
- vector_name
- embedding_model
- dimension
- distance_metric
- payload_contract_version
- sync_status
- last_sync_at
```

## Escopo e permissoes

Tipos de conexao:

```text
personal
- criada por um usuario
- gerenciada apenas pelo dono
- pode ser usada em colecoes autorizadas, se a politica permitir

workspace
- criada para um workspace
- gerenciada por owner/admin
- usada por curators ou service accounts com permissao
```

Permissoes candidatas:

```text
vector_connection.create
vector_connection.use
vector_connection.manage
vector_connection.delete
vector_binding.create
vector_binding.delete
```

Regras:

- segredo fica referenciado por `auth_secret_ref`, nunca exposto em payloads de API;
- usar uma conexao e gerenciar uma conexao sao permissoes diferentes;
- uma colecao so escreve em indice remoto por meio de um binding explicito;
- agentes e service accounts precisam de permissao explicita para usar bindings;
- analytics local deve registrar uso da conexao sem registrar endpoint sensivel ou credencial.

## Contrato minimo de payload

Campos esperados em cada ponto vetorial criado pela aplicacao:

```text
project_id
workspace_id
knowledge_collection_id
document_id
chunk_id
content_kind
embedding_model
embedding_model_version
vector_space
file_page_start
file_page_end
printed_page_start
printed_page_end
heading_path
source_hash
payload_contract_version
```

O payload externo deve ser suficiente para recuperar candidatos e voltar ao SQL para montar o contexto completo.

## Validacoes necessarias

Antes de usar uma conexao, a aplicacao deve validar:

- autenticacao;
- permissao de leitura;
- permissao de escrita, quando aplicavel;
- permissao do usuario/service account para usar a conexao;
- criacao de colecao/index, quando aplicavel;
- dimensao do vetor;
- metrica de distancia;
- suporte a filtros por metadados;
- suporte a namespaces/tenants, quando necessario;
- limite de tamanho de payload;
- comportamento de upsert/delete;
- busca com filtro por colecao/projeto.
- busca com filtro por workspace/colecao.

## Modos de uso

### managed_by_app

A Fabrica controla schema, escrita, payload e nomes de colecao/index.

Este deve ser o primeiro modo implementado para bancos externos.

### external_existing

A Fabrica se conecta a uma colecao existente, mas exige compatibilidade com o contrato minimo de payload.

Se a validacao falhar, o sistema deve explicar quais campos ou capacidades faltam.

### search_only

Modo limitado para colecoes arbitrarias.

Pode servir para exploracao, comparacao ou busca bruta, mas nao deve prometer:

- citacao confiavel;
- expansao de contexto;
- relacionamento com assets;
- pagina fisica/impressa;
- reconstrucao de fonte;
- RAG completo com proveniencia.

## UX proposta

Tela: Conexoes vetoriais.

Acoes:

- criar conexao;
- testar conexao;
- validar capacidades;
- criar binding com uma colecao da Fabrica;
- escolher modo de uso;
- visualizar status de sincronizacao;
- reindexar;
- desconectar sem apagar dados remotos, quando possivel.

Mensagens importantes para o usuario:

- "Banco conectado, mas sem permissao de escrita."
- "Dimensao esperada 1536, colecao remota usa 768."
- "Colecao compativel para search-only, mas sem metadados suficientes para citacoes."
- "Payload compativel com contrato v1."

## Decisao de escopo

MVP:

- Qdrant interno.
- Modelo de dados ja preparado para bindings futuros.

Pos-MVP imediato:

- Qdrant externo em modo managed_by_app.
- Conexoes externas com escopo workspace.
- Conexoes pessoais como recurso planejado, se a UX justificar.

Futuro:

- pgvector, Pinecone, Weaviate e Milvus.
- Importacao de colecoes existentes.
- Modo search-only para exploracao controlada.
