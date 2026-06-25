# feat/laboratorio-recuperacao

Status: candidata.

## Objetivo

Criar laboratorio de recuperacao com pergunta, chunks, contexto e citacoes.

## Tags

CODE, UX, CONTRACT, TEST, OBS

## Documentos relevantes

- Estado vivo: [Registro de bordo](../docs/registro-de-bordo.md), [Diario de bordo](../docs/diario-de-bordo.md).
- Base operacional: [Trigger de implementacao agentica](../docs/trigger-implementacao-agentica.md), [Plano tecnico do MVP](../docs/plano-tecnico-mvp.md), [Padroes de projeto](../docs/padroes-de-projeto.md), [Estrategia de testes](../docs/estrategia-de-testes.md), [Decisoes de arquitetura](../docs/decisoes-arquiteturais.md).
- Especificos: [MVP interface e API](../docs/mvp-interface-e-api.md), [Algoritmos RAG](../docs/algoritmos-rag-mvp.md), [Analytics e observabilidade](../docs/analytics-observabilidade-mvp.md), [Design visual](../docs/design-visual.md), [Seguranca e permissoes](../docs/seguranca-permissoes-mvp.md), [feat/context-builder-base](18-feat-context-builder-base.md).
- Contratos/fixtures: [RAG](../tests/contracts/rag), [eventos](../tests/contracts/events).

## Escopo

- Tela de pergunta.
- Mostrar chunks recuperados.
- Mostrar contexto expandido.
- Mostrar citacoes.
- Registrar feedback local de chunk/resposta.
- Registrar eventos basicos.

## Fora de escopo

- Comparacao de perfis.
- Benchmark avancado.
- Curadoria manual completa.

## Definido

- Transparencia antes de automacao.
- UI mostra o que foi enviado ao modelo.

## Falta definir

- Layout detalhado.
- Campos exatos exibidos para chunk/contexto.
- Como reaproveitar componente de preview de citacao quando `feat/citacoes-preview-fonte` entrar.

## Estrategia

1. Criar endpoint de laboratorio.
2. Criar tela.
3. Renderizar chunks/contexto.
4. Registrar feedback e eventos basicos.

## Testabilidade

- API com fixture.
- UI renderiza chunks/contexto.
- Estado sem resultado.
- Feedback gera evento local.

## Fechamento

- Usuario consegue testar qualidade de recuperacao.
