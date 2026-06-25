# test/e2e-mvp-ingestao-recuperacao

Status: candidata.

## Objetivo

Criar e2e do fluxo principal do MVP.

## Tags

TEST, RUNTIME, UX, DATA, CONTRACT, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [MVP interface e API](../docs/mvp-interface-e-api.md), [Cobertura do MVP pelo plano de branches](../docs/cobertura-mvp-branches.md), [build/dev-runtime-compose](03-build-dev-runtime-compose.md), [test/smoke-stack-local](07-test-smoke-stack-local.md), [feat/ingestao-pipeline-indexacao](16a-feat-ingestao-pipeline-indexacao.md), [feat/laboratorio-recuperacao](19-feat-laboratorio-recuperacao.md).
- Contratos/fixtures: [contracts](../tests/contracts), [fixtures](../tests/fixtures), [e2e](../tests/e2e).

## Escopo

- Subir stack via compose e2e.
- Executar Playwright.
- Usar fixtures pequenas.
- Cobrir workspace, colecao, upload, ingestao, chunks, busca e laboratorio.
- Cobrir settings essenciais.
- Cobrir status de run.
- Cobrir interpretacao visual/tabelas com adapter mockado.
- Cobrir abertura de fonte a partir de citacao.
- Cobrir feedback local e analytics basico.
- Coletar logs de falha.

## Fora de escopo

- Cobertura exaustiva.
- OCR pesado.
- LLM real.
- LLM/VLM real para interpretacao visual.
- Rede externa.

## Definido

- Compose e2e canonico em `infra/compose/compose.e2e.yml`.
- E2E deve ser curto e de alto valor.
- Fluxo segue o criterio de fechamento do MVP documentado em `mvp-interface-e-api.md`.

## Falta definir

- Fixture PDF final.
- Estrategia de seed e cleanup.
- Se o teste roda em CI desde o inicio.

## Estrategia

1. Criar ambiente e2e.
2. Criar seed minimo.
3. Criar fluxo Playwright.
4. Rodar em ambiente limpo.

## Testabilidade

- Pipeline e2e passa em ambiente limpo.
- Criterios do fechamento do MVP em `mvp-interface-e-api.md` sao exercitados ou justificados.
- Falhas deixam logs suficientes.

## Fechamento

- MVP tem prova ponta a ponta.
