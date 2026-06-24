# docs/analytics-observabilidade-mvp

Status: candidata.

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

## Falta definir

- Taxonomia final dos eventos MVP.
- Retencao padrao.
- Campos permitidos/proibidos.
- Formato de export JSON/CSV.
- Relacao entre logs de job e eventos de analytics.
- Envelope versionado de evento, origem, correlation id e identidade.
- Politica de agregacao do dashboard minimo.
- Estrategia de delete/export por workspace sem misturar auditoria operacional obrigatoria.
- Limites de cardinalidade e payload para eventos de UI/API/worker/MCP.

## Detalhes que devem ficar explicitos

- Event envelope: `event_type`, `schema_version`, `occurred_at`, `workspace_id`, origem, ator, correlation id, recurso e payload.
- Lista de eventos do MVP por fluxo: bootstrap, workspace, colecao, ingestao, pagina, visual/tables, chunk, busca, contexto, resposta, feedback e API; MCP deve ter schema preparado, mas so emitira eventos quando `feat/mcp-tools-base` entrar.
- Campos proibidos: conteudo integral de documento, embedding, prompt completo, resposta completa quando historico estiver desligado, segredo, chave, token e credencial.
- Retencao: padrao local, configuracao por workspace, limpeza por periodo e comportamento em export/delete.
- Export: formato JSON/CSV, escopo por workspace, filtros e mascaramento de campos sensiveis.
- Logs de job vs eventos: logs diagnosticam run/step; eventos alimentam analytics/agregacao; auditoria registra acoes sensiveis.
- Dashboard minimo: runs, falhas por etapa, latencia, consultas, chunks recuperados, feedback e limites MCP.
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
