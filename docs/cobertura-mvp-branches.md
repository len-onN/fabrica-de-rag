# Cobertura do MVP pelo Plano de Branches

## Objetivo

Este documento cruza o contrato funcional do MVP com o plano de branches. Ele existe para garantir que o desenvolvimento nao avance com lacunas invisiveis, nem deixe decisoes estruturais para serem improvisadas durante a implementacao.

## Resultado da avaliacao

O plano inicial cobria o nucleo principal do MVP, mas deixava lacunas em seis areas:

- decisoes estruturais ainda sem branch dona;
- operacao completa de `ingest_run`: status, cancelamento, retry e logs;
- render de paginas e OCR opcional;
- interpretacao de imagens e tabelas em PDFs;
- configuracoes do workspace;
- navegador de chunks fora do laboratorio;
- abertura/preview da fonte original a partir de uma citacao.

Essas lacunas foram cobertas adicionando branches de planejamento e branches funcionais especificas. A partir deste ajuste, nenhuma decisao arquitetural complexa deve ser empurrada para a feature branch que implementa o codigo.

## Portoes de planejamento antes do codigo

Antes da primeira branch de codigo de produto, o projeto deve fechar estes portoes:

| Branch | Decisao que tira da implementacao |
| --- | --- |
| `docs/modelo-dados-contratos-mvp` | Modelo relacional, IDs, DTOs REST, contratos Pydantic, payload Qdrant, eventos e estrategia OpenAPI. |
| `docs/arquitetura-ingestao-rag` | Maquina de estados da ingestao, orquestracao, retry/cancel, idempotencia, storage e reindexacao. |
| `docs/seguranca-permissoes-mvp` | Auth local, sessao/token, roles, matriz de permissoes, workspace scope, API/MCP e segredos. |
| `docs/algoritmos-rag-mvp` | Numeracao impressa, chunking, contexto, citacoes, ranking, budgets e complexidade. |
| `docs/analytics-observabilidade-mvp` | Taxonomia de eventos, retencao, export/delete, logs de jobs, privacidade e metricas. |
| `docs/interpretacao-imagens-tabelas-pdf` | Camada de elementos visuais/tabelas, assets, vision interpreter LLM/VLM, source locator, fallback textual e relacoes com chunks/citacoes. |

## Cobertura do criterio de fechamento do MVP

| Criterio do MVP | Branches responsaveis | Cobertura |
| --- | --- | --- |
| Criar ambiente local | `feat/auth-bootstrap-workspaces`, `feat/workspace-settings-mvp` | Coberto. |
| Criar uma colecao | `feat/colecoes-documentos-core` | Coberto, com UI operacional expandida. |
| Enviar um PDF | `feat/ingestao-upload-pdf` | Coberto. |
| Revisar/corrigir mapa de paginas | `feat/worker-pdf-render-ocr-base`, `feat/paginas-numeracao` | Coberto, incluindo preview de pagina. |
| Interpretar imagens e tabelas do PDF | `feat/worker-pdf-render-ocr-base`, `feat/worker-pdf-visual-tables-base` | Coberto por camada propria de elementos/assets/tabelas e interpretacao textual por adapter visual. |
| Rodar ingestao | `feat/ingestao-runs-operacao`, `feat/ingestao-pipeline-indexacao` | Coberto. |
| Gerar chunks e embeddings | `feat/chunking-semantico`, `feat/embeddings-base`, `feat/qdrant-indexacao` | Coberto. |
| Perguntar no laboratorio | `feat/busca-vetorial-base`, `feat/context-builder-base`, `feat/laboratorio-recuperacao` | Coberto. |
| Ver resposta com citacoes | `feat/resposta-rag-base`, `feat/citacoes-preview-fonte` | Coberto. |
| Abrir fonte original da citacao | `feat/citacoes-preview-fonte` | Coberto por nova branch. |
| Registrar feedback local | `feat/laboratorio-recuperacao`, `feat/analytics-eventos-base` | Coberto. |
| Ver analytics local basico | `feat/analytics-eventos-base`, `feat/analytics-dashboard-base` | Coberto, com planejamento dedicado. |

## Cobertura por telas do MVP

| Tela/fluxo | Branches responsaveis |
| --- | --- |
| `/setup`, `/login` | `feat/auth-bootstrap-workspaces` |
| Dashboard | `feat/workspace-dashboard-minimo` |
| Settings | `feat/workspace-settings-mvp` |
| Lista/nova/detalhe de colecoes | `feat/colecoes-documentos-core` |
| Upload/wizard de PDF | `feat/ingestao-upload-pdf`, `feat/ingestao-runs-operacao` |
| Status de ingestao | `feat/ingestao-runs-operacao`, `feat/ingestao-pipeline-indexacao` |
| Detalhe de documento | `feat/colecoes-documentos-core`, `feat/paginas-numeracao` |
| Mapa de paginas | `feat/worker-pdf-render-ocr-base`, `feat/paginas-numeracao` |
| Elementos visuais/tabelas | `feat/worker-pdf-visual-tables-base` |
| Navegador de chunks | `feat/chunks-navegador` |
| Laboratorio | `feat/laboratorio-recuperacao`, `feat/resposta-rag-base`, `feat/citacoes-preview-fonte` |
| Analytics | `feat/analytics-eventos-base`, `feat/analytics-dashboard-base` |
| API local/RAG | `feat/api-rag-publica` |
| MCP basico | `feat/mcp-tools-base`, sem bloquear o MVP funcional se ficar como extensao planejada |

## Fora do MVP

Continuam fora do MVP:

- conectores externos de fontes;
- upload DOCX/EPUB/MOBI;
- conectores externos de vector DB;
- MCP completo com administracao;
- convites multiusuario;
- service accounts completas;
- embeddings visuais;
- busca multimodal por imagem;
- curadoria manual profunda de chunks;
- telemetria remota.

## Regra operacional

Uma feature branch nao deve decidir arquitetura estrutural sozinha. Se a implementacao revelar uma decisao estrutural nao planejada, a branch deve parar, registrar a lacuna no registro de bordo e abrir ou atualizar uma branch `docs/*` responsavel pela decisao.
