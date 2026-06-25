$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Stop-Placeholder `
    -CommandName "scripts/test-unit.ps1" `
    -PlannedBranch "chore/backend-spring-base, chore/frontend-angular-base e chore/worker-python-base" `
    -Reason "As apps ainda nao foram scaffoldadas; este comando agregara testes unitarios quando as suites existirem."

