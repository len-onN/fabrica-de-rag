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
