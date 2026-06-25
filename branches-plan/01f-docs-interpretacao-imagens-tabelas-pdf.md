# docs/interpretacao-imagens-tabelas-pdf

Status: em fechamento.

## Objetivo

Fechar a arquitetura da camada de interpretacao de imagens e tabelas em PDFs antes das branches que implementarem extracao de elementos visuais, tabelas, assets, chunks e contexto relacionado.

## Tags

DOC, DATA, CONTRACT, RUNTIME, PERF, TEST

## Escopo

- Definir modelo conceitual de `document_element`, `asset`, `table`, `figure` e relacoes com pagina/chunk.
- Definir contratos Spring -> worker para detectar imagens, tabelas e regioes visuais.
- Definir representacao textual inicial para tabelas e imagens internas ao PDF.
- Definir contrato de interpretador visual por LLM/VLM para gerar descricao textual estruturada de imagens, diagramas e tabelas quando habilitado.
- Definir quando uma tabela vira chunk proprio, quando vira metadado e quando preserva ordem de leitura.
- Definir como imagens/tabelas aparecem em citacoes, preview de fonte e context builder.
- Definir limites do MVP vs capacidades futuras com modelos multimodais.

## Fora de escopo

- Implementar codigo.
- Embeddings visuais.
- Curadoria manual profunda de regioes/crops.
- Busca multimodal por imagem.

## Definido

- A camada de interpretacao de imagens e tabelas em PDF e fundamental para o MVP.
- PDF continua sendo a unica fonte documental suportada no MVP.
- Embeddings visuais continuam fora do MVP.
- Interpretacao visual textual por LLM/VLM entra por adapter configuravel, com modo mockado/local para testes e opt-in quando houver provider remoto.
- O worker Python concentra processamento documental pesado.
- SQL continua sendo a fonte da verdade para elementos, assets, relacoes e proveniencia.
- `docs/interpretacao-imagens-tabelas-pdf.md` fecha `pdf.source_locator.v1`, `pdf.document_element.v1`, `pdf.asset.v1`, `pdf.table_json.v1`, `pdf.table_markdown.v1`, `pdf.visual_interpretation.v1` e os contratos `worker.pdf.extract_elements.*.v1` e `worker.pdf.interpret_visual.*.v1`.
- Bbox usa `pdf_points_top_left`, pagina fisica 1-based, pagina normalizada apos rotacao e source locator citavel.
- Tabelas preservam estrutura JSON minima e Markdown como fallback textual/chunk inicial.
- Provider remoto de visao fica desligado por padrao; mock deterministico e obrigatorio para testes, smoke e e2e.
- Cache de interpretacao visual e versionado por hash de asset/source locator/provider/model/prompt/policy e escopado por workspace.
- Budgets iniciais, erros estruturados, eventos seguros e fixtures JSON foram definidos.

## Falta definir

- Biblioteca concreta para extracao de tabelas/imagens no worker.
- Parametros reais de memoria/tempo por biblioteca.
- Prompt final de provider remoto quando um provider real for escolhido.
- UX final de destaque de bbox, modal/painel e fallback visual.
- Ajustes finos apos fixtures reais de PDF.

## Detalhes que devem ficar explicitos

- Tipos de elemento: paragraph, heading, table, figure, image, caption, footnote, header, footer, unknown.
- Campos obrigatorios: `workspace_id`, `document_id`, `page_id`, `element_type`, `bbox`, `reading_order`, `source_locator`, `confidence`, `extraction_method`.
- Assets: imagem/render/crop devem ter URI, hash, tipo, dimensoes, pagina e politica de limpeza/cache.
- Tabelas: guardar estrutura minima, texto renderizavel, relacao com linhas/celulas quando disponivel e fallback textual.
- Imagens: guardar bbox/asset, legenda extraida ou texto proximo, OCR interno quando houver texto na imagem e interpretacao textual por adapter visual quando habilitado.
- Interpretacoes visuais: armazenar como derivado versionado, nunca como fonte canonica; registrar provider, modelo, prompt version, input asset hash, idioma, confidence e created_at.
- Context builder: preservar tabelas como unidades atomicas e incluir legenda/imagem relacionada quando uma ancora depender dela.
- Citacoes: permitir abrir pagina, destacar bbox quando existir e mostrar fallback textual quando crop/render nao existir.
- Testes: fixtures com tabela simples, tabela quebrada em paginas, figura com legenda e imagem sem legenda.
- Pontos OCP/LSP/IoC: novos extratores de tabela/imagem e vision interpreters devem ser adapters substituiveis, preservando contrato de elemento, erro estruturado, confidence, source locator e semantica de privacidade/budget.

## Estrategia

1. Mapear elementos visuais/tabelas exigidos pelo MVP. Concluido em `docs/interpretacao-imagens-tabelas-pdf.md`.
2. Definir contratos e exemplos JSON em `tests/contracts`. Concluido com fixtures de worker, RAG e eventos.
3. Definir relacoes com paginas, assets, chunks e citacoes. Concluido por source locator, relations e regras de contexto.
4. Definir limites do MVP e backlog futuro multimodal. Concluido; embeddings visuais e busca multimodal seguem fora do MVP.
5. Atualizar ADRs, plano de branches e escopos funcionais. Concluido nesta branch.

## Testabilidade

- Cada tipo de elemento tem contrato e exemplo.
- Fixtures pequenas cobrem tabela, imagem com legenda, imagem sem legenda e ordem de leitura.
- O contrato preserva workspace scope e source locator.
- Tabelas nao sao quebradas como paragrafo comum sem criterio explicito.
- Citacao consegue voltar a pagina/bbox ou fallback textual.
- Eventos visuais nao carregam binario, prompt completo nem texto integral.
- Provider mockado permite testes sem rede e sem modelo real.

## Fechamento

- Branches de worker, modelo de dados, chunking, contexto e citacoes podem implementar imagens/tabelas sem improvisar contratos.
