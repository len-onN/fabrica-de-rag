# feat/workspace-settings-mvp

Status: concluída.

## Objetivo

Implementar configuracoes do workspace que afetam ingestao, analytics local, acesso e API local.

## Tags

CODE, UX, CONTRACT, DATA, SEC, TEST

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [Usuarios e acesso](../docs/usuarios-e-acesso.md), [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [Design visual](../docs/design-visual.md).
- Contratos/fixtures: [analytics](../tests/contracts/analytics), [eventos](../tests/contracts/events), [REST](../tests/contracts/rest).

## Escopo

- Criar rota `/w/:workspaceId/settings`.
- Implementar tabs Geral, Ingestao, Analytics local, Acesso e API.
- Salvar finalidade e nome do workspace.
- Salvar defaults de ingestao: perfil, OCR, idioma, chunk target, overlap e politica de contexto.
- Salvar configuracoes de analytics: historico, retencao, export/delete.
- Exibir acesso atual e role em modo leitura no MVP.
- Exibir status/base URL da API local e exemplos.

## Fora de escopo

- Convites.
- Chaves de API completas.
- Provedores externos.
- Administracao MCP.

## Definido

- Workspace e fronteira de configuracao.
- Analytics local deve ser configuravel.
- Chaves de API ficam para fase planejada.
- Analytics usa modos `metadata_only`, `local_history` e `analytics_off`.
- Retencao configuravel por classe segue `docs/analytics-observabilidade-mvp.md`.
- Alteracao de settings gera `workspace_analytics_settings_updated`.

## Falta definir

- Formato dos exemplos de API local.
- Layout final dos controles de retencao e historico.

## Estrategia

1. Criar modelo/settings no backend.
2. Criar endpoints GET/PATCH.
3. Criar tela com tabs.
4. Aplicar defaults nos fluxos de ingestao e laboratorio.
5. Testar permissoes.

## Testabilidade

- Configuracoes persistem.
- Workspace errado nao acessa configuracao.
- Defaults aparecem no upload/laboratorio.
- Usuario sem permissao nao altera settings.

## Fechamento

- Workspace possui configuracoes MVP operaveis.
