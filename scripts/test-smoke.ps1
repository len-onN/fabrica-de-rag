$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Stop-Placeholder `
    -CommandName "scripts/test-smoke.ps1" `
    -PlannedBranch "test/smoke-stack-local" `
    -Reason "A stack local ainda nao existe; o smoke sera criado depois do Compose e das bases das apps."

