# feat/laboratorio-recuperacao

Status: candidata.

## Objetivo

Criar laboratorio de recuperacao com pergunta, chunks, contexto e citacoes.

## Tags

CODE, UX, CONTRACT, TEST, OBS

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
