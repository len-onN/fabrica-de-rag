param(
    [ValidateSet("base", "dev", "e2e")]
    [string] $Profile = "dev"
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Stop-Placeholder `
    -CommandName ("scripts/compose-up.ps1 -Profile {0}" -f $Profile) `
    -PlannedBranch "build/dev-runtime-compose" `
    -Reason "Os arquivos Docker Compose ainda nao foram criados; este comando sera ligado ao compose real na proxima branch de runtime."

