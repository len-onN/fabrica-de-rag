# feat/context-builder-base

Status: candidata.

## Objetivo

Montar contexto expandido por vizinhos, com budget e citacoes.

## Tags

CODE, CONTRACT, TEST, PERF

## Escopo

- Receber chunks ancoras.
- Expandir before/after conforme politica.
- Aplicar budget.
- Deduplicar.
- Preservar citacoes.

## Fora de escopo

- Expansao por secao complexa.
- Relacoes de tabela/imagem.
- Reranking.

## Definido

- Context builder separado da busca.
- Contexto final e diferente de chunk recuperavel.

## Falta definir

- Budget inicial.
- Politica conservadora vs sequencial.
- Formato final do pacote de contexto.

## Estrategia

1. Especificar algoritmo.
2. Criar contrato.
3. Implementar expansao por vizinhos.
4. Testar budget e ordem.

## Testabilidade

- Budget pequeno.
- Chunk duplicado.
- Vizinho ausente.
- Ordem final.

## Fechamento

- Sistema monta contexto previsivel.

