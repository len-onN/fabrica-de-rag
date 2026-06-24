# feat/analytics-eventos-base

Status: candidata.

## Objetivo

Criar taxonomia e armazenamento local de eventos.

## Tags

CODE, DATA, CONTRACT, TEST, OBS, SEC

## Escopo

- Definir schema versionado de evento.
- Registrar eventos de ingestao e laboratorio.
- Criar retencao inicial.
- Criar endpoints de export/delete quando definidos no planejamento.
- Evitar conteudo sensivel por padrao.

## Fora de escopo

- Telemetria remota.
- Dashboards sofisticados.
- Ciencia de dados avancada.

## Definido

- Analytics e local.
- Eventos devem ser escopados por workspace.
- Sem telemetria remota no MVP.

## Falta definir

- Taxonomia inicial final.
- Retencao padrao.
- Campos que nunca podem conter conteudo sensivel.
- Formato final JSON/CSV de export.

## Estrategia

1. Definir contrato de evento.
2. Criar persistencia.
3. Implementar retencao, export/delete quando aplicavel.
4. Emitir eventos nos fluxos ja existentes.
5. Testar schema e workspace scope.

## Testabilidade

- Schema valido.
- Evento gravado.
- Workspace scope.
- Export/delete respeitam escopo.

## Fechamento

- Fluxos principais emitem eventos locais.
