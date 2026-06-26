# chore/frontend-angular-base

Status: candidata.

## Objetivo

Criar a base Angular executavel, testavel e alinhada ao design visual.

## Tags

CODE, UX, CONTRACT, TEST, RUNTIME

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Design visual](../docs/design-visual.md), [MVP interface e API](../docs/mvp-interface-e-api.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [chore/workspace-fundacao](02-chore-workspace-fundacao.md).
- Contratos/fixtures: [REST](../tests/contracts/rest).

## Escopo

- Criar app Angular em `apps/web`.
- Configurar roteamento inicial.
- Criar shell visual.
- Criar tokens de tema claro/escuro.
- Criar client HTTP base.
- Criar estrutura por features.

## Fora de escopo

- Telas finais de produto.
- Login real.
- Laboratorio RAG.

## Definido

- Angular moderno.
- Standalone components.
- Signals para estado local/derivado.
- UI densa e utilitaria, sem landing page.
- Versao Angular: 22 (requer Node 24).
- Biblioteca de UI: Custom + `@angular/cdk` + `lucide-angular`.
- Runner final de unit/component tests: Vitest (via Angular CLI).

## Falta definir

- Nada pendente para o escopo inicial.

## Estrategia

1. Criar projeto.
2. Aplicar estrutura feature-first.
3. Criar layout shell.
4. Criar tema inicial.
5. Criar teste de render basico.

## Testabilidade

- Build passa.
- Teste do shell passa.
- Estados visuais basicos nao quebram layout.

## Fechamento

- Web app renderiza shell e pode receber a primeira tela real.

### Testes executados
- Build `ng build` para garantir sanidade.
- `ng test` executado e garantindo renderizacao do `ShellComponent` (vitest/jsdom passou com sucesso).
- Validação das injeções de rota (`<router-outlet>`) e HTTP Service.

### Riscos remanescentes e mudancas laterais
- Angular 22 demandou uso de `--legacy-peer-deps` na instalacao do `lucide-angular`, pois a biblioteca aponta peer-dependencies limitadas ao Angular 21, mas o SVG wrapper é totalmente compativel funcionalmente. Risco minimo de quebra visual que sera mitigado com testes E2E/smoke futuros.
- Mudança na versao global do Node requerida para futuros desenvolvedores (Node >= 24).
