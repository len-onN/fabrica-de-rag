# Fontes e Integracoes Futuras

## Visao

O MVP da Fabrica de RAG deve focar apenas em PDF. Esse foco reduz debito tecnico e permite aprofundar o que realmente diferencia o produto: pagina fisica, pagina impressa, bbox, imagens, OCR, chunks, contexto, citacoes e analytics local.

Mesmo assim, a arquitetura deve prever expansao natural para novas fontes. O objetivo e nao implementar conectores agora, mas evitar que o modelo fique preso exclusivamente a pagina PDF.

## Decisao de escopo

MVP:

- upload de PDF;
- ingestao de PDF;
- tratamento de paginas, numeracao, elementos, imagens internas ao PDF e chunks;
- citacoes baseadas no PDF enviado.

Ficam fora do MVP: embeddings visuais, busca multimodal por imagem e conectores de novas fontes. Interpretacao textual derivada de imagens/tabelas em PDFs entra por camada propria.

Futuro:

- DOCX;
- EPUB;
- MOBI;
- Markdown/HTML/TXT;
- pastas locais;
- Notion;
- Jira;
- Confluence;
- GitHub/GitLab;
- Google Drive/Docs;
- websites e sitemaps;
- outras ferramentas com API.

## Regra sobre conversao para PDF

Se o usuario converter outro formato para PDF fora da aplicacao e enviar esse PDF, a aplicacao tratara o PDF como fonte canonica.

Isso significa:

- as citacoes apontam para o PDF enviado;
- a paginacao e a numeracao sao do PDF enviado;
- a aplicacao nao garante equivalencia com o arquivo original;
- perdas de estrutura causadas pela conversao sao responsabilidade do fluxo externo.

No futuro, conversao para PDF pode ser usada como apoio visual, mas nao como unica fonte semantica.

## Contrato comum futuro

Conceito:

```text
source_connector
-> source_item
-> source_element
-> asset
-> chunk
-> embedding
-> relation
```

### source_connector

Representa a origem:

```text
pdf_upload
local_folder
notion
jira
confluence
github
gitlab
google_drive
website
```

### source_item

Representa uma unidade importada:

```text
PDF: arquivo
Notion: pagina/database item
Jira: issue
Confluence: pagina
GitHub: arquivo/issue/PR/wiki
Website: pagina URL
```

### source_locator

Representa como voltar ao trecho original.

Exemplos:

```text
PDF
- file_page_number
- printed_page_label
- bbox

DOCX
- section_index
- heading_path
- paragraph_index

EPUB
- spine_index
- chapter_href
- fragment_id
- block_index

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
- commit_sha
- line_start
- line_end

Website
- url
- canonical_url
- heading_path
- content_hash
```

## Mapa de possibilidades

### DOCX

Valor:

- alto para documentos internos, artigos, notas e textos longos.

Desafios:

- paginacao nao e canonica;
- imagens, tabelas, comentarios, notas e estilos precisam de mapeamento;
- melhor preservar estrutura do Word em vez de converter tudo para PDF.

Prioridade futura:

- media/alta.

### EPUB

Valor:

- alto para livros, apostilas e material de estudo.

Desafios:

- formato refluivel, sem pagina fixa;
- citacao deve usar capitulo, href, fragmento ou bloco;
- imagens e notas exigem tratamento proprio.

Prioridade futura:

- media/alta.

### MOBI/AZW

Valor:

- medio.

Desafios:

- variacoes historicas;
- conversao geralmente via Calibre;
- possivel DRM;
- nao suportar quebra de DRM.

Prioridade futura:

- baixa/experimental.

### Notion

Valor:

- alto para bases pessoais e times.

Desafios:

- blocos aninhados;
- databases;
- permissoes externas;
- sincronizacao incremental;
- limites de API;
- conteudo mutavel.

Prioridade futura:

- alta, se o produto evoluir para conectores.

### Jira

Valor:

- alto para desenvolvimento, produto e times.

Desafios:

- issues, comentarios, anexos e historico;
- JQL/filtros;
- permissoes por projeto;
- dados sensiveis de organizacao;
- atualizacao incremental.

Prioridade futura:

- media/alta para uso em engenharia.

### Confluence

Valor:

- alto para documentacao corporativa.

Desafios:

- espacos, paginas, hierarquia e anexos;
- permissoes;
- conteudo HTML complexo;
- sincronizacao incremental.

Prioridade futura:

- media/alta.

### GitHub/GitLab

Valor:

- alto para desenvolvimento assistido por conhecimento.

Fontes possiveis:

- arquivos;
- README/docs;
- issues;
- pull requests;
- wikis;
- releases.

Desafios:

- branch/commit como localizador;
- codigo exige chunking proprio;
- permissoes de repositorio;
- secrets acidentais;
- diferenciar documentacao de codigo.

Prioridade futura:

- alta para agentes de desenvolvimento.

### Google Drive/Docs

Valor:

- alto para times e pesquisa.

Desafios:

- permissoes externas;
- exportacao de Docs;
- arquivos variados;
- sincronizacao incremental;
- privacidade.

Prioridade futura:

- media.

### Websites e sitemaps

Valor:

- alto para documentacao publica.

Desafios:

- robots/crawling responsavel;
- canonical URL;
- conteudo dinamico;
- deduplicacao;
- atualizacao;
- boilerplate.

Prioridade futura:

- media.

## Implicacoes para a arquitetura atual

Mesmo sem implementar conectores agora, o MVP deve evitar acoplamentos ruins:

- nao assumir que toda fonte tem pagina impressa;
- nao assumir que toda evidencia tem bbox;
- nao assumir que todo documento vem de upload;
- nao assumir que toda fonte e imutavel;
- nao guardar localizador apenas como colunas PDF;
- permitir `source_locator` estruturado;
- manter `source_type`;
- preservar `source_hash`/versao;
- separar assets, chunks e elementos da origem exata.

## Recomendacao

```text
Implementar PDF muito bem no MVP.
Criar modelo com source_type/source_locator flexivel.
Nao implementar conectores externos agora.
Mapear integracoes futuras para orientar o desenho.
```
