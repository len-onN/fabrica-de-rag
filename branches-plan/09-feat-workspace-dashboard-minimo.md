# feat/workspace-dashboard-minimo

Status: candidata.

## Objetivo

Criar dashboard inicial do workspace com dados reais e estados vazios.

## Tags

CODE, UX, CONTRACT, TEST, SEC

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [MVP interface e API](../docs/mvp-interface-e-api.md), [Design visual](../docs/design-visual.md), [Usuarios e acesso](../docs/usuarios-e-acesso.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [feat/auth-bootstrap-workspaces](08-feat-auth-bootstrap-workspaces.md).
- Contratos/fixtures: [REST](../tests/contracts/rest), [eventos](../tests/contracts/events).

## Escopo

- Criar rota de dashboard.
- Mostrar workspace ativo.
- Mostrar estado vazio de colecoes/documentos/jobs.
- Consumir API real.
- Preparar navegacao para proximas features.

## Fora de escopo

- Analytics completo.
- Gestao avancada de workspace.
- Graficos sofisticados.

## Definido

- Dashboard e ferramenta operacional, nao landing page.
- Dados sempre escopados por workspace.

## Falta definir

- Conteudo exato dos cards/paineis iniciais.
- Se havera sidebar persistente ja nesta branch.

## Estrategia

1. Criar endpoint resumo.
2. Criar service/client no Angular.
3. Criar tela com estado vazio.
4. Testar permissao e render.

## Testabilidade

- Teste API de resumo.
- Teste componente para estado vazio.
- Teste de workspace errado quando aplicavel.

## Fechamento

- Usuario entra no workspace e ve uma tela inicial real.
