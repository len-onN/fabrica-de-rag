Set-StrictMode -Version Latest

function Get-RepoRoot {
    $current = (Resolve-Path -LiteralPath (Get-Location).Path).Path

    while ($true) {
        if (Test-Path -LiteralPath (Join-Path $current ".git") -PathType Container) {
            return $current
        }

        $parent = Split-Path -Path $current -Parent
        if ([string]::IsNullOrWhiteSpace($parent) -or $parent -eq $current) {
            throw "Nao encontrei a raiz do repositorio a partir de $current."
        }

        $current = $parent
    }
}

function Write-Info {
    param(
        [Parameter(Mandatory = $true)]
        [string] $Message
    )

    Write-Host "[ragcreator] $Message"
}

function Assert-RequiredPath {
    param(
        [Parameter(Mandatory = $true)]
        [string] $RepoRoot,

        [Parameter(Mandatory = $true)]
        [string] $RelativePath,

        [ValidateSet("Any", "Container", "Leaf")]
        [string] $PathType = "Any"
    )

    $fullPath = Join-Path $RepoRoot $RelativePath
    $exists = switch ($PathType) {
        "Any" { Test-Path -LiteralPath $fullPath }
        "Container" { Test-Path -LiteralPath $fullPath -PathType Container }
        "Leaf" { Test-Path -LiteralPath $fullPath -PathType Leaf }
    }

    if (-not $exists) {
        throw "Caminho obrigatorio ausente: $RelativePath"
    }
}

function Stop-Placeholder {
    param(
        [Parameter(Mandatory = $true)]
        [string] $CommandName,

        [Parameter(Mandatory = $true)]
        [string] $PlannedBranch,

        [Parameter(Mandatory = $true)]
        [string] $Reason
    )

    [Console]::Error.WriteLine("[ragcreator] $CommandName ainda nao esta implementado.")
    [Console]::Error.WriteLine("[ragcreator] $Reason")
    [Console]::Error.WriteLine("[ragcreator] Branch planejada: $PlannedBranch")
    exit 2
}

