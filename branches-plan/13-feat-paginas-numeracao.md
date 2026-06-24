# feat/paginas-numeracao

Status: candidata.

## Objetivo

Implementar mapa de paginas e numeracao impressa por ancoras.

## Tags

CODE, DATA, CONTRACT, UX, TEST, PERF

## Escopo

- Criar tabelas de paginas.
- Registrar pagina fisica.
- Permitir ancora de pagina impressa.
- Inferir sequencia simples.
- Criar UI minima do mapa de paginas.
- Usar preview/render de pagina quando disponivel.
- Permitir incluir/excluir paginas do RAG e da numeracao.

## Fora de escopo

- Inferencia sofisticada multi-documento.
- OCR de numeracao.
- Curadoria visual avancada.

## Definido

- Pagina fisica e pagina impressa sao conceitos distintos.
- Citacoes devem carregar ambas quando aplicavel.

## Falta definir

- Modelo exato de segmento de numeracao.
- Casos-limite que entram nesta primeira branch.
- Como representar pagina sem numeracao.
- Fallback quando render de pagina nao estiver disponivel.

## Estrategia

1. Especificar algoritmo.
2. Criar schema.
3. Criar use case de ancora.
4. Criar inferencia simples.
5. Criar UI minima.

## Testabilidade

- Sequencia simples.
- Capa/prefacio.
- Pagina sem numeracao.
- Segmento quando incluido.

## Fechamento

- Citacoes podem usar pagina fisica/impressa e o mapa de paginas fica revisavel.
