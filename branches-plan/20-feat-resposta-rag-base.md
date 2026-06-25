# feat/resposta-rag-base

Status: candidata.

## Objetivo

Gerar resposta RAG com grounding e citacoes, usando provider real ou adapter mockado definido no planejamento.

## Tags

CODE, CONTRACT, TEST, OBS, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [MVP interface e API](../docs/mvp-interface-e-api.md), [feat/context-builder-base](18-feat-context-builder-base.md), [feat/laboratorio-recuperacao](19-feat-laboratorio-recuperacao.md).
- Contratos/fixtures: [resposta RAG](../tests/contracts/rag/ask-response-insufficient-evidence.v1.json), [citacoes](../tests/contracts/rag/citation.v1.json), [eventos](../tests/contracts/events).

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
