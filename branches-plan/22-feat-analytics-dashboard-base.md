# feat/analytics-dashboard-base

Status: candidata.

## Objetivo

Criar dashboard local minimo de qualidade e falhas.

## Tags

CODE, UX, DATA, CONTRACT, TEST, OBS

## Escopo

- Agregar falhas, latencia, runs, consultas e feedback.
- Criar UI com filtros basicos.
- Criar estado vazio.
- Consumir dados locais do workspace.
- Exibir eventos e metricas definidos em `docs/analytics-observabilidade-mvp`.
- Respeitar retencao e configuracao do workspace.

## Fora de escopo

- Ciencia de dados avancada.
- Telemetria remota.
- Recomendacoes automaticas.

## Definido

- Dashboard operacional, nao decorativo.
- Dados locais e escopados por workspace.
- Dashboard consome agregacoes do contrato `analytics.dashboard.summary.v1`.
- Indicadores iniciais: runs/falhas/latencia, busca/contexto/resposta, feedback, visual/tabelas, API e MCP.
- Filtros iniciais: workspace, intervalo, colecao, documento, origem e tipo de evento.

## Falta definir

- Componentes visuais concretos e layout final da tela.
- Estrategia de cache/agregacao se fixtures reais mostrarem custo.

## Estrategia

1. Criar endpoints de agregacao seguindo `docs/analytics-observabilidade-mvp.md`.
2. Criar componentes de dashboard.
3. Testar fixtures com dados.
4. Testar estado vazio.

## Testabilidade

- Agregacao com fixture.
- UI estado vazio e com dados.
- Filtros por colecao/documento/origem.

## Fechamento

- Usuario enxerga qualidade e falhas do RAG.
