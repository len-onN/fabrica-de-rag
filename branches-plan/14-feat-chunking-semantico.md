# feat/chunking-semantico

Status: candidata.

## Objetivo

Criar chunking inicial com proveniencia e persistencia.

## Tags

CODE, DATA, CONTRACT, TEST, PERF

## Escopo

- Definir algoritmo inicial.
- Gerar chunks por bloco/paragrafo.
- Registrar heading path quando disponivel.
- Aplicar overlap por perfil.
- Persistir chunks.

## Fora de escopo

- Curadoria manual avancada.
- Tabelas complexas.
- Relacoes semanticas sofisticadas.

## Definido

- Chunk recuperavel nao e contexto final.
- Proveniencia e obrigatoria.
- Algoritmo definido em `docs/algoritmos-rag-mvp.md` como `semantic_block_v1`.
- Default `balanced`: target 700 tokens e overlap 80.
- Default `fast`: target 450 tokens e overlap 40.
- Tabelas e interpretacoes visuais citaveis viram chunks proprios quando couberem no budget.
- Regras de tabela/visual fechadas em `docs/interpretacao-imagens-tabelas-pdf.md`: tabela pequena entra como `table_text`, interpretacao concluida entra como `visual_interpretation`, e ambas preservam source locator.

## Falta definir

- Ajustes finos apos fixtures reais de PDF, sem mudar o contrato `semantic_block_v1`.

## Estrategia

1. Especificar algoritmo.
2. Criar contrato de entrada/saida.
3. Implementar no worker ou backend conforme decisao tecnica.
4. Persistir chunks.
5. Testar fixtures textuais.

## Testabilidade

- Texto curto.
- Texto longo.
- Limite de tamanho.
- Overlap.
- Ordem e proveniencia.
- Tabela como unidade, tabela grande truncada com flag e interpretacao visual sem duplicar overlap.

## Fechamento

- Documento gera chunks rastreaveis.
