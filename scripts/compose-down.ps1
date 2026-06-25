$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Stop-Placeholder `
    -CommandName "scripts/compose-down.ps1" `
    -PlannedBranch "build/dev-runtime-compose" `
    -Reason "Os arquivos Docker Compose ainda nao foram criados; nao ha stack versionada para encerrar nesta branch."

