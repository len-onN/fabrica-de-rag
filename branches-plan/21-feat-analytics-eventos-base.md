# feat/analytics-eventos-base

Status: candidata.

## Objetivo

Criar taxonomia e armazenamento local de eventos.

## Tags

CODE, DATA, CONTRACT, TEST, OBS, SEC

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [Analytics local de RAG](../docs/analytics-local-rag.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md).
- Contratos/fixtures: [eventos](../tests/contracts/events), [analytics](../tests/contracts/analytics).

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
- Implementar a taxonomia aprovada em `docs/analytics-observabilidade-mvp.md`.
- Envelope canonico: `analytics.event.v1`.
- Eventos carregam `eventName`, `origin`, `retentionClass`, `workspaceId`, ator, correlation id, recurso e `properties`.
- Retencao inicial por classe: analytics 90 dias, historico 30 dias, run log 30 dias e auditoria minima 365 dias.
- Export/delete seguem os contratos `analytics.export.manifest.v1` e `analytics.retention-policy.v1`.

## Falta definir

- Migrations e indices concretos.
- Estrategia de job local de retencao.
- Forma inicial de endpoint interno para escrita de eventos por fluxo.

## Estrategia

1. Implementar contrato de evento definido em `docs/analytics-observabilidade-mvp.md`.
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
