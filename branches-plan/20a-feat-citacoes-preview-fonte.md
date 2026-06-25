# feat/citacoes-preview-fonte

Status: candidata.

## Objetivo

Permitir abrir a fonte original de uma citacao com preview de pagina, trecho, pagina fisica e pagina impressa.

## Tags

CODE, UX, CONTRACT, DATA, TEST

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [MVP interface e API](../docs/mvp-interface-e-api.md), [Design visual](../docs/design-visual.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [feat/worker-pdf-render-ocr-base](12a-feat-worker-pdf-render-ocr-base.md).
- Contratos/fixtures: [citacoes](../tests/contracts/rag/citation.v1.json), [citacao visual](../tests/contracts/rag/citation-visual.v1.json), [PDF fixtures](../tests/fixtures/pdfs).

## Escopo

- Criar componente reutilizavel de citacao.
- Abrir pagina/preview a partir de chunk ou resposta.
- Mostrar documento, pagina PDF, pagina impressa, trecho e contexto minimo.
- Mostrar bbox/elemento quando a citacao vier de imagem, tabela ou interpretacao visual.
- Integrar laboratorio, resposta RAG e navegador de chunks.
- Tratar fonte ausente, pagina ignorada ou artefato de preview indisponivel.

## Fora de escopo

- Crops granulares por bbox.
- Viewer PDF completo.

## Definido

- Citacoes precisam preservar proveniencia.
- Preview de pagina vem do worker/render ou fallback textual.
- Elementos de imagem/tabela usam source locator da camada visual quando existir.
- Abrir fonte original e criterio de fechamento do MVP.
- Contrato inicial definido em `docs/algoritmos-rag-mvp.md` como `rag.citation.v1`.
- `sourceLabel` usa `PDF p. X / impresso p. Y` quando pagina impressa existir; caso contrario usa `PDF p. X`.
- Bbox/source locator visual definidos em `docs/interpretacao-imagens-tabelas-pdf.md` como `pdf.source_locator.v1`.
- Fixture `tests/contracts/rag/citation-visual.v1.json` cobre citacao de interpretacao visual.

## Falta definir

- UX de modal vs painel lateral.
- Fallback quando render nao existir.
- Como destacar bbox sem exigir crop granular.

## Estrategia

1. Definir contrato de origem/citacao.
2. Criar endpoint ou compor dados de chunk/document/page.
3. Criar componente de preview.
4. Integrar laboratorio, resposta e chunks.
5. Testar casos sem render/sem pagina impressa.

## Testabilidade

- Citacao abre fonte correta.
- Workspace errado nao acessa fonte.
- Pagina fisica e impressa aparecem quando existem.
- Citacao de tabela/imagem mostra elemento/source locator correto.
- Fallback textual funciona sem render.
- Highlight de bbox usa coordenadas `pdf_points_top_left` normalizadas.

## Fechamento

- Usuario consegue validar a evidencia por tras de uma resposta.
