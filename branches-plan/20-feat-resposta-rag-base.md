# feat/resposta-rag-base

Status: candidata.

## Objetivo

Gerar resposta RAG com grounding e citacoes, usando provider real ou adapter mockado definido no planejamento.

## Tags

CODE, CONTRACT, TEST, OBS, PERF

## Escopo

- Criar adapter de LLM ou mock.
- Criar prompt base.
- Gerar resposta com citacoes.
- Tratar ausencia de evidencia suficiente.
- Aplicar politica definida em `docs/algoritmos-rag-mvp`.

## Fora de escopo

- Avaliacao automatica avancada.
- Multi-model routing.
- Otimizacao de custo.

## Definido

- LLM nao substitui fonte.
- Testes usam mock por padrao.

## Falta definir

- Politica de resposta quando nao houver evidencia.

## Estrategia

1. Aplicar contrato definido no planejamento.
2. Criar adapter real ou mockado conforme decisao.
3. Integrar context builder.
4. Garantir citacoes obrigatorias.

## Testabilidade

- LLM mockado.
- Citacao obrigatoria.
- Sem evidencia.

## Fechamento

- Pergunta pode retornar resposta citada quando habilitada.
