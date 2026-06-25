# docs/analytics-observabilidade-mvp

Status: em fechamento.

## Objetivo

Fechar a taxonomia de analytics local, observabilidade e retencao antes das branches que registrarem eventos, jobs, logs ou dashboards.

## Tags

DOC, OBS, DATA, CONTRACT, SEC, TEST

## Escopo

- Definir eventos versionados do MVP.
- Definir origens: UI, API, worker, MCP.
- Definir campos proibidos por privacidade.
- Definir retencao e configuracao local.
- Definir exportacao/delete de analytics local.
- Definir logs de ingestao e erros por pagina.
- Definir metricas basicas de latencia, tokens, chunks e etapas.

## Fora de escopo

- Telemetria remota.
- Ciencia de dados avancada.
- Coortes e recomendacoes automaticas.

## Definido

- Analytics e local.
- Sem telemetria remota no MVP.
- Eventos devem ser escopados por workspace.
- Conteudo sensivel nao deve ser registrado por padrao.
- Envelope canonico `analytics.event.v1` com `eventName` em `snake_case`, `origin`, `retentionClass`, `workspaceId`, ator, correlation id, recurso e propriedades seguras.
- Origens canonicas: `ui`, `api`, `worker`, `mcp` e `system`; eventos MCP so sao emitidos quando `feat/mcp-tools-base` existir.
- Trilhas separadas: analytics event, historico local opcional, run log, auditoria minima e log tecnico.
- Retencao padrao: analytics 90 dias, historico 30 dias, run log 30 dias e auditoria minima 365 dias.
- Exportacao usa JSONL/CSV com manifesto `analytics.export.manifest.v1`.
- Dashboard MVP usa contrato `analytics.dashboard.summary.v1`.
- Fixtures foram adicionadas em `tests/contracts/events` e `tests/contracts/analytics`.

## Falta definir

- Migrations, endpoints e agregadores concretos, a fechar em `feat/analytics-eventos-base` e `feat/analytics-dashboard-base`.
- UI real de configuracao de historico/retencao, a fechar em `feat/workspace-settings-mvp`.
- Propriedades finais de eventos visuais/tabelas, a fechar em `docs/interpretacao-imagens-tabelas-pdf`.
- Eventos MCP reais, a fechar em `feat/mcp-tools-base`.
- Rate limiting e eventos finais de API key, a fechar em `feat/api-rag-publica`.

## Detalhes que devem ficar explicitos

- Event envelope: `eventVersion`, `eventName`, `origin`, `retentionClass`, `occurredAt`, `workspaceId`, ator, correlation id, recurso e `properties`.
- Lista de eventos do MVP por fluxo: bootstrap, workspace, colecao, ingestao, pagina, visual/tables, chunking, embedding, indexacao, busca, contexto, resposta, feedback, API e MCP; MCP deve ter schema preparado, mas so emitira eventos quando `feat/mcp-tools-base` entrar.
- Campos proibidos: conteudo integral de documento, embedding, prompt completo, resposta completa quando historico estiver desligado, segredo, chave, token e credencial.
- Retencao: classes `analytics`, `history`, `run_log` e `audit_minimum`, configuracao por workspace, limpeza por periodo e comportamento em export/delete.
- Export: JSONL/CSV com manifesto, escopo por workspace, filtros, contagens e mascaramento de campos sensiveis.
- Logs de job vs eventos: logs diagnosticam run/step; eventos alimentam analytics/agregacao; auditoria registra acoes sensiveis.
- Dashboard minimo: runs, falhas por etapa, latencia, consultas, chunks recuperados, contexto, resposta, feedback, visual/tabelas e limites MCP.
- Observabilidade tecnica: correlation id entre UI/API/worker/MCP, erro seguro para usuario e detalhe tecnico em log controlado.
- Pontos OCP/LSP/IoC: novo evento por schema versionado, novo agregador, novo painel, nova origem e futura telemetria remota opt-in sem alterar evento local base; sinks/agregadores devem ser substituiveis sem vazar campos proibidos ou misturar workspaces.

## Estrategia

1. Listar eventos por fluxo do MVP.
2. Definir schema base e exemplos.
3. Definir configuracoes de retencao.
4. Definir dashboard minimo e agregacoes.
5. Registrar testes de schema, privacidade e workspace scope.

## Testabilidade

- Cada evento tem schema.
- Eventos de workspaces diferentes nao se misturam.
- Delete/export respeitam escopo e retencao.
- Dashboard funciona com fixtures.

## Fechamento

- Branches de analytics e observabilidade implementam uma taxonomia ja aprovada.
