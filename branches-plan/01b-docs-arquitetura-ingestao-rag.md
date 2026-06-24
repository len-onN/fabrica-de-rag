# docs/arquitetura-ingestao-rag

Status: candidata.

## Objetivo

Fechar a arquitetura de ingestao e indexacao do MVP antes de implementar runs, worker, chunking, embeddings e Qdrant.

## Tags

DOC, RUNTIME, DATA, CONTRACT, OBS, PERF, TEST

## Escopo

- Definir maquina de estados de documento e ingest run.
- Definir etapas: upload, inspect, page review, extract, OCR, chunking, embedding, indexacao e finalizacao.
- Definir quando a ingestao pausa para revisao do mapa de paginas.
- Definir retry, cancelamento, erro estruturado e logs de etapa.
- Definir idempotencia para reprocessar/reindexar.
- Definir layout de storage local.
- Definir quando REST interno deixa de ser suficiente e quando fila entraria.

## Fora de escopo

- Implementar fila.
- Implementar OCR real.
- Criar codigo de pipeline.

## Definido

- Upload nao executa processamento pesado sincrono.
- REST interno Spring -> worker e suficiente no MVP.
- Jobs longos precisam de status observavel.
- Qdrant e derivado e pode ser reconstruido.

## Falta definir

- Estados finais e transicoes permitidas.
- Semantica de retry por etapa.
- Semantica de cancelamento.
- Onde ficam logs de run e erros por pagina.
- Como reindexar sem duplicar chunks/pontos.

## Estrategia

1. Desenhar state machine de `document_status` e `ingest_run_status`.
2. Definir contrato de etapa e erro.
3. Definir storage URI e layout de artefatos.
4. Definir idempotencia/retry/reindexacao.
5. Atualizar ADRs e padroes.

## Testabilidade

- Tabela de transicoes valida.
- Casos de falha por etapa listados.
- Reprocessamento e reindexacao tem comportamento esperado.
- Smoke/e2e sabem quais estados esperar.

## Fechamento

- Branches de ingestao implementam fluxo definido, nao uma orquestracao improvisada.
