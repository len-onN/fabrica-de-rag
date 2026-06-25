$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Stop-Placeholder `
    -CommandName "scripts/test-integration.ps1" `
    -PlannedBranch "build/dev-runtime-compose e branches base das stacks" `
    -Reason "Nao ha banco, Qdrant, worker ou API executavel nesta branch; integracao real entra quando a infraestrutura existir."

