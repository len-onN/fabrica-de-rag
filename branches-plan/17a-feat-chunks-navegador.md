# feat/chunks-navegador

Status: candidata.

## Objetivo

Criar navegador de chunks para inspecao, filtros, vizinhos e feedback local basico fora do laboratorio.

## Tags

CODE, UX, CONTRACT, DATA, TEST, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [MVP interface e API](../docs/mvp-interface-e-api.md), [Design visual](../docs/design-visual.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [feat/busca-vetorial-base](17-feat-busca-vetorial-base.md).
- Contratos/fixtures: [RAG](../tests/contracts/rag), [REST](../tests/contracts/rest), [eventos](../tests/contracts/events).

## Escopo

- Criar rota ou tab `/w/:workspaceId/collections/:collectionId/chunks`.
- Listar chunks com documento, pagina PDF, pagina impressa, heading path, token count e status de embedding.
- Filtrar por documento, pagina, label impresso, status e busca textual simples.
- Abrir origem.
- Ver vizinhos anterior/proximo.
- Registrar feedback local de relevancia quando habilitado.

## Fora de escopo

- Edicao manual de chunks.
- Mescla/divisao manual.
- Curadoria avancada.

## Definido

- Inspecao e feedback entram no MVP.
- Edicao profunda de chunks fica fora do MVP.
- SQL enriquece dados de chunks.

## Falta definir

- Busca textual simples via SQL ou estrategia temporaria.
- Campos finais exibidos por linha.
- Como agregar feedback no card/lista.

## Estrategia

1. Criar endpoint de listagem paginada.
2. Criar filtros.
3. Criar UI densa de inspecao.
4. Reutilizar componente de citacao/origem quando existir.
5. Registrar feedback/eventos.

## Testabilidade

- Filtros retornam chunks esperados.
- Workspace errado nao ve chunks.
- Vizinho respeita ordem e documento.
- UI cobre vazio, loading e erro.

## Fechamento

- Usuario consegue auditar chunks sem depender apenas do laboratorio.
