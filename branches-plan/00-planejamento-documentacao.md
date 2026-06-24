# planejamento/documentacao

Status: em andamento.

## Objetivo

Consolidar documentacao inicial, estrategia operacional, politica de agentes/skills, plano de branches, plano tecnico e estrategia de testes.

## Tags

DOC, TEST, AGENT, RUNTIME

## Definido

- Fluxo `develop -> branch -> PR`.
- Registro de bordo e diario de bordo.
- Fundacao minima testavel antes de fatias verticais.
- Compose e2e canonico em `infra/compose/compose.e2e.yml`.
- Arquivos individuais por branch nesta pasta.
- `docs/plano-tecnico-mvp` incorporada nesta branch.
- Stack baseline inicial definida.
- Cobertura do MVP revisada contra o plano de branches.
- Portoes de planejamento adicionados antes do codigo de produto.
- Gate SOLID/OCP-LSP-IoC explicitado para evitar mudancas laterais e regressao retro-destrutiva.
- Cada branch deve declarar pontos de extensao, invariantes preservadas e contratos sensiveis.
- Cada adapter/provider/policy novo deve declarar substituibilidade, suite de contrato e ligacao por IoC/DI.
- Trigger de implementacao agentica criado para retomar ambiente, Git, branch, plano especifico, skills e gates antes do codigo.
- Revisao de coesao documental criada para consolidar decisoes vigentes e remover divergencias textuais.
- Camada de interpretacao de imagens/tabelas em PDFs adicionada como componente fundamental do MVP.

## Falta definir

- Nada estrutural pendente nesta branch; proximas decisoes tem branches donas.

## Testabilidade

- Revisao de links internos.
- Consistencia entre README, docs e `branches-plan`.
- Verificacao de que os portoes do grupo 1 deixam decisoes estruturais explicitas antes do codigo.
- Verificacao de que o README aponta para o trigger de implementacao.
- Verificacao de que README, planejamento, features, interface/API, plano de branches e `branches-plan` convergem na mesma versao vigente.
- Verificacao de que imagens/tabelas em PDFs possuem portao documental e branch funcional propria.

## Fechamento

- PR para `develop` com a camada de planejamento pronta para iniciar `docs/modelo-dados-contratos-mvp`.
