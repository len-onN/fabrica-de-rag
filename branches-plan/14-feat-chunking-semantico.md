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

## Falta definir

- Tamanho inicial de chunk.
- Overlap inicial por perfil.
- Campos exatos de heading path.

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

## Fechamento

- Documento gera chunks rastreaveis.

