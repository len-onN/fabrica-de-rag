# feat/ingestao-runs-operacao

Status: candidata.

## Objetivo

Criar a operacao observavel de runs de ingestao: status, etapas, cancelamento, retry e logs.

## Tags

CODE, DATA, CONTRACT, UX, OBS, TEST

## Escopo

- Criar rota `/w/:workspaceId/ingest-runs/:runId`.
- Exibir status e progresso por etapa.
- Exibir parametros usados, duracao, erros e versao do worker quando existir.
- Implementar endpoints de cancelamento e retry.
- Criar logs estruturados de run.
- Registrar eventos basicos de run.

## Fora de escopo

- Pipeline completo de extracao/chunking/indexacao.
- Fila.
- OCR real.

## Definido

- Jobs longos precisam de status observavel.
- Upload cria run inicial.
- Etapas futuras devem encaixar no mesmo contrato de run.

## Falta definir

- Transicoes finais da state machine.
- Semantica de retry por etapa.
- Semantica de cancelamento enquanto uma etapa esta executando.

## Estrategia

1. Aplicar state machine definida em `docs/arquitetura-ingestao-rag`.
2. Criar endpoints de run.
3. Criar tela de status.
4. Criar contrato de logs.
5. Testar transicoes principais.

## Testabilidade

- Run muda de status corretamente.
- Cancel/retry respeitam transicoes permitidas.
- Logs aparecem sem vazar conteudo sensivel.
- Eventos sao escopados por workspace.

## Fechamento

- Usuario consegue acompanhar e operar runs mesmo antes do pipeline completo.
