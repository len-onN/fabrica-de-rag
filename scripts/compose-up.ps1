param(
    [ValidateSet("base", "dev", "e2e")]
    [string] $Profile = "dev",

    [switch] $NoWait
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

$repoRoot = Get-RepoRoot
Assert-DockerComposeAvailable
Assert-DockerEngineAvailable

$composeArguments = New-DockerComposeBaseArguments -RepoRoot $repoRoot -Profile $Profile
$upArguments = $composeArguments + @("up", "-d")
if (-not $NoWait) {
    $upArguments += @("--wait", "--wait-timeout", "120")
}

Write-Info ("Subindo runtime local com profile '{0}'." -f $Profile)
& docker @upArguments
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

$psArguments = $composeArguments + @("ps")
& docker @psArguments
exit $LASTEXITCODE
