# feat/colecoes-documentos-core

Status: candidata.

## Objetivo

Criar recursos centrais de colecoes, documentos e metadados.

## Tags

CODE, DATA, CONTRACT, UX, TEST, SEC

## Escopo

- Criar schema de colecoes e documentos.
- Criar CRUD minimo de colecoes.
- Criar registro de documento sem processamento completo.
- Criar storage local inicial para arquivos.
- Criar UI minima de lista/criacao de colecao.
- Criar detalhe de colecao com tabs Documentos, Chunks, Jobs, Laboratorio, Analytics e Configuracoes como shell.
- Criar detalhe de documento com visao geral, paginas, chunks, analytics e configuracoes como shell.
- Expor API de administracao basica de colecoes dentro do escopo do MVP.

## Fora de escopo

- Upload/processamento completo de PDF.
- Qdrant.
- Chunking.

## Definido

- SQL e fonte da verdade.
- Workspace scope obrigatorio.
- Storage guarda binarios; banco guarda metadados.

## Falta definir

- Modelo inicial exato de storage local.
- Campos minimos de colecao/documento.
- Politica inicial de soft delete.
- Como representar tabs ainda sem implementacao completa.

## Estrategia

1. Criar migrations.
2. Criar use cases e endpoints.
3. Criar UI operacional de lista, criacao e detalhe.
4. Criar shells de tabs que serao preenchidas por branches futuras.
5. Testar isolamento por workspace.

## Testabilidade

- Migration.
- Repository/API tests.
- Teste de isolamento por workspace.

## Fechamento

- Colecoes e documentos existem como recursos reais e a UI tem os pontos de entrada do MVP.
