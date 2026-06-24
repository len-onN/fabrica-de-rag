# feat/workspace-dashboard-minimo

Status: candidata.

## Objetivo

Criar dashboard inicial do workspace com dados reais e estados vazios.

## Tags

CODE, UX, CONTRACT, TEST, SEC

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

