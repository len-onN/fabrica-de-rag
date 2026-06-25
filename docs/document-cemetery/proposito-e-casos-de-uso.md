# Proposito e Casos de Uso

## Proposito

A Fabrica de RAG existe para unir pesquisa e geracao com fontes reais.

O objetivo e permitir que pessoas e agentes criem bases de conhecimento confiaveis, rastreaveis e adaptaveis. A LLM nao deve substituir a fonte; ela deve ajudar a encontrar, reorganizar, comparar, explicar e aplicar o conhecimento preservando referencias.

## Ideia central

```text
Fontes reais
-> ingestao controlada
-> conhecimento estruturado
-> busca e contexto com proveniencia
-> geracao adaptavel
-> estudo, pesquisa, escrita, codigo e agentes
```

## Pesquisa e estudo

Um usuario pode criar uma base RAG com artigos, livros, apostilas, PDFs tecnicos, normas ou anotacoes.

A aplicacao deve ajudar a:

- manter referencias reais;
- comparar trechos;
- resumir com citacoes;
- criar guias de estudo;
- reorganizar conhecimento por tema;
- adaptar explicacoes ao modo de pesquisa do usuario;
- encontrar lacunas e contradicoes;
- voltar rapidamente ao trecho original.

## Escrita e producao intelectual

Uma colecao pode apoiar escrita academica, tecnica ou profissional.

Possibilidades:

- estruturar um argumento com base em fontes;
- gerar outlines;
- transformar notas em secoes;
- localizar evidencias;
- revisar consistencia entre texto produzido e fontes;
- produzir diferentes formatos a partir da mesma base.

## Desenvolvimento assistido por conhecimento

Um usuario pode criar um RAG com suas tecnicas de codigo, padroes, decisoes, documentacoes internas, snippets, guias de arquitetura e historico de aprendizado.

Esse RAG pode orientar:

- sugestoes de implementacao;
- padroes de arquitetura;
- estilo de codigo;
- revisoes;
- onboarding;
- agentes de desenvolvimento;
- geracao de codigo alinhada a tecnicas e preferencias documentadas.

## Agentes guiados por conhecimento

Agentes podem usar a base como fonte de comportamento e conhecimento por meio de API ou MCP.

Exemplos:

- agente que consulta politicas de contexto antes de responder;
- agente de codigo que busca padroes internos;
- agente de pesquisa que expande contexto e compara fontes;
- agente local que trabalha com documentos privados sem depender de memoria externa opaca.

## Implicacoes para UX

A aplicacao deve oferecer controles que ajudem o usuario a construir conhecimento, nao apenas armazenar vetores:

- perfil de ingestao;
- mapa de paginas;
- revisao de fontes;
- chunking por sentido;
- politicas de contexto;
- laboratorio de recuperacao;
- citacoes;
- curadoria de relacoes;
- ferramentas MCP seguras.

## Implicacoes para analytics local

O analytics local deve existir para ajudar o usuario a melhorar sua propria engenharia de conhecimento.

Ele pode ajudar a descobrir:

- quais fluxos de ingestao geram mais friccao;
- quais controles sao usados ou ignorados;
- quais politicas de contexto ajudam em certos tipos de uso;
- onde agentes entram em loops ou atingem limites;
- quais presets merecem evoluir;
- quais telas precisam explicar menos e fazer mais.

No MVP, esses dados ficam na propria instalacao do usuario. Uma telemetria remota para melhorar o produto como um todo pode ser discutida no futuro, mas deve ser opt-in, desligada por padrao e sem conteudo privado.

O analytics local nao deve buscar:

- identificar a pessoa;
- inferir temas especificos de pesquisa;
- enviar documentos ou perguntas para fora da instalacao;
- ranquear usuarios;
- criar perfil individual de aprendizado;
- alimentar publicidade ou tracking externo.

## Principio norteador

```text
A aplicacao deve ajudar o usuario a aprender sobre seus RAGs,
sem transformar seu conhecimento privado em telemetria externa.
```
