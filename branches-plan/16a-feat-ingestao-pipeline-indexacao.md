# feat/ingestao-pipeline-indexacao

Status: candidata.

## Objetivo

Orquestrar a ingestao completa ate chunks, embeddings e indexacao Qdrant, usando as etapas ja implementadas.

## Tags

CODE, RUNTIME, DATA, CONTRACT, OBS, TEST, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [feat/qdrant-indexacao](16-feat-qdrant-indexacao.md).
- Contratos/fixtures: [ingestao](../tests/contracts/ingestion), [eventos](../tests/contracts/events), [worker](../tests/contracts/worker), [Qdrant](../tests/contracts/qdrant).

## Escopo

- Encadear inspect, revisao de paginas, extracao/OCR, chunking, embeddings e indexacao.
- Atualizar `ingest_run` por etapa.
- Aplicar retry/cancelamento definidos.
- Marcar documento como `ready`, `failed` ou estado intermediario correto.
- Suportar reprocessamento e reindexacao inicial.
- Garantir idempotencia contra duplicacao de chunks/pontos.

## Fora de escopo

- Fila distribuida.
- Paralelismo sofisticado.
- Otimizacoes de custo.

## Definido

- REST interno e suficiente no MVP.
- Qdrant e derivado e reindexavel.
- Run deve ser auditavel.

## Falta definir

- Tamanho de lote para embeddings/indexacao.
- Politica de retry automatica vs manual.
- Ordem exata de limpeza em reprocessamento.

## Estrategia

1. Implementar orquestrador de caso de uso no backend.
2. Integrar contratos do worker.
3. Integrar chunking, embeddings e Qdrant.
4. Atualizar status/logs/eventos.
5. Testar fluxo feliz e falhas por etapa.

## Testabilidade

- Fluxo completo com PDF fixture.
- Falha em etapa intermediaria deixa run diagnosticavel.
- Retry nao duplica chunks/pontos.
- Cancelamento respeita estado.
- Workspace scope preservado.

## Fechamento

- Documento passa de upload a indexado em Qdrant com proveniencia e status claros.
