# Proposta Principal

## Visao

A Fabrica de RAG sera uma aplicacao web para transformar documentos e fontes de conhecimento em bases consultaveis por RAG, com controle sobre ingestao, chunking, vetorizacao, contexto, evidencias e publicacao.

A proposta nao e apenas criar embeddings. A ideia e oferecer uma bancada de engenharia de conhecimento onde o usuario consiga entender, corrigir, testar e servir uma base de conhecimento com confianca.

O norte do produto e unir pesquisa e geracao: usar fontes reais, referencias rastreaveis e o poder de uma LLM para estruturar conhecimento de multiplas formas. Isso deve empoderar estudo, pesquisa, escrita, tomada de decisao e desenvolvimento assistido por conhecimento proprio.

## Problema

RAGs falham com frequencia por motivos que nao aparecem quando olhamos apenas para os vetores:

- PDFs possuem layout complexo, tabelas, imagens, cabecalhos, rodapes e numeracao ambigua.
- A pagina fisica do PDF quase nunca coincide perfeitamente com a pagina impressa.
- Chunks podem quebrar sentido, misturar temas ou perder contexto necessario.
- Imagens, diagramas e tabelas podem conter informacao essencial.
- A busca vetorial encontra trechos candidatos, mas nem sempre monta o conjunto coerente de contexto.
- Agentes precisam de ferramentas seguras para investigar fontes sem acesso irrestrito ao banco.

## Objetivo

Criar uma plataforma que permita:

- Subir documentos, especialmente PDFs.
- Escolher perfis de ingestao.
- Inspecionar e ajustar paginas, elementos, imagens, tabelas e chunks.
- Criar colecoes vetoriais com metadados ricos.
- Testar perguntas e ver exatamente quais fontes foram usadas.
- Servir consultas via API.
- Expor ferramentas MCP para agentes locais ou remotos.
- Criar RAGs para estudo, pesquisa, escrita, desenvolvimento de software e agentes guiados por conhecimento.
- Oferecer analytics local para o usuario entender e melhorar seus proprios RAGs, consultas, ingestao e agentes.

No MVP, a fonte documental suportada sera PDF. Outras fontes e integracoes entram como expansao planejada, nao como escopo inicial.

## Ideia central

O sistema deve separar tres camadas:

```text
SQL documental
- documentos
- paginas
- elementos
- assets
- chunks
- relacoes
- runs de ingestao

Object storage
- PDFs originais
- renders de paginas
- imagens extraidas
- crops
- thumbnails

Vector DB
- embeddings textuais
- embeddings visuais
- payloads minimos para busca
```

O SQL e a fonte da verdade. O vector DB e um indice derivado.

## Fontes e integracoes futuras

Embora o MVP foque em PDF, a arquitetura deve prever uma expansao natural para outras fontes:

- DOCX, EPUB, MOBI, Markdown, HTML e TXT;
- pastas locais;
- Notion;
- Jira;
- Confluence;
- GitHub/GitLab;
- Google Drive/Docs;
- websites e sitemaps;
- exportacoes de ferramentas externas.

A regra e nao transformar todas as fontes em PDF como unica verdade. Conversao para PDF pode ser util como representacao visual, mas cada origem deve preservar seu proprio localizador:

```text
PDF
- pagina fisica
- pagina impressa
- bbox

Notion
- page_id
- block_id
- database_id

Jira
- issue_key
- comment_id
- attachment_id

Git
- repository
- path
- commit
- line_range

Website
- url
- canonical_url
- heading_path
- content_hash
```

O contrato comum deve ser "fonte -> itens -> elementos -> chunks -> relacoes -> embeddings", mantendo proveniencia e permissoes.

## Bancos vetoriais do usuario

A aplicacao podera evoluir para integrar bancos vetoriais do proprio usuario, sem abrir mao do contrato documental da Fabrica de RAG.

A direcao proposta e suportar conectores de vector store em niveis:

```text
Nivel 1
- exportar/sincronizar vetores para um banco externo

Nivel 2
- usar banco externo como backend principal da colecao, desde que a aplicacao crie ou valide o schema

Nivel 3
- importar colecao existente que siga o contrato de metadados da Fabrica

Nivel 4
- conectar colecao arbitraria em modo limitado/search-only
```

O principio continua o mesmo: o SQL documental preserva proveniencia, relacoes, paginas, chunks e assets. O banco vetorial, interno ou externo, e um indice de busca.

## Analytics local de RAG

A aplicacao deve incluir analytics local desde o MVP, com foco em ajudar o proprio usuario a avaliar qualidade de ingestao, recuperacao, contexto, respostas e uso por agentes.

O objetivo nao e rastrear pessoas nem coletar dados para um servico central. No MVP self-host/local, os eventos ficam na propria instalacao do usuario e funcionam como uma camada de auditoria e estudo sobre seus RAGs.

Eventos locais devem usar schemas rigidos, categorias e metadados operacionais:

```text
ingest_profile_selected
- profile
- page_count_bucket
- ocr_enabled

page_numbering_anchor_created
- numbering_style
- segment_count_bucket

retrieval_lab_query_executed
- context_policy
- reranker_enabled
- latency_bucket

mcp_tool_invoked
- tool_name
- success
- latency_bucket
```

Como os dados ficam locais, o usuario pode opcionalmente registrar perguntas, respostas, feedback e relevancia de chunks para sua propria avaliacao. Qualquer telemetria remota fica fora do MVP, como possibilidade futura opt-in, sem conteudo e com consentimento explicito.

## PDFs e imagens

Para PDFs, a aplicacao deve tratar texto e elementos visuais como partes relacionadas do mesmo documento.

Um diagrama, por exemplo, pode gerar:

- Um asset visual.
- Um elemento com bbox e pagina fisica.
- Um chunk de caption.
- Um chunk de OCR.
- Um embedding textual.
- Opcionalmente, um embedding visual.

Assim, quando uma resposta usar um chunk, o sistema consegue voltar ate a pagina, regiao visual e fonte original.

## Contexto e coesao

A busca vetorial deve recuperar ancoras. A resposta final deve ser montada por um context builder que expande essas ancoras com regras controladas:

- vizinhos antes/depois;
- secao completa;
- tabela relacionada;
- imagem ou legenda relacionada;
- referencias cruzadas;
- expansao multi-hop quando permitido.

O objetivo e reduzir a aleatoriedade estrutural. O agente pode investigar, mas as ferramentas devem impor trilhos: limites, ordenacao, deduplicacao, permissao e proveniencia.

## Experiencia do usuario

A aplicacao deve expor controles compreensiveis:

- perfil de ingestao;
- mapa de paginas;
- preview de extracao;
- controle de chunking;
- politica de contexto;
- laboratorio de recuperacao;
- curadoria manual de relacoes e exclusoes.

O usuario edita conhecimento, nao vetores.
