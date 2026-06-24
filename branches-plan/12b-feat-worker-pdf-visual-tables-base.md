# feat/worker-pdf-visual-tables-base

Status: candidata.

## Objetivo

Implementar a base da camada de interpretacao de imagens e tabelas em PDFs, usando os contratos definidos em `docs/interpretacao-imagens-tabelas-pdf`.

## Tags

CODE, RUNTIME, DATA, CONTRACT, TEST, PERF

## Escopo

- Detectar elementos visuais e tabelas em paginas de PDF.
- Extrair imagens embutidas ou regioes visuais como assets referenciaveis.
- Extrair tabelas simples para representacao textual/estruturada inicial.
- Interpretar imagens, diagramas e tabelas por adapter LLM/VLM quando habilitado.
- Persistir `document_elements`, assets e relacoes com pagina/documento.
- Criar fallback textual para tabelas/imagens com legenda, texto proximo ou OCR interno quando disponivel.
- Persistir interpretacoes visuais como texto derivado versionado, com provider/modelo/prompt/hash/confidence.
- Registrar confidence, metodo de extracao e erros por pagina/elemento.

## Fora de escopo

- Embeddings visuais.
- Busca multimodal por imagem.
- Edicao manual profunda de regioes.
- Extracao perfeita de tabelas complexas/multicoluna.

## Definido

- A camada visual/tabelas e parte fundamental do MVP.
- Render/OCR basico vem antes desta branch.
- SQL guarda a fonte da verdade dos elementos e assets.
- Worker Python concentra extracao pesada.
- Interpretacao por LLM/VLM entra por provider configuravel e deve ter adapter mockado para testes.
- Provider remoto exige configuracao explicita e respeito a privacidade/budget.

## Falta definir

- Biblioteca inicial para deteccao/extracao de tabelas.
- Representacao final da tabela simples.
- Heuristica inicial para relacionar imagem/tabela a legenda e texto proximo.
- Contrato inicial do vision interpreter e prompt versionado.
- Politica de cache/reprocessamento para interpretacoes por hash de asset/modelo/prompt.
- Limites de tempo/memoria por pagina.

## Estrategia

1. Aplicar contratos definidos em `docs/interpretacao-imagens-tabelas-pdf`.
2. Adicionar endpoints internos do worker para elementos visuais/tabelas.
3. Criar adapter de interpretacao visual com implementacao mockada/deterministica para testes.
4. Persistir assets/elementos/interpretacoes no backend.
5. Integrar logs/erros de run por pagina/elemento.
6. Criar fixtures pequenas de PDF com tabela e figura.

## Testabilidade

- Fixture com tabela simples gera elemento `table` e texto estruturado.
- Fixture com figura/legenda gera asset/elemento relacionado.
- Adapter mockado gera interpretacao textual versionada e citavel.
- PDF sem imagens/tabelas retorna lista vazia sem erro.
- Workspace scope e source locator sao preservados.
- Limites de tempo/memoria sao observados.

## Fechamento

- O pipeline passa a preservar imagens e tabelas como elementos interpretaveis, citaveis e utilizaveis por chunking/context builder.
