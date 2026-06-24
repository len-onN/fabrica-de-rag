# docs/arquitetura-ingestao-rag

Status: candidata.

## Objetivo

Fechar a arquitetura de ingestao e indexacao do MVP antes de implementar runs, worker, chunking, embeddings e Qdrant.

## Tags

DOC, RUNTIME, DATA, CONTRACT, OBS, PERF, TEST

## Escopo

- Definir maquina de estados de documento e ingest run.
- Definir etapas: upload, inspect, page review, extract, OCR, visual/table interpretation, chunking, embedding, indexacao e finalizacao.
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
- Contrato de step de ingestao: entrada, saida, erro, timeout, idempotency key e artefatos gerados.
- Politica de pausa para revisao humana do mapa de paginas.
- Layout canonico de storage local para PDF original, renders, OCR, thumbnails, crops e artefatos intermediarios.
- Relacao entre estado transacional no SQL, chamada ao worker e atualizacao do indice Qdrant.
- Relacao entre extracao estrutural, interpretacao visual por adapter, assets, elementos e chunks derivados.

## Detalhes que devem ficar explicitos

- State machine de `document_status`, `ingest_run_status` e `ingest_step_status`, com transicoes permitidas e proibidas, incluindo etapa visual/tabelas quando habilitada.
- Modelo de retry manual/automatico por etapa, incluindo maximo de tentativas e quando uma etapa e considerada segura para repetir.
- Semantica de cancelamento antes, durante e depois de uma chamada ao worker.
- Modelo de erro estruturado: codigo, etapa, mensagem segura, detalhe tecnico interno, pagina afetada e correlation id.
- Idempotencia: chaves por run/step/documento, sobrescrita segura de artefatos e point ids Qdrant deterministicos.
- Reindexacao: quando apagar/recriar chunks, embeddings e pontos; como preservar historico de run sem duplicar resultado ativo.
- Storage URI: formato, ownership por workspace/documento/run, limpeza de artefatos temporarios e migracao futura para MinIO/S3.
- Observabilidade: diferenca entre log de run, evento de analytics e auditoria.
- Pontos OCP/LSP/IoC: nova etapa de pipeline, novo adapter de worker, nova politica de OCR, novo modo de storage e futura fila; cada implementacao deve preservar semantica de step, erro, timeout, idempotencia e cancelamento.

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
