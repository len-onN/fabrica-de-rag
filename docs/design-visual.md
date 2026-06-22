# Design Visual

## Visao

A interface da Fabrica de RAG deve ser minimalista, densa e legivel. O produto e uma ferramenta de trabalho para engenharia de conhecimento, nao uma landing page ou dashboard decorativo.

A identidade visual inicial usa violeta como assinatura e verde como cor semantica de confianca, progresso e qualidade.

## Principios

- Modo claro e modo escuro desde o MVP.
- Violeta como identidade, nao como fundo dominante em tudo.
- Verde para estados positivos e confiaveis.
- Neutros para leitura, superficies, tabelas e paineis.
- Cor deve orientar acao, estado e hierarquia.
- Evitar interfaces monocromaticas ou saturadas demais.
- Bordas suaves por padrao; bordas fortes apenas para foco, selecao e estados ativos.
- Priorizar contraste, legibilidade e uso prolongado.

## Papeis semanticos

```text
Violeta
- marca
- acoes primarias
- foco
- selecao
- links importantes
- chunks/contextos ativos

Verde
- sucesso
- pronto
- validado
- qualidade boa
- chunk relevante
- indexacao completa

Neutros
- fundo
- superficies
- tabelas
- textos
- bordas
- divisoes

Ambar
- aviso
- revisao necessaria
- OCR incerto
- numeracao ambigua

Vermelho
- erro
- falha de ingestao
- permissao negada
- exclusao destrutiva
```

## Paleta clara

```text
background:        #F8F7FB
surface:           #FFFFFF
surface-muted:     #F1EEF8
border:            #D8D0E6
border-strong:     #B9A9D4

text:              #1E1A27
text-muted:        #665D73

primary:           #6D3FD1
primary-hover:     #5B2DBA
primary-soft:      #E8DFFF

success:           #2F8F63
success-soft:      #E4F7EC

warning:           #B7791F
danger:            #B42318
```

## Paleta escura

```text
background:        #100E17
surface:           #171320
surface-muted:     #211A2D
border:            #342747
border-strong:     #4A3863

text:              #F5F1FA
text-muted:        #B8AEC7

primary:           #A78BFA
primary-hover:     #BFAAFF
primary-soft:      #2B2140

success:           #74D99F
success-soft:      #173524

warning:           #F2B84B
danger:            #FF7A7A
```

## Tokens sugeridos

```text
--color-bg
--color-surface
--color-surface-muted
--color-border
--color-border-strong
--color-text
--color-text-muted
--color-primary
--color-primary-hover
--color-primary-soft
--color-success
--color-success-soft
--color-warning
--color-danger
```

## Uso por componente

### Sidebar

- fundo neutro-violeta suave;
- item ativo com primary-soft;
- icone/texto ativo em primary;
- contadores e badges com neutros ou success quando indicarem pronto.

### Botoes

- primario: violeta;
- secundario: neutro com borda;
- sucesso: verde apenas quando a acao confirma validacao ou conclusao;
- destrutivo: vermelho;
- icones com tooltip em acoes compactas.

### Tabelas e listas

- fundo neutro/surface;
- bordas suaves;
- selecao com primary-soft;
- status positivos em success;
- avisos em warning;
- erros em danger.

### Chunks e contexto

- chunk selecionado: primary-soft;
- chunk relevante: marcador success;
- chunk irrelevante: marcador danger discreto;
- contexto enviado ao modelo: borda primary;
- origem/citacao validada: success.

### Jobs de ingestao

- queued/running: primary;
- completed: success;
- needs_review: warning;
- failed: danger;
- cancelled: text-muted.

## Tema

Preferencias:

```text
system
light
dark
```

O usuario pode escolher o tema nas configuracoes do workspace ou usuario. A primeira implementacao pode seguir a preferencia do sistema e permitir alternancia manual.

## Regras de nao uso

- Nao usar violeta em todos os fundos.
- Nao usar verde como cor primaria de marca.
- Nao usar gradientes decorativos como base da interface.
- Nao depender apenas de cor para comunicar estado.
- Nao usar vermelho para estados que sao apenas aviso.
- Nao sacrificar contraste para manter suavidade visual.

## Direcao visual

Nome interno sugerido:

```text
Violet Lab
```

Resumo:

```text
Violeta como assinatura.
Verde como confianca.
Neutros como ferramenta de trabalho.
```
