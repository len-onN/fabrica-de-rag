# docs/vinculos-planos-branches

Status: em fechamento.

## Objetivo

Vincular os planos de branches futuras aos documentos, ADRs, contratos e fixtures relevantes para cada escopo.

## Tags

DOC, TEST, AGENT

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano de branches](../docs/plano-de-branches.md), [Escopos detalhados](../docs/escopos-detalhados-branches.md), [Branches plan](README.md).
- Fontes de decisao: [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md), [Skills e boas praticas para agentes](../docs/skills-e-boas-praticas-para-agentes.md).

## Escopo

- Criar regra de leitura de documentos relevantes para os planos futuros.
- Adicionar secao `Documentos relevantes` nas branches planejadas de implementacao, teste, performance e documentacao local.
- Apontar documentos de dominio, ADRs e contratos/fixtures quando a branch depender deles diretamente.
- Atualizar o registro e o diario de bordo.

## Fora de escopo

- Reabrir decisoes arquiteturais ja fechadas nos portoes `docs/*`.
- Reescrever escopo, estrategia ou criterio de fechamento das branches futuras.
- Criar novos contratos ou fixtures.
- Implementar codigo de produto.

## Definido

- A documentacao local continua como fonte primaria de decisao.
- Cada branch futura deve carregar os documentos transversais e os documentos especificos listados no proprio plano.
- Contratos e fixtures so entram na lista quando forem insumo direto para a branch.
- O vinculo deve orientar agentes futuros sem transformar cada plano em um documento longo.

## Falta definir

- Se scripts POSIX futuros vao precisar de uma convencao propria de links quando entrarem em CI/Linux.

## Estrategia

1. Registrar a branch documental na sequencia de `branches-plan`.
2. Definir regra compacta para documentos transversais e especificos.
3. Adicionar links por branch futura.
4. Conferir consistencia por busca textual.
5. Atualizar bordo e diario.

## Testabilidade

- Cada branch futura deve ter secao `Documentos relevantes`.
- Links devem apontar para arquivos ou pastas existentes.
- `git diff --check` deve passar.

## Fechamento

- Planos futuros ficam navegaveis por contexto antes de novas implementacoes.
- Proximo agente consegue abrir o plano da branch e saber quais documentos carregar.
