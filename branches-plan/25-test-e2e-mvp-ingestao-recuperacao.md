# test/e2e-mvp-ingestao-recuperacao

Status: candidata.

## Objetivo

Criar e2e do fluxo principal do MVP.

## Tags

TEST, RUNTIME, UX, DATA, CONTRACT, OBS

## Escopo

- Subir stack via compose e2e.
- Executar Playwright.
- Usar fixtures pequenas.
- Cobrir workspace, colecao, upload, ingestao, chunks, busca e laboratorio.
- Cobrir settings essenciais.
- Cobrir status de run.
- Cobrir abertura de fonte a partir de citacao.
- Cobrir feedback local e analytics basico.
- Coletar logs de falha.

## Fora de escopo

- Cobertura exaustiva.
- OCR pesado.
- LLM real.
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
- Criterios 1 a 11 do fechamento do MVP sao exercitados ou justificados.
- Falhas deixam logs suficientes.

## Fechamento

- MVP tem prova ponta a ponta.
