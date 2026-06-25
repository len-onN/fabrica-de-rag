$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Stop-Placeholder `
    -CommandName "scripts/test-e2e.ps1" `
    -PlannedBranch "test/e2e-mvp-ingestao-recuperacao" `
    -Reason "Ainda nao ha fluxo de usuario nem Compose e2e executavel; o e2e fica reservado para o endurecimento do MVP."

