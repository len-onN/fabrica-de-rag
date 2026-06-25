# Scripts

Interface operacional local do repositorio.

Comandos atuais:

| Script | Estado | Saida esperada |
| --- | --- | --- |
| `check.ps1` | Implementado para fundacao e Compose | Verifica estrutura raiz, contratos e `docker compose config` para `base`, `dev` e `e2e`. |
| `test-unit.ps1` | Placeholder | Retorna `2` ate existirem suites unitarias. |
| `test-integration.ps1` | Placeholder | Retorna `2` ate existirem integracoes reais. |
| `test-smoke.ps1` | Placeholder | Retorna `2` ate a stack local existir. |
| `test-e2e.ps1` | Placeholder | Retorna `2` ate o e2e existir. |
| `compose-up.ps1` | Implementado para dependencias | Sobe Postgres e Qdrant e aguarda health checks por padrao. |
| `compose-down.ps1` | Implementado para dependencias | Encerra o profile escolhido e pode remover volumes. |

Uso principal:

```powershell
.\scripts\check.ps1
.\scripts\compose-up.ps1 -Profile dev
.\scripts\compose-down.ps1 -Profile dev
```

Profiles do Compose:

| Profile | Arquivos | Uso |
| --- | --- | --- |
| `base` | `compose.yml` | Configuracao base sem portas extras. |
| `dev` | `compose.yml` + `compose.dev.yml` | Desenvolvimento local com portas publicadas. |
| `e2e` | `compose.yml` + `compose.e2e.yml` | Ambiente isolado para testes e2e futuros. |

Os scripts de teste que ainda sao placeholders falham de proposito com mensagem clara, para nao fingir que uma suite inexistente foi testada.
