# feat/context-builder-base

Status: candidata.

## Objetivo

Montar contexto expandido por vizinhos, com budget e citacoes.

## Tags

CODE, CONTRACT, TEST, PERF

## Escopo

- Receber chunks ancoras.
- Expandir before/after conforme politica.
- Incluir relacoes diretas de tabela/imagem/legenda quando a ancora depender de elemento visual.
- Aplicar budget.
- Deduplicar.
- Preservar citacoes.

## Fora de escopo

- Expansao por secao complexa.
- Relacoes multi-hop ou inferencia complexa entre elementos.
- Reranking.

## Definido

- Context builder separado da busca.
- Contexto final e diferente de chunk recuperavel.
- Imagens/tabelas entram no contexto como texto derivado citavel, preservando source locator.
- Builder definido em `docs/algoritmos-rag-mvp.md` como `context_builder_v1`.
- Politicas iniciais: `conservative_neighbors_v1` e `sequential_neighbors_v1`.
- Budget default: laboratorio 6000 tokens, API 5000, MCP 4000.
- Tabelas tentam entrar como unidade; quando nao couberem, entram truncadas com flag e citacao preservada.
- Regras de elementos visuais/tabelas fechadas em `docs/interpretacao-imagens-tabelas-pdf.md`.

## Falta definir

- Ajuste fino de UI para exibir descartes e truncamentos.

## Estrategia

1. Especificar algoritmo.
2. Criar contrato.
3. Implementar expansao por vizinhos.
4. Incluir relacoes diretas de elementos visuais/tabelas.
5. Testar budget, citacoes e ordem.

## Testabilidade

- Budget pequeno.
- Chunk duplicado.
- Vizinho ausente.
- Elemento visual relacionado.
- Tabela preservada como unidade.
- Ordem final.
- Interpretacao visual marcada como dado derivado e citacao preservada mesmo com truncamento.

## Fechamento

- Sistema monta contexto previsivel.
