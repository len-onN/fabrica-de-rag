# chore/frontend-angular-base

Status: candidata.

## Objetivo

Criar a base Angular executavel, testavel e alinhada ao design visual.

## Tags

CODE, UX, CONTRACT, TEST, RUNTIME

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

## Falta definir

- Versao Angular.
- Biblioteca de UI ou componentes proprios.
- Runner final de unit/component tests.

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

