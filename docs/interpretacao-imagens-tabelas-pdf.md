# Interpretacao de Imagens e Tabelas em PDFs

## Objetivo

Este documento fecha o portao `docs/interpretacao-imagens-tabelas-pdf`.

Ele define a camada de elementos visuais e tabelas em PDFs: modelo conceitual, contratos Spring -> worker, `source_locator`, assets, representacao textual de tabelas, interpretacao visual por adapter LLM/VLM, privacidade, budgets, cache, relacoes com chunks, contexto, citacoes e analytics.

Ele nao implementa codigo, nao escolhe biblioteca concreta de tabelas e nao obriga provider remoto. As branches de implementacao devem transformar estas regras em migrations, DTOs, modelos Pydantic, adapters, policies, testes e fixtures.

## Fontes e skills ativadas

- Documentacao local: `docs/modelo-dados-contratos-mvp.md`, `docs/arquitetura-ingestao-rag.md`, `docs/seguranca-permissoes-mvp.md`, `docs/algoritmos-rag-mvp.md`, `docs/analytics-observabilidade-mvp.md`, `docs/padroes-de-projeto.md`, `docs/estrategia-de-testes.md`, `docs/skills-e-boas-praticas-para-agentes.md` e ADRs.
- Arquivos operacionais: `branches-plan/01f-docs-interpretacao-imagens-tabelas-pdf.md` e planos das branches `feat/worker-pdf-visual-tables-base`, `feat/chunking-semantico`, `feat/context-builder-base` e `feat/citacoes-preview-fonte`.
- Skill externa: `pdf`, usada como orientacao de layout/renderizacao, com a regra de nao depender apenas de extracao textual quando layout, tabelas, imagens e legibilidade importam.
- Fonte externa de provider LLM/VLM: nenhuma ativada nesta branch. Provider real fica atras de adapter configuravel e deve ativar documentacao oficial propria quando for escolhido.

## Decisoes principais

| Tema | Decisao |
| --- | --- |
| Fonte canonica | O PDF original, paginas, elementos, assets e metadados persistidos continuam sendo a fonte canonica. |
| Dados derivados | Tabelas normalizadas, OCR interno e interpretacoes visuais sao dados derivados versionados. |
| Worker | Python worker executa extracao visual/tabelas e interpretacao por contrato interno `/worker/v1`. |
| Backend | Spring valida workspace, permissao, storage, parametros, budget e persistencia; o worker nao decide autorizacao. |
| Source locator | Usar `pdf.source_locator.v1`, com pagina fisica 1-based, bbox normalizada, unidade fixa e ordem de leitura. |
| Bbox | Bbox usa pontos PDF normalizados com origem no canto superior esquerdo da pagina renderizada, apos rotacao normalizada. |
| Tabelas | Guardar `table_json_v1` como estrutura minima e `table_markdown_v1` como texto renderizavel/citavel. |
| Imagens/figuras | Guardar bbox/asset, legenda extraida, texto proximo seguro, OCR interno quando houver e interpretacao visual opcional. |
| Vision interpreter | Entrar por `VisionInterpreter` substituivel, com mock deterministico obrigatorio para testes e provider remoto opt-in. |
| Privacidade | Provider remoto fica desligado por padrao; nunca enviar PDF inteiro, workspace inteiro, segredo, prompt completo em log ou texto bruto alem do crop/elemento necessario. |
| Cache | Interpretacao visual pode ser reutilizada por hash de input + provider + modelo + prompt version + policy, escopada ao workspace. |
| Embeddings visuais | Fora do MVP. O texto derivado pode receber embedding textual como `visual_interpretation`. |
| Citacoes | Tabela/imagem/interpretacao deve abrir documento, pagina, bbox e fallback textual quando o preview visual nao estiver disponivel. |

## Contratos e versoes

| Contrato | Valor inicial |
| --- | --- |
| Source locator PDF | `pdf.source_locator.v1` |
| Elemento extraido | `pdf.document_element.v1` |
| Asset derivado de PDF | `pdf.asset.v1` |
| Estrutura de tabela | `pdf.table_json.v1` |
| Markdown de tabela | `pdf.table_markdown.v1` |
| Request de extracao | `worker.pdf.extract_elements.request.v1` |
| Response de extracao | `worker.pdf.extract_elements.response.v1` |
| Request de interpretacao visual | `worker.pdf.interpret_visual.request.v1` |
| Response de interpretacao visual | `worker.pdf.interpret_visual.response.v1` |
| Interpretacao visual persistida | `pdf.visual_interpretation.v1` |
| Prompt/policy inicial | `visual_interpreter_pdf_v1` |
| Provider mock | `mock-vision-interpreter` |
| Modelo mock | `mock-vision-interpreter-v1` |

Mudancas obrigatorias, renomeacoes, remocao de campo, mudanca de unidade de bbox, semantica de confidence ou formato de erro criam nova versao ou plano explicito de migracao.

## Source locator PDF

Contrato:

```text
pdf.source_locator.v1
```

Campos minimos:

| Campo | Obrigatorio | Regra |
| --- | --- | --- |
| `sourceType` | Sim | `pdf_upload` no MVP. |
| `documentId` | Sim | ID publico do documento. |
| `filePageNumber` | Sim | Pagina fisica do arquivo, 1-based. |
| `printedLabel` | Nao | Pagina impressa efetiva quando conhecida. |
| `unit` | Sim | Valor inicial `pdf_points_top_left`. |
| `pageWidth` | Sim | Largura normalizada da pagina em pontos. |
| `pageHeight` | Sim | Altura normalizada da pagina em pontos. |
| `rotation` | Sim | Rotacao aplicada para normalizar a pagina: `0`, `90`, `180` ou `270`. |
| `bbox` | Condicional | `[x0, y0, x1, y1]`, obrigatorio para elemento localizado. |
| `readingOrder` | Condicional | Inteiro crescente por pagina quando houver elemento ordenavel. |
| `elementId` | Nao | ID publico do elemento associado. |
| `assetId` | Nao | ID publico do asset associado. |

Regras:

- `bbox` deve ser normalizada para a pagina ja rotacionada para leitura.
- `x0 < x1` e `y0 < y1`.
- Coordenadas fora da pagina devem ser cortadas para os limites da pagina e gerar warning seguro.
- `readingOrder` e local da pagina; a ordenacao global usa documento + pagina + reading order.
- O source locator nunca carrega caminho local, storage path absoluto, texto integral, segredo ou hash interno.
- Fontes futuras podem criar outros locators sem alterar `pdf.source_locator.v1`.

Exemplo:

```json
{
  "sourceType": "pdf_upload",
  "documentId": "doc_fixture_pdf",
  "filePageNumber": 3,
  "printedLabel": "1",
  "unit": "pdf_points_top_left",
  "pageWidth": 612,
  "pageHeight": 792,
  "rotation": 0,
  "bbox": [72, 144, 520, 360],
  "readingOrder": 18,
  "elementId": "elm_fixture_table"
}
```

## Elementos

Contrato:

```text
pdf.document_element.v1
```

Tipos iniciais:

| Tipo | Uso |
| --- | --- |
| `text_block` | Bloco textual extraido ou OCR normalizado. |
| `heading` | Titulo ou subtitulo detectado. |
| `table` | Tabela detectada, com estrutura ou fallback textual. |
| `figure` | Diagrama, grafico, fluxo, screenshot ou imagem informacional composta. |
| `image` | Imagem embutida ou regiao visual simples. |
| `caption` | Legenda associavel a tabela, figura ou imagem. |
| `footnote` | Rodape textual com valor citavel. |
| `header` | Cabecalho repetitivo ou de pagina. |
| `footer` | Rodape repetitivo ou numeracao. |
| `unknown` | Regiao detectada sem classificacao confiavel. |

Campos obrigatorios para qualquer elemento persistivel:

- `workspaceId`;
- `documentId`;
- `pageId` ou `filePageNumber`;
- `elementId`;
- `elementType`;
- `sourceLocator`;
- `readingOrder`;
- `extractionMethod`;
- `confidence`;
- `contractVersion`.

Campos condicionais:

- `assetId`: obrigatorio para imagem/figura/crop persistido.
- `text`: texto renderizavel curto ou fallback textual.
- `structuredContent`: estrutura JSON versionada para tabela, figura ou OCR interno.
- `relatedElementIds`: legenda, tabela continuada, imagem descrita ou texto proximo.
- `warnings`: lista pequena de codigos seguros.

Regras:

- Elemento sem source locator nao deve gerar chunk citavel.
- `confidence` varia de `0.0` a `1.0`; `null` so e permitido quando o extrator nao tiver sinal numerico e a razao estiver em `warnings`.
- Header/footer repetitivo pode existir como elemento, mas `includeInSearch=false` deve ser decidido no pipeline de chunking/pagina quando houver evidencia.
- `unknown` pode ser persistido para auditoria de extracao, mas nao vira chunk por padrao.
- Elementos extraidos por worker mockado e real devem preservar a mesma semantica de erro, bbox, ordem, workspace e source locator.

## Assets

Contrato:

```text
pdf.asset.v1
```

Tipos iniciais continuam alinhados ao modelo de dados:

- `original_pdf`;
- `page_render`;
- `embedded_image`;
- `crop`;
- `thumbnail`.

Usos:

| Uso | Asset type | Regra |
| --- | --- | --- |
| PDF original | `original_pdf` | Criado no upload. |
| Preview de pagina | `page_render` | Criado por render/OCR base. |
| Imagem embutida | `embedded_image` | Usa hash e dimensoes do objeto extraido quando disponivel. |
| Regiao visual/tabela | `crop` | Crop de pagina ou elemento para interpretacao/preview. |
| Miniatura | `thumbnail` | Derivada de render/crop para UI. |

Campos minimos:

- `assetId`;
- `workspaceId`;
- `documentId`;
- `filePageNumber` quando aplicavel;
- `assetType`;
- `storageUri`;
- `mimeType`;
- `contentHash`;
- `widthPx`;
- `heightPx`;
- `sourceLocator`;
- `createdFrom`: `original_pdf`, `page_render`, `embedded_image` ou `crop`;
- `retentionClass`: `canonical`, `derived_cache` ou `temporary`.

Regras:

- `storageUri` usa IDs publicos e nunca caminho local absoluto.
- Crop/table region usa `assetType=crop` e `structuredContent.purpose`, nao novo enum no MVP.
- Assets derivados podem ser limpos/recriados por policy de cache quando o PDF original e o contrato de extracao forem iguais.
- Backend resolve URI; worker recebe URI autorizada e nao path arbitrario.
- Eventos e logs nunca carregam binario, base64, imagem, crop ou render.

## Tabelas

Contratos:

```text
pdf.table_json.v1
pdf.table_markdown.v1
```

Decisao:

- `table_json_v1` e a estrutura minima para UI, testes e reprocessamento.
- `table_markdown_v1` e o fallback textual inicial para chunks, contexto e citacao.
- CSV-like fica fora do contrato principal porque perde merged cells, legenda e metadados com facilidade.

`structuredContent` minimo para tabela:

```json
{
  "kind": "table_json_v1",
  "rows": 3,
  "columns": 2,
  "headerRows": 1,
  "markdown": "| Campo | Valor |\\n| --- | --- |\\n| Pagina fisica | 3 |",
  "cells": [
    {
      "row": 0,
      "column": 0,
      "text": "Campo",
      "rowspan": 1,
      "colspan": 1,
      "bbox": [72, 144, 220, 170]
    }
  ],
  "qualityFlags": []
}
```

Regras:

- Toda tabela citavel precisa de `sourceLocator` com bbox da regiao inteira.
- Cels individuais sao opcionais no MVP, mas quando existirem devem usar coordenadas normalizadas no mesmo sistema da pagina.
- Tabela pequena deve virar chunk `table_text` como unidade quando couber no limite.
- Tabela grande deve gerar chunk de resumo estruturado e manter detalhes no elemento/asset para preview futuro.
- Tabela quebrada entre paginas usa elementos separados ligados por `table_continuation`.
- Tabela sem estrutura confiavel pode virar `text` com `structuredContent.kind=table_markdown_v1` e warning `table_structure_low_confidence`.
- Markdown deve evitar texto decorativo e preservar cabecalhos, linhas e unidades relevantes.

## Imagens, figuras e legendas

Regras:

- `image` representa imagem simples ou objeto visual isolado.
- `figure` representa imagem informacional composta: diagrama, grafico, fluxo, screenshot, mapa, organograma ou desenho tecnico.
- `caption` deve ser elemento proprio quando detectada com confianca suficiente.
- Relacoes diretas devem usar `relatedElementIds` no contrato do worker e virar `chunk_relations` no backend quando persistidas.
- Texto proximo pode ser usado como contexto de interpretacao se for curto, seguro e ligado por proximidade/ordem; nao deve incluir pagina inteira.
- OCR interno de imagem entra em `structuredContent.detectedText` e pode ser usado como fallback, mas nao substitui a interpretacao visual quando a informacao depender de layout.
- Imagem sem legenda e sem interpretacao visual pode aparecer no inventario de elementos, mas nao deve gerar resposta afirmativa sozinha.

## Interpretacao visual

Contrato persistido:

```text
pdf.visual_interpretation.v1
```

Contrato de prompt/policy:

```text
visual_interpreter_pdf_v1
```

Objetivo:

- Gerar descricao textual estruturada de imagens, figuras, diagramas e tabelas quando a extracao local nao basta para preservar o conhecimento.

Entrada logica:

- elemento visual/tabela;
- asset/crop autorizado;
- source locator;
- legenda ou texto proximo curto;
- idioma preferido;
- policy de privacidade;
- budget por elemento/run;
- provider/modelo configurado ou mock.

Saida minima:

- `interpretationId`;
- `elementId`;
- `assetId`;
- `status`: `completed`, `failed` ou `skipped`;
- `interpretationText`;
- `structuredText`;
- `provider`;
- `model`;
- `promptVersion`;
- `inputHash`;
- `confidence`;
- `error` quando falhar;
- `usage` seguro quando houver provider remoto.

`structuredText` inicial:

| Campo | Regra |
| --- | --- |
| `kind` | `visual_interpretation_v1`. |
| `summary` | Descricao curta, citavel, sem extrapolar alem do visual. |
| `detectedText` | Texto visivel/OCR dentro da imagem quando existir. |
| `keyFacts` | Lista curta de fatos observaveis. |
| `relationships` | Ligacoes visiveis, eixos, setas, categorias ou partes. |
| `limitations` | Incertezas: baixa resolucao, corte parcial, texto ilegivel, tabela truncada. |

Regras:

- Interpretacao visual e dado derivado, nunca fonte canonica.
- Interpretacao nao pode apagar ou sobrescrever tabela/asset/source locator original.
- Resposta afirmativa gerada a partir de interpretacao visual deve citar o elemento e, quando possivel, mostrar preview da pagina/bbox.
- Provider real e mock devem preservar campos obrigatorios e erro estruturado.
- Prompt completo nao deve ser persistido em analytics/log; apenas `promptVersion`.

## Privacidade e provider remoto

Padrao do MVP:

- `remoteVisualProviderEnabled=false`.
- Testes, smoke e e2e usam `mock-vision-interpreter`.
- Provider remoto exige configuracao explicita do workspace e permissao administrativa.

Regras de envio:

- enviar apenas crop/asset necessario, nao o PDF inteiro;
- enviar texto proximo apenas quando `includeNearbyText=true` e limitado por budget;
- nunca enviar segredo, token, cookie, API key, connection string ou header;
- nunca registrar imagem/crop em log, evento ou analytics;
- nao enviar documento inteiro, workspace inteiro, lista de usuarios ou dados fora do elemento;
- quando houver duvida de sensibilidade, usar `skipped` com `privacy_blocked` em vez de chamar provider remoto.

Configuracao conceitual de workspace:

| Campo | Default | Regra |
| --- | --- | --- |
| `visualExtractionEnabled` | `true` | Extracao estrutural local de elementos/tabelas. |
| `remoteVisualProviderEnabled` | `false` | Chamada remota LLM/VLM. |
| `visualProvider` | `mock-vision-interpreter` | Provider ativo para interpretacao. |
| `visualHistoryMode` | `metadata_only` | Nunca guarda prompt completo por padrao. |
| `includeNearbyText` | `false` | Pode ser ligado para melhorar interpretacao. |
| `maxRemoteVisualElementsPerRun` | `20` | Limite inicial por run no MVP. |

## Budgets iniciais

Budgets sao guardrails operacionais. A implementacao pode ajustar por configuracao local, mas nao pode remover limites.

| Limite | Default MVP | Observacao |
| --- | --- | --- |
| Elementos visuais extraidos por documento | 1000 | Excedente gera warning e evento seguro. |
| Tabelas extraidas por documento | 200 | Suficiente para fixtures e uso local inicial. |
| Celulas por tabela estruturada | 2000 | Acima disso, guardar resumo + markdown truncado. |
| Tamanho maximo de crop para provider remoto | 2048 px no maior lado | Redimensionar mantendo proporcao. |
| Elementos remotos por run | 20 | Default conservador; configuravel por workspace. |
| Timeout de interpretacao por elemento | 30 s | Provider mock deve simular contrato sem espera real. |
| Output por interpretacao | 800 tokens estimados | Texto maior deve ser resumido/truncado com flag. |
| Texto proximo enviado ao interpreter | 1200 caracteres | Apenas quando habilitado. |
| Retentativas de provider remoto | 1 | Apenas erro transiente seguro. |

Quando um limite for atingido:

- persistir elemento/asset original quando possivel;
- marcar interpretacao como `skipped`;
- usar `error.code=budget_limit_hit`;
- emitir evento `visual_budget_limit_hit` sem conteudo sensivel;
- permitir retry/reprocessamento com configuracao nova.

## Cache e reprocessamento

Cache key:

```text
sha256(
  contractVersion + ":" +
  workspaceId + ":" +
  documentId + ":" +
  elementId + ":" +
  assetContentHash + ":" +
  sourceLocatorHash + ":" +
  provider + ":" +
  model + ":" +
  promptVersion + ":" +
  language + ":" +
  privacyPolicyHash
)
```

Regras:

- cache e escopado por workspace no MVP.
- alteracao de asset, bbox, prompt version, modelo, idioma ou policy invalida a interpretacao.
- cache hit deve registrar metadado seguro, mas nao duplicar chamada remota.
- reprocessamento cria nova `ingestion_generation`; resultado antigo so deixa de ser ativo em `finalize`.
- falha transiente pode ser reexecutada se budget permitir.
- falha `privacy_blocked`, `unsupported_media` ou `content_too_large` nao deve ser retry automatico.

## Chunking, contexto e citacoes

Regras para chunks:

- `table` pode gerar chunk `contentKind=table_text`.
- `figure` ou `image` com interpretacao concluida pode gerar chunk `contentKind=visual_interpretation`.
- Legenda pode entrar no chunk visual ou ser chunk textual relacionado, conforme tamanho e ordem de leitura.
- `unknown`, `header` e `footer` nao viram chunk por padrao.
- Tabela e interpretacao visual nao devem ser duplicadas por overlap.
- Source locator e provider/model/prompt version devem ficar rastreaveis por origem do chunk.

Relacoes iniciais:

- `caption_of_image`;
- `table_continuation`;
- `derived_from_visual`;
- `previous`;
- `next`;
- `same_section` quando houver heading path.

Regras para contexto:

- context builder inclui tabela/visual diretamente relacionado quando a ancora depender dele.
- tabela pequena entra como unidade; tabela grande entra como resumo/truncamento com citacao preservada.
- interpretacao visual deve ser identificada como derivada para nao parecer texto canonico do PDF.
- nunca remover source locator para economizar tokens.

Regras para citacoes:

- citacao de tabela/imagem deve carregar `sourceElementId`, `assetId` quando houver, `bbox` e `contentKind`.
- preview deve abrir pagina renderizada e destacar bbox quando o render existir.
- sem render, usar fallback textual com aviso.
- workspace scope e permissao devem ser revalidados antes de abrir asset ou preview.

## Contratos Spring -> worker

### `POST /worker/v1/pdf/extract-elements`

Request:

- `contractVersion=worker.pdf.extract_elements.request.v1`;
- `requestId`;
- `workspaceId`;
- `documentId`;
- `runId`;
- `storageUri`;
- `pages`;
- `options`.

Options iniciais:

- `detectTables`;
- `detectImages`;
- `detectCaptions`;
- `includeCellBBoxes`;
- `renderAssetReferences`;
- `maxElements`;
- `maxTableCells`;

Response:

- `contractVersion=worker.pdf.extract_elements.response.v1`;
- `elements`;
- `assets`;
- `pageSummaries`;
- `warnings`.

### `POST /worker/v1/pdf/interpret-visual`

Request:

- `contractVersion=worker.pdf.interpret_visual.request.v1`;
- `requestId`;
- `workspaceId`;
- `documentId`;
- `runId`;
- `language`;
- `provider`;
- `model`;
- `promptVersion`;
- `privacyPolicy`;
- `budget`;
- `items`.

Response:

- `contractVersion=worker.pdf.interpret_visual.response.v1`;
- `interpretations`;
- `budget`;
- `warnings`.

Erro estruturado usa `worker.error.v1` e os codigos abaixo quando aplicaveis:

| Codigo | Retry automatico | Uso |
| --- | --- | --- |
| `pdf_render_missing` | Nao | Elemento depende de render inexistente. |
| `visual_asset_missing` | Nao | Asset/crop referenciado nao existe. |
| `unsupported_media` | Nao | Mime/dimensao nao suportada. |
| `table_structure_low_confidence` | Nao | Tabela vira fallback textual. |
| `provider_timeout` | Sim | Provider remoto estourou timeout. |
| `provider_unavailable` | Sim | Provider remoto indisponivel. |
| `privacy_blocked` | Nao | Policy impede envio/interpretacao. |
| `budget_limit_hit` | Nao | Limite da run/workspace atingido. |
| `invalid_visual_contract` | Nao | Payload incompativel com contrato. |

## Analytics e observabilidade

Eventos reservados em `docs/analytics-observabilidade-mvp.md` ficam com estes campos minimos adicionais:

| Evento | Propriedades seguras |
| --- | --- |
| `visual_elements_extracted` | `tablesCount`, `figuresCount`, `imagesCount`, `unknownCount`, `durationMs`, `warningsCount`. |
| `table_extracted` | `rowsBucket`, `columnsBucket`, `confidenceBucket`, `hasCellBBoxes`, `continuedAcrossPages`. |
| `visual_interpretation_completed` | `provider`, `model`, `promptVersion`, `durationMs`, `confidenceBucket`, `cacheHit`. |
| `visual_interpretation_failed` | `provider`, `model`, `promptVersion`, `errorCode`, `retryable`. |
| `visual_budget_limit_hit` | `provider`, `limitType`, `itemsSkippedCount`, `profile`. |

Campos proibidos continuam valendo:

- imagem, crop, render, base64 ou PDF bruto;
- texto integral de pagina/documento;
- prompt completo;
- resposta completa de provider quando historico local estiver desligado;
- segredo, token, cookie, API key ou connection string.

## OCP, LSP e IoC

Pontos de extensao:

- `PdfElementExtractor`;
- `TableExtractor`;
- `VisualAssetExtractor`;
- `CaptionResolver`;
- `VisionInterpreter`;
- `VisualPrivacyPolicy`;
- `VisualBudgetPolicy`;
- `VisualInterpretationCache`;
- `VisualChunkMapper`;
- `VisualCitationResolver`.

Invariantes LSP:

- extratores novos preservam `pdf.source_locator.v1`, bbox, reading order, confidence e erro estruturado;
- table extractors retornam `table_json_v1` ou fallback declarado, nunca paragrafo comum silencioso;
- vision interpreter mockado e real preservam provider, modelo, prompt version, input hash, confidence, status e erro;
- cache nao muda semantica observavel da interpretacao;
- policies nunca relaxam workspace scope, permissao, privacidade ou budget;
- citation resolvers sempre conseguem fallback textual ou erro seguro quando preview nao existe.

IoC:

- backend depende de ports para worker, storage, cache, event publisher e citation resolver;
- FastAPI dependencies ligam bibliotecas concretas de PDF/tabela/visao;
- provider remoto e configurado por adapter, nao chamado diretamente por caso de uso;
- MCP/API consomem contratos do backend e nao acessam assets/storage/worker diretamente.

## Testabilidade obrigatoria

Fixtures adicionadas ou esperadas nesta branch:

- `tests/contracts/worker/pdf-extract-elements.request.v1.json`;
- `tests/contracts/worker/pdf-extract-elements.response.v1.json`;
- `tests/contracts/worker/pdf-interpret-visual.request.v1.json`;
- `tests/contracts/worker/pdf-interpret-visual.response.v1.json`;
- `tests/contracts/rag/citation-visual.v1.json`;
- `tests/contracts/events/visual-interpretation-completed.v1.json`;
- `tests/contracts/events/table-extracted.v1.json`.

Testes futuros por branch:

| Branch | Casos obrigatorios |
| --- | --- |
| `feat/worker-pdf-visual-tables-base` | tabela simples, tabela quebrada entre paginas, figura com legenda, imagem sem legenda, PDF sem elementos visuais, provider mockado e budget excedido. |
| `feat/chunking-semantico` | tabela como chunk proprio, interpretacao visual como chunk derivado, legenda relacionada, pagina ignorada e tabela grande truncada. |
| `feat/context-builder-base` | relacao direta de tabela/visual, budget pequeno, deduplicacao, truncamento e citacao preservada. |
| `feat/citacoes-preview-fonte` | bbox destacado, fallback textual sem render, asset indisponivel, workspace errado e source locator com pagina impressa. |
| `feat/analytics-eventos-base` | eventos visuais validos, proibicao de binario/prompt, budget hit e falha segura. |

Checks minimos:

- todo exemplo JSON e valido;
- cada elemento citavel tem source locator;
- tabela nao vira paragrafo comum sem warning;
- provider remoto fica desligado por padrao;
- mock visual nao depende de rede;
- logs/eventos nao carregam imagem, prompt completo ou texto integral;
- workspace scope aparece em todos os contratos persistiveis.

## Pendencias encaminhadas

- Biblioteca concreta de extracao de tabelas/imagens: `feat/worker-pdf-visual-tables-base`.
- Parametros reais de memoria/tempo por biblioteca: `feat/worker-pdf-visual-tables-base`.
- Prompt final de provider remoto: branch que escolher provider real de visao.
- UI final de destaque bbox/modal/painel: `feat/citacoes-preview-fonte`.
- Ajustes finos de chunking com PDFs reais: `feat/chunking-semantico`.
- Busca multimodal, embeddings visuais e curadoria profunda: fora do MVP.

## Fechamento do portao

Com este portao fechado, as branches de worker, chunking, contexto, citacoes, analytics e preview podem implementar imagens/tabelas sem decidir de improviso:

- quais campos tornam um elemento citavel;
- como bbox e source locator sao representados;
- quando tabela vira chunk proprio;
- como interpretar imagem/tabela por adapter substituivel;
- como preservar privacidade e budget;
- quais fixtures protegem compatibilidade.
