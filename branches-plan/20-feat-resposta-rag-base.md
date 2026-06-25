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
- Politica definida em `docs/algoritmos-rag-mvp.md` como `grounded_answer_policy_v1`.
- Resposta afirmativa exige pelo menos uma citacao.
- Sem contexto confiavel retorna `status=insufficient_evidence`.

## Falta definir

- Provider real e prompt final, se forem habilitados nesta branch.

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
