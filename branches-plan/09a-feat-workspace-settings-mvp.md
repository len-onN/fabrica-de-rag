# feat/workspace-settings-mvp

Status: candidata.

## Objetivo

Implementar configuracoes do workspace que afetam ingestao, analytics local, acesso e API local.

## Tags

CODE, UX, CONTRACT, DATA, SEC, TEST

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

## Falta definir

- Valores padrao finais.
- Permissao minima para alterar configuracoes.
- Formato dos exemplos de API local.

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
