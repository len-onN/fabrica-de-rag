# MVP: Interface e API

## Objetivo

Este documento descreve o contrato funcional inicial do MVP: paginas, rotas, controles, exibicoes e API. Ele deve servir como base para backlog, prototipacao de UI, contratos REST e implementacao.

O MVP foca em PDF como unica fonte documental.

## Principios de escopo

Entram no MVP:

- upload e ingestao de PDF;
- colecoes de conhecimento;
- mapa de paginas;
- numeracao impressa;
- texto nativo e OCR opcional;
- deteccao e interpretacao de imagens/tabelas em PDFs;
- chunking textual;
- embeddings textuais;
- Qdrant interno;
- laboratorio de recuperacao;
- analytics local;
- API HTTP de consulta RAG;
- usuario unico local com modelo preparado para workspaces/roles.

Ficam fora do MVP:

- conectores externos de fontes como Notion/Jira;
- upload DOCX/EPUB/MOBI;
- conectores externos de vector DB;
- MCP completo com administracao;
- convites multiusuario;
- service accounts completas;
- embeddings visuais;
- busca multimodal por imagem;
- curadoria manual profunda de chunks.

Observacao:

MCP basico (`search_chunks`, `get_chunk`, `expand_context`, `ask_rag`) fica no trilho do MVP como extensao nao bloqueante. Ele nao deve bloquear o fechamento do MVP funcional de ingestao, recuperacao, citacoes e analytics local.

## Navegacao principal

Estrutura sugerida:

```text
/setup
/login
/w/:workspaceId/dashboard
/w/:workspaceId/collections
/w/:workspaceId/collections/new
/w/:workspaceId/collections/:collectionId
/w/:workspaceId/collections/:collectionId/ingest
/w/:workspaceId/documents/:documentId
/w/:workspaceId/documents/:documentId/pages
/w/:workspaceId/collections/:collectionId/lab
/w/:workspaceId/analytics
/w/:workspaceId/settings
```

Layout global:

- sidebar com workspace atual, dashboard, colecoes, analytics e configuracoes;
- header com usuario atual, status de jobs e seletor de workspace;
- area principal densa, utilitaria e orientada a operacao;
- notificacoes para jobs de ingestao, erros e acoes concluidas.

## Paginas

### 1. Setup inicial

Rota:

```text
/setup
```

Uso:

- primeira execucao local;
- cria usuario/admin inicial;
- cria workspace padrao.

Campos:

- nome de exibicao;
- email;
- senha;
- nome do workspace;
- finalidade do workspace: estudo, pesquisa, escrita, codigo, agente ou geral.

Controles:

- checkbox: manter analytics local habilitado;
- seletor de retencao inicial: 30, 90, 180 dias ou sem expiracao automatica;
- botao "Criar ambiente";
- link "Ja existe uma conta" quando aplicavel.

Exibicoes:

- aviso de que o MVP roda local/self-hosted;
- resumo do que sera criado;
- erro de validacao;
- estado de carregamento.

### 2. Login

Rota:

```text
/login
```

Campos:

- email;
- senha.

Controles:

- botao "Entrar";
- link para setup se nao houver usuario inicial.

Exibicoes:

- erro de credenciais;
- ambiente local ativo;
- versao da aplicacao.

Observacao:

Recuperacao de senha, MFA e provedores externos ficam fora do MVP.

### 3. Dashboard do workspace

Rota:

```text
/w/:workspaceId/dashboard
```

Uso:

- visao inicial do workspace.

Controles:

- seletor de intervalo: hoje, 7 dias, 30 dias, tudo;
- botao "Nova colecao";
- botao "Enviar PDF";
- botao "Abrir laboratorio".

Exibicoes:

- total de colecoes;
- total de documentos;
- total de chunks;
- jobs em andamento;
- ultimos erros de ingestao;
- documentos mais recuperados;
- respostas marcadas como uteis/inuteis;
- chamadas MCP/API quando existirem;
- cards de acoes rapidas.

### 4. Lista de colecoes

Rota:

```text
/w/:workspaceId/collections
```

Controles:

- busca por nome;
- filtro por finalidade;
- filtro por status: vazia, processando, pronta, com erro;
- ordenacao: nome, atualizacao, numero de documentos;
- botao "Nova colecao";
- botao "Enviar PDF".

Exibicoes:

- tabela ou lista de colecoes;
- nome;
- finalidade;
- documentos;
- chunks;
- ultimo job;
- status vetorial;
- data de atualizacao;
- acoes: abrir, enviar PDF, laboratorio, configuracoes.

### 5. Nova colecao

Rota:

```text
/w/:workspaceId/collections/new
```

Campos:

- nome;
- descricao;
- finalidade: estudo, pesquisa, escrita, codigo, agente ou geral.

Controles:

- perfil de ingestao padrao: rapido ou balanceado;
- politica de contexto padrao: conservador ou sequencial;
- modelo de embedding textual: padrao configurado da instancia;
- vector store: Qdrant interno;
- botao "Criar colecao";
- botao "Cancelar".

Exibicoes:

- resumo da configuracao;
- aviso de que conectores externos ficam para futuro;
- erros de validacao.

### 6. Detalhe da colecao

Rota:

```text
/w/:workspaceId/collections/:collectionId
```

Tabs:

- Documentos;
- Chunks;
- Jobs;
- Laboratorio;
- Analytics;
- Configuracoes.

Controles globais:

- botao "Enviar PDF";
- botao "Abrir laboratorio";
- botao "Reindexar";
- menu de configuracoes.

Exibicoes:

- status da colecao;
- quantidade de documentos;
- quantidade de chunks;
- modelo de embedding;
- ultima ingestao;
- ultimos erros;
- metricas resumidas de uso local.

### 7. Wizard de envio e ingestao de PDF

Rota:

```text
/w/:workspaceId/collections/:collectionId/ingest
```

Etapa 1: arquivo

Campos e controles:

- area drag-and-drop;
- seletor de arquivo;
- aceitar apenas `.pdf`;
- botao "Validar PDF";
- botao "Cancelar".

Exibicoes:

- nome do arquivo;
- tamanho;
- hash/checksum;
- numero de paginas, quando detectado;
- erro se nao for PDF;
- erro se arquivo estiver corrompido ou protegido.

Etapa 2: perfil

Controles:

- perfil de ingestao: rapido ou balanceado;
- OCR: automatico, desligado ou forcar;
- idioma OCR: padrao da instancia, pt-BR, en-US ou outro configurado;
- revisar mapa de paginas antes de indexar: ligado por padrao;
- chunking: bloco semantico;
- tamanho alvo de chunk;
- overlap;
- politica de contexto padrao: conservador ou sequencial.

Exibicoes:

- estimativa de custo/tempo local;
- aviso sobre OCR;
- resumo da configuracao.

Etapa 3: pre-analise

Controles:

- botao "Abrir mapa de paginas";
- botao "Iniciar indexacao";
- botao "Salvar como rascunho";
- botao "Cancelar".

Exibicoes:

- paginas detectadas;
- paginas sem texto nativo;
- paginas candidatas a OCR;
- imagens/tabelas detectadas;
- possiveis paginas vazias;
- qualidade media de extracao textual;
- warnings.

### 8. Status do job de ingestao

Rota:

```text
/w/:workspaceId/ingest-runs/:runId
```

Controles:

- botao "Cancelar" quando em execucao;
- botao "Tentar novamente" quando falhar;
- botao "Abrir documento";
- botao "Abrir logs".

Exibicoes:

- status: queued, running, completed, failed, cancelled;
- etapas: validacao, extracao, OCR, elementos visuais/tabelas, interpretacao visual, chunking, embedding, indexacao;
- progresso por etapa;
- duracao;
- erros por pagina;
- parametros usados;
- modelo de embedding;
- versao do worker.

### 9. Detalhe do documento

Rota:

```text
/w/:workspaceId/documents/:documentId
```

Tabs:

- Visao geral;
- Paginas;
- Chunks;
- Analytics;
- Configuracoes.

Controles:

- botao "Abrir mapa de paginas";
- botao "Reprocessar";
- botao "Excluir documento";
- filtro por pagina;
- filtro por status: indexado, com erro, ignorado.

Exibicoes:

- arquivo original;
- hash;
- total de paginas;
- total de chunks;
- paginas ignoradas;
- paginas com OCR;
- data de upload;
- runs de ingestao associados.

### 10. Mapa de paginas

Rota:

```text
/w/:workspaceId/documents/:documentId/pages
```

Uso:

- revisar pagina fisica;
- corrigir pagina impressa;
- marcar paginas ignoradas;
- controlar sequencia de numeracao.

Layout:

- tabela de paginas a esquerda;
- preview da pagina a direita;
- painel de edicao abaixo ou lateral.

Colunas:

- pagina PDF;
- label impresso detectado;
- label impresso efetivo;
- papel da pagina: capa, sumario, corpo, anexo, em branco, desconhecido;
- entra no RAG;
- conta na numeracao;
- OCR;
- status.

Controles:

- busca/filtro por pagina;
- filtro: todas, revisar, ignoradas, OCR, sem texto, erro;
- selecionar pagina;
- selecionar intervalo;
- definir ancora: "PDF p. X = impresso p. Y";
- estilo de numeracao: arabico, romano, custom;
- continuar sequencia;
- reiniciar segmento;
- marcar como capa;
- marcar como sumario;
- marcar como corpo;
- marcar como anexo;
- marcar como pagina em branco;
- incluir/excluir do RAG;
- contar/nao contar na numeracao;
- forcar OCR;
- aplicar em intervalo;
- botao "Salvar mapa";
- botao "Recalcular labels";
- botao "Continuar ingestao".

Exibicoes:

- preview visual da pagina;
- texto extraido da pagina;
- warnings de conflito de numeracao;
- resumo de segmentos;
- impacto: paginas indexadas, ignoradas, OCR pendente.

### 11. Navegador de chunks

Rota:

```text
/w/:workspaceId/collections/:collectionId/chunks
```

Tambem pode aparecer como tab dentro da colecao.

Controles:

- busca textual simples;
- filtro por documento;
- filtro por pagina PDF;
- filtro por pagina impressa;
- filtro por status de embedding;
- filtro por chunks com feedback negativo;
- botao "Abrir origem";
- botao "Ver vizinhos".

Exibicoes:

- chunk id;
- trecho;
- documento;
- pagina PDF;
- pagina impressa;
- heading path;
- token count;
- status do embedding;
- vizinhos anterior/proximo;
- feedback local agregado.

Observacao:

Edicao manual de chunks fica fora do MVP. O MVP pode permitir inspecao e feedback.

### 12. Laboratorio de recuperacao

Rota:

```text
/w/:workspaceId/collections/:collectionId/lab
```

Controles de pergunta:

- textarea da pergunta;
- botao "Buscar";
- botao "Gerar resposta";
- botao "Limpar";
- toggle "Salvar historico local";

Controles de recuperacao:

- top-k;
- politica de contexto: conservador ou sequencial;
- vizinhos antes;
- vizinhos depois;
- budget de tokens;
- incluir paginas ignoradas: sempre desligado no MVP;
- reranker: desligado no MVP, reservado para futuro.

Exibicoes:

- resposta gerada;
- citacoes;
- chunks recuperados brutos;
- scores;
- contexto expandido;
- chunks enviados ao modelo;
- chunks descartados;
- metricas: latencia, tokens, quantidade de chunks;
- preview da fonte;
- alertas de budget atingido.

Acoes de feedback:

- marcar chunk como relevante;
- marcar chunk como irrelevante;
- marcar resposta como util;
- marcar resposta como inutil;
- marcar "faltou contexto";
- abrir pagina de origem;
- copiar citacao.

### 13. Analytics local

Rota:

```text
/w/:workspaceId/analytics
```

Controles:

- intervalo de tempo;
- filtro por colecao;
- filtro por documento;
- filtro por origem: UI, API, worker, MCP;
- botao "Exportar JSON";
- botao "Exportar CSV";
- botao "Limpar historico";

Exibicoes:

- funil de ingestao;
- erros por etapa;
- documentos mais recuperados;
- documentos menos recuperados;
- chunks mais citados;
- chunks marcados como irrelevantes;
- perguntas com baixo contexto, se historico estiver habilitado;
- respostas uteis/inuteis;
- uso de politicas de contexto;
- tempo medio por etapa;
- chamadas de API/MCP quando existirem.

### 14. Configuracoes do workspace

Rota:

```text
/w/:workspaceId/settings
```

Tabs:

- Geral;
- Ingestao;
- Analytics local;
- Acesso;
- API.

Controles gerais:

- nome do workspace;
- finalidade;
- salvar.

Controles de ingestao:

- perfil padrao;
- OCR padrao;
- idioma OCR padrao;
- interpretacao de imagens/tabelas: desligada, automatica ou forcar;
- provider visual: padrao configurado da instancia;
- chunk target tokens;
- chunk overlap;
- politica de contexto padrao.

Controles de analytics:

- salvar historico de perguntas/respostas: nunca, metadados, completo local;
- retencao;
- exportar;
- limpar.

Controles de acesso:

- usuario atual;
- role;
- workspace padrao;
- membros em modo somente leitura no MVP.

Controles de API:

- status da API local;
- base URL;
- exemplos de chamadas;
- chaves de API ficam para fase planejada.

## API publica do Spring Boot

Prefixo:

```text
/api/v1
```

### Auth e bootstrap

```text
POST /api/v1/bootstrap
POST /api/v1/auth/login
POST /api/v1/auth/logout
GET  /api/v1/me
```

### Workspaces

```text
GET   /api/v1/workspaces
GET   /api/v1/workspaces/{workspaceId}
PATCH /api/v1/workspaces/{workspaceId}
GET   /api/v1/workspaces/{workspaceId}/members
```

### Colecoes

```text
GET    /api/v1/workspaces/{workspaceId}/collections
POST   /api/v1/workspaces/{workspaceId}/collections
GET    /api/v1/collections/{collectionId}
PATCH  /api/v1/collections/{collectionId}
DELETE /api/v1/collections/{collectionId}
```

Payload de criacao:

```json
{
  "name": "Arquitetura RAG",
  "description": "Documentos de estudo",
  "purpose": "study",
  "defaultIngestionProfile": "balanced",
  "defaultContextPolicy": "sequential"
}
```

### Documentos

```text
GET    /api/v1/collections/{collectionId}/documents
POST   /api/v1/collections/{collectionId}/documents
GET    /api/v1/documents/{documentId}
DELETE /api/v1/documents/{documentId}
GET    /api/v1/documents/{documentId}/file
```

Upload:

```text
POST /api/v1/collections/{collectionId}/documents
Content-Type: multipart/form-data

file: PDF
```

### Ingestao

```text
POST /api/v1/documents/{documentId}/ingest-runs
GET  /api/v1/ingest-runs/{runId}
POST /api/v1/ingest-runs/{runId}/cancel
POST /api/v1/ingest-runs/{runId}/retry
GET  /api/v1/collections/{collectionId}/ingest-runs
```

Payload de inicio:

```json
{
  "profile": "balanced",
  "ocrMode": "auto",
  "ocrLanguage": "pt-BR",
  "reviewPageMapBeforeIndexing": true,
  "chunking": {
    "strategy": "semantic_block",
    "targetTokens": 700,
    "overlapTokens": 80
  },
  "contextPolicy": "sequential"
}
```

### Paginas e mapa de paginas

```text
GET   /api/v1/documents/{documentId}/pages
GET   /api/v1/documents/{documentId}/pages/{pageId}
PATCH /api/v1/documents/{documentId}/pages/{pageId}
POST  /api/v1/documents/{documentId}/pages/bulk-update
POST  /api/v1/documents/{documentId}/page-numbering/anchors
POST  /api/v1/documents/{documentId}/page-numbering/recalculate
```

Payload de anchor:

```json
{
  "filePageNumber": 14,
  "printedLabel": "2",
  "numberingStyle": "arabic",
  "applyDirection": "both"
}
```

Payload de pagina:

```json
{
  "pageRole": "body",
  "includeInSearch": true,
  "includeInNumbering": true,
  "forceOcr": false,
  "effectivePrintedLabel": "2"
}
```

### Chunks

```text
GET /api/v1/collections/{collectionId}/chunks
GET /api/v1/chunks/{chunkId}
GET /api/v1/chunks/{chunkId}/neighbors
```

Parametros:

```text
documentId
filePageNumber
printedLabel
embeddingStatus
q
before
after
```

### RAG e laboratorio

```text
POST /api/v1/collections/{collectionId}/search
POST /api/v1/collections/{collectionId}/context/assemble
POST /api/v1/collections/{collectionId}/ask
POST /api/v1/retrieval-feedback
```

Payload de busca:

```json
{
  "query": "Como o documento define pagina impressa?",
  "topK": 12,
  "contextPolicy": "sequential",
  "neighborBefore": 2,
  "neighborAfter": 2,
  "tokenBudget": 6000
}
```

Resposta esperada de `ask`:

```json
{
  "answer": "Texto da resposta.",
  "citations": [
    {
      "documentId": "doc_123",
      "chunkId": "chk_123",
      "sourceLabel": "PDF p. 14 / impresso p. 2",
      "filePageNumber": 14,
      "printedLabel": "2"
    }
  ],
  "retrievedChunks": [],
  "expandedContext": [],
  "metrics": {
    "latencyMs": 1200,
    "retrievedChunks": 12,
    "contextChunks": 8,
    "tokenEstimate": 4300
  }
}
```

### Analytics local

```text
GET    /api/v1/workspaces/{workspaceId}/analytics/summary
GET    /api/v1/collections/{collectionId}/analytics/summary
GET    /api/v1/analytics/events
POST   /api/v1/analytics/events
PATCH  /api/v1/workspaces/{workspaceId}/analytics/settings
DELETE /api/v1/workspaces/{workspaceId}/analytics/events
```

Observacao:

`POST /analytics/events` e usado pela propria aplicacao para registrar eventos locais. Deve validar schema rigidamente.

### Configuracoes

```text
GET   /api/v1/workspaces/{workspaceId}/settings
PATCH /api/v1/workspaces/{workspaceId}/settings
```

## API interna Spring -> Python worker

Prefixo sugerido:

```text
/worker/v1
```

Endpoints internos:

```text
POST /worker/v1/pdf/inspect
POST /worker/v1/pdf/extract-text
POST /worker/v1/pdf/ocr
POST /worker/v1/pdf/render-page
POST /worker/v1/pdf/extract-elements
POST /worker/v1/pdf/interpret-visual
POST /worker/v1/chunks/build
POST /worker/v1/embeddings/text
```

Esses endpoints nao devem ser expostos publicamente.

### pdf/inspect

Entrada:

```json
{
  "documentId": "doc_123",
  "storageUri": "storage://documents/doc_123/original.pdf"
}
```

Saida:

```json
{
  "pageCount": 42,
  "encrypted": false,
  "metadata": {
    "title": "Documento"
  }
}
```

### pdf/extract-text

Saida conceitual:

```json
{
  "pages": [
    {
      "filePageNumber": 1,
      "textBlocks": [],
      "quality": {
        "hasNativeText": true,
        "textDensity": "medium"
      }
    }
  ]
}
```

### pdf/ocr

Entrada:

```json
{
  "documentId": "doc_123",
  "pages": [1, 2, 3],
  "language": "pt-BR"
}
```

### chunks/build

Entrada:

```json
{
  "documentId": "doc_123",
  "strategy": "semantic_block",
  "targetTokens": 700,
  "overlapTokens": 80
}
```

### pdf/extract-elements

Saida conceitual:

```json
{
  "elements": [
    {
      "elementType": "table",
      "filePageNumber": 3,
      "bbox": [72, 144, 520, 360],
      "readingOrder": 18,
      "text": "| Coluna A | Coluna B |",
      "confidence": 0.82,
      "extractionMethod": "table_detector_v1"
    }
  ],
  "assets": []
}
```

### pdf/interpret-visual

Entrada:

```json
{
  "documentId": "doc_123",
  "assetId": "asset_123",
  "purpose": "describe_for_rag",
  "language": "pt-BR",
  "promptVersion": "visual_interpretation_v1"
}
```

Saida conceitual:

```json
{
  "interpretationText": "A figura mostra um fluxo com tres etapas...",
  "structuredText": {
    "kind": "figure_description",
    "containsText": true,
    "detectedText": ["Etapa 1", "Etapa 2"]
  },
  "provider": "mock",
  "model": "mock-vision",
  "promptVersion": "visual_interpretation_v1",
  "confidence": 0.74
}
```

### embeddings/text

Entrada:

```json
{
  "model": "default",
  "items": [
    {
      "chunkId": "chk_123",
      "content": "Texto do chunk"
    }
  ]
}
```

## Estados principais

### Document status

```text
uploaded
analyzing
needs_page_review
ready_to_index
indexing
ready
failed
archived
```

### Ingest run status

```text
queued
running
completed
failed
cancelled
```

### Embedding status

```text
pending
embedded
failed
stale
```

### Page role

```text
cover
toc
preface
body
appendix
blank
separator
unknown
```

### Context policy

```text
conservative
sequential
```

### Ingestion profile

```text
fast
balanced
```

## Criterio de fechamento do MVP

O MVP esta funcional quando um usuario consegue:

1. Criar ambiente local.
2. Criar uma colecao.
3. Enviar um PDF.
4. Revisar/corrigir mapa de paginas.
5. Rodar ingestao.
6. Preservar e interpretar imagens/tabelas relevantes do PDF.
7. Gerar chunks e embeddings.
8. Perguntar no laboratorio.
9. Ver resposta com citacoes.
10. Abrir fonte original da citacao.
11. Registrar feedback local.
12. Ver analytics local basico.
