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

## Falta definir

- Budget inicial.
- Politica conservadora vs sequencial.
- Formato final do pacote de contexto.
- Politica de budget para elementos visuais/tabelas relacionados.

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

## Fechamento

- Sistema monta contexto previsivel.
