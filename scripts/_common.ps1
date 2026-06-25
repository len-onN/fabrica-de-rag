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

function Assert-DockerComposeAvailable {
    $dockerCommand = Get-Command -Name "docker" -ErrorAction SilentlyContinue
    if ($null -eq $dockerCommand) {
        Stop-OperationalError -Message "Docker CLI nao encontrado. Instale o Docker com Compose V2 para usar o runtime local."
    }

    if (-not (Test-DockerCommand -DockerArguments @("compose", "version"))) {
        Stop-OperationalError -Message "Docker Compose V2 nao esta disponivel pelo comando 'docker compose'."
    }
}

function Assert-DockerEngineAvailable {
    if (-not (Test-DockerCommand -DockerArguments @("info"))) {
        Stop-OperationalError -Message "Docker engine nao esta acessivel. Inicie o Docker Desktop ou o daemon Docker antes de subir/encerrar o runtime local."
    }
}

function Stop-OperationalError {
    param(
        [Parameter(Mandatory = $true)]
        [string] $Message
    )

    [Console]::Error.WriteLine("[ragcreator] $Message")
    exit 2
}

function Test-DockerCommand {
    param(
        [Parameter(Mandatory = $true)]
        [string[]] $DockerArguments
    )

    $previousPreference = $ErrorActionPreference

    try {
        $ErrorActionPreference = "Continue"
        & docker @DockerArguments *> $null
        return $LASTEXITCODE -eq 0
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
}

function Get-ComposeProjectName {
    param(
        [ValidateSet("base", "dev", "e2e")]
        [string] $Profile = "dev"
    )

    switch ($Profile) {
        "base" { return "ragcreator-base" }
        "dev" { return "ragcreator-dev" }
        "e2e" { return "ragcreator-e2e" }
    }
}

function Get-ComposeFileArguments {
    param(
        [Parameter(Mandatory = $true)]
        [string] $RepoRoot,

        [ValidateSet("base", "dev", "e2e")]
        [string] $Profile = "dev"
    )

    $composeDir = Join-Path $RepoRoot "infra/compose"
    $files = @("compose.yml")

    if ($Profile -eq "dev") {
        $files += "compose.dev.yml"
    }

    if ($Profile -eq "e2e") {
        $files += "compose.e2e.yml"
    }

    $arguments = @()
    foreach ($file in $files) {
        $fullPath = Join-Path $composeDir $file
        if (-not (Test-Path -LiteralPath $fullPath -PathType Leaf)) {
            throw "Arquivo Compose obrigatorio ausente: infra/compose/$file"
        }

        $arguments += @("-f", $fullPath)
    }

    return $arguments
}

function New-DockerComposeBaseArguments {
    param(
        [Parameter(Mandatory = $true)]
        [string] $RepoRoot,

        [ValidateSet("base", "dev", "e2e")]
        [string] $Profile = "dev"
    )

    $projectName = Get-ComposeProjectName -Profile $Profile
    $fileArguments = Get-ComposeFileArguments -RepoRoot $RepoRoot -Profile $Profile

    return @("compose", "--project-name", $projectName) + $fileArguments
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
