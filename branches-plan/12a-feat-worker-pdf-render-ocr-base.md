# feat/worker-pdf-render-ocr-base

Status: em andamento.

## Objetivo

Adicionar render de paginas e OCR opcional basico ao worker para suportar mapa de paginas, preview de fonte e PDFs sem texto nativo suficiente.

## Tags

CODE, RUNTIME, CONTRACT, DATA, UX, TEST, PERF

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Arquitetura de ingestao e RAG](../docs/arquitetura-ingestao-rag.md), [Interpretacao de imagens e tabelas em PDFs](../docs/interpretacao-imagens-tabelas-pdf.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [feat/worker-pdf-inspect](12-feat-worker-pdf-inspect.md), [Design visual](../docs/design-visual.md).
- Contratos/fixtures: [worker](../tests/contracts/worker), [PDF fixtures](../tests/fixtures/pdfs), [ingestao](../tests/contracts/ingestion).

## Escopo

- Implementar endpoint interno `pdf/render-page`.
- Implementar endpoint interno `pdf/extract-text` quando nao couber em `worker-pdf-inspect`.
- Implementar endpoint interno `pdf/ocr` com modo basico e configuravel.
- Persistir artefatos de preview/render no storage local.
- Registrar qualidade textual por pagina.
- Permitir OCR manual/automatico/forcado conforme configuracao.

## Fora de escopo

- OCR avancado por layout complexo.
- Interpretacao de imagens/tabelas, que entra em `feat/worker-pdf-visual-tables-base`.
- Embeddings visuais.

## Definido

- OCR e opcional no MVP.
- Preview de pagina e necessario para revisar mapa e abrir fonte.
- Worker concentra processamento documental pesado.

## Falta definir

- Biblioteca OCR inicial.
- DPI/resolucao padrao do render.
- Limites de memoria/tempo por pagina.
- Politica de cache/limpeza de renders.

## Estrategia

1. Definir contratos Pydantic.
2. Implementar render de pagina com fixture pequena.
3. Implementar OCR basico atras de configuracao.
4. Persistir referencias no SQL/storage.
5. Medir tempo/memoria em fixture representativa.

## Testabilidade

- Render gera artefato esperado.
- OCR desligado nao executa OCR.
- OCR forcado marca pagina corretamente.
- PDF malformado retorna erro estruturado.
- Limites de tempo/memoria sao observados.

## Fechamento

- Mapa de paginas e citacoes podem mostrar preview real da fonte.
