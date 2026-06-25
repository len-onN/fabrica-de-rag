param(
    [ValidateSet("base", "dev", "e2e")]
    [string] $Profile = "dev",

    [switch] $RemoveVolumes
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

$repoRoot = Get-RepoRoot
Assert-DockerComposeAvailable
Assert-DockerEngineAvailable

$composeArguments = New-DockerComposeBaseArguments -RepoRoot $repoRoot -Profile $Profile
$downArguments = $composeArguments + @("down")

if ($RemoveVolumes) {
    $downArguments += "--volumes"
}

Write-Info ("Encerrando runtime local com profile '{0}'." -f $Profile)
& docker @downArguments
exit $LASTEXITCODE
