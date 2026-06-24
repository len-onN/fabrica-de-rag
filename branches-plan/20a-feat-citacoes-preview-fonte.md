# feat/citacoes-preview-fonte

Status: candidata.

## Objetivo

Permitir abrir a fonte original de uma citacao com preview de pagina, trecho, pagina fisica e pagina impressa.

## Tags

CODE, UX, CONTRACT, DATA, TEST

## Escopo

- Criar componente reutilizavel de citacao.
- Abrir pagina/preview a partir de chunk ou resposta.
- Mostrar documento, pagina PDF, pagina impressa, trecho e contexto minimo.
- Integrar laboratorio, resposta RAG e navegador de chunks.
- Tratar fonte ausente, pagina ignorada ou artefato de preview indisponivel.

## Fora de escopo

- Crops granulares por bbox.
- Evidencia visual por imagem/tabela.
- Viewer PDF completo.

## Definido

- Citacoes precisam preservar proveniencia.
- Preview de pagina vem do worker/render ou fallback textual.
- Abrir fonte original e criterio de fechamento do MVP.

## Falta definir

- Formato final do source label.
- UX de modal vs painel lateral.
- Fallback quando render nao existir.

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
- Fallback textual funciona sem render.

## Fechamento

- Usuario consegue validar a evidencia por tras de uma resposta.
