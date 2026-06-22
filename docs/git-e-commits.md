# Git e Commits

## Branches

Branches principais:

```text
develop
```

Branches de trabalho devem descrever a frente de trabalho.

Exemplos:

```text
planejamento/documentacao
feature/ingestao-pdf
feature/laboratorio-recuperacao
fix/mapa-paginas
docs/padroes-projeto
```

## Conventional Commits

Os commits devem seguir Conventional Commits.

Formato:

```text
tipo(escopo opcional): mensagem curta em portugues
```

Exemplos:

```text
docs: registra proposta inicial do projeto
feat(ingestao): adiciona upload de PDF
fix(paginas): corrige inferencia de numeracao impressa
refactor(api): reorganiza contratos de colecoes
test(worker): cobre extracao de texto nativo
chore(docker): adiciona postgres e qdrant ao compose
```

Tipos comuns:

```text
feat
fix
docs
style
refactor
test
chore
build
ci
perf
revert
```

## Idioma

Mensagens de commit devem ser escritas em portugues.

Exemplo:

```text
docs: documenta contrato funcional do MVP
```

## Escopos longos

O escopo do commit deve ser curto sempre que possivel.

Quando o contexto for grande ou o escopo for longo demais para uma mensagem curta, usar multiplas mensagens `-m` para manter o commit informativo.

Exemplo:

```text
git commit \
  -m "docs: registra planejamento inicial do MVP" \
  -m "Documenta proposta, features, ADRs, interface, API, usuarios, analytics local e design visual." \
  -m "Estabelece PDF como fonte documental inicial e deixa conectores externos para fases futuras."
```

## Regras praticas

- Commits devem representar uma unidade coerente de mudanca.
- Evitar commits que misturem implementacao, formatacao e documentacao sem relacao.
- Preferir escopos curtos e claros.
- Usar corpo do commit quando o motivo ou o impacto nao couber na primeira linha.
- Commits de planejamento podem ser `docs`.
- Commits de configuracao de projeto podem ser `chore`, `build` ou `ci`, conforme o caso.

## Primeiro fluxo do repositorio

Fluxo inicial definido:

```text
develop
planejamento/documentacao
```

A branch `planejamento/documentacao` concentra o estado inicial de documentacao e planejamento do MVP.
