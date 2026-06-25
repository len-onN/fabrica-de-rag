# feat/worker-pdf-inspect

Status: candidata.

## Objetivo

Integrar o worker Python para inspecao basica de PDF.

## Tags

CODE, RUNTIME, CONTRACT, DATA, TEST, OBS, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Modelo de dados e contratos](../docs/modelo-dados-contratos-mvp.md), [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [chore/worker-python-base](06-chore-worker-python-base.md), [feat/ingestao-upload-pdf](11-feat-ingestao-upload-pdf.md).
- Contratos/fixtures: [worker pdf inspect](../tests/contracts/worker), [ingestao](../tests/contracts/ingestion), [PDF fixtures](../tests/fixtures/pdfs).

## Escopo

- Criar endpoint interno `pdf/inspect`.
- Extrair numero de paginas e metadados basicos.
- Criar contrato Spring -> worker.
- Persistir resultado inicial da inspecao.
- Registrar erro estruturado quando o PDF falhar.
- Extrair texto nativo simples quando isso couber no mesmo contrato.

## Fora de escopo

- OCR, que entra em `feat/worker-pdf-render-ocr-base`.
- Extracao completa de layout.
- Chunking.

## Definido

- Worker usa Pydantic.
- API interna REST no MVP.
- Timeout explicito entre Spring e worker.

## Falta definir

- Biblioteca PDF inicial.
- Campos exatos do contrato de inspect.
- Timeout inicial.
- Separacao final entre inspect e extract-text.

## Estrategia

1. Definir contrato.
2. Implementar endpoint no worker.
3. Criar client interno no Spring.
4. Atualizar ingest run com resultado.
5. Testar fixture pequena.

## Testabilidade

- Teste worker com PDF fixture.
- Teste contrato Spring -> worker.
- Falha de PDF malformado.

## Fechamento

- Run de ingestao registra inspecao real do PDF.
