# feat/ingestao-upload-pdf

Status: candidata.

## Objetivo

Implementar upload de PDF e criacao de run inicial de ingestao.

## Tags

CODE, DATA, CONTRACT, UX, TEST, SEC, OBS

## Escopo

- Validar arquivo PDF.
- Persistir PDF no storage local.
- Criar `ingest_run`.
- Expor status inicial da run.
- Criar UI minima de upload.
- Registrar evento local basico.
- Preparar link para a rota de status criada em `feat/ingestao-runs-operacao`.

## Fora de escopo

- Extracao completa.
- OCR.
- Chunking.
- Embeddings.
- Cancelamento/retry/logs completos.

## Definido

- PDF e unica fonte documental no MVP.
- Upload nao deve executar processamento pesado sincrono.

## Falta definir

- Limite inicial de tamanho.
- Diretorio/padrao de nomes no storage.
- Politica de erro para PDF invalido.

## Estrategia

1. Criar endpoint multipart.
2. Validar tipo/tamanho.
3. Gravar storage e metadados.
4. Criar ingest run.
5. Mostrar status na UI.

## Testabilidade

- Upload valido.
- Arquivo invalido.
- Storage/metadados consistentes.
- Status da run.

## Fechamento

- Usuario envia PDF, cria run inicial e pode seguir para o acompanhamento operacional.
