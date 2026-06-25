# Scripts

Interface operacional local do repositorio.

Comandos atuais:

| Script | Estado | Saida esperada |
| --- | --- | --- |
| `check.ps1` | Implementado para a fundacao | Verifica a estrutura raiz e retorna `0`. |
| `test-unit.ps1` | Placeholder | Retorna `2` ate existirem suites unitarias. |
| `test-integration.ps1` | Placeholder | Retorna `2` ate existirem integracoes reais. |
| `test-smoke.ps1` | Placeholder | Retorna `2` ate a stack local existir. |
| `test-e2e.ps1` | Placeholder | Retorna `2` ate o e2e existir. |
| `compose-up.ps1` | Placeholder | Retorna `2` ate o Compose existir. |
| `compose-down.ps1` | Placeholder | Retorna `2` ate o Compose existir. |

Uso principal nesta branch:

```powershell
.\scripts\check.ps1
```

Os placeholders falham de proposito com mensagem clara, para nao fingir que uma stack inexistente foi testada.

