$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

$repoRoot = Get-RepoRoot

$requiredDirectories = @(
    "apps",
    "apps/api",
    "apps/web",
    "apps/worker",
    "apps/mcp",
    "infra",
    "infra/compose",
    "scripts",
    "tests",
    "tests/contracts",
    "tests/e2e",
    "tests/fixtures",
    "tests/fixtures/json",
    "tests/fixtures/pdfs"
)

foreach ($relativePath in $requiredDirectories) {
    Assert-RequiredPath -RepoRoot $repoRoot -RelativePath $relativePath -PathType "Container"
}

$requiredFiles = @(
    ".editorconfig",
    ".gitattributes",
    ".gitignore",
    ".java-version",
    ".node-version",
    ".python-version",
    "README.md",
    "apps/README.md",
    "apps/api/README.md",
    "apps/web/README.md",
    "apps/worker/README.md",
    "apps/mcp/README.md",
    "infra/README.md",
    "infra/compose/README.md",
    "scripts/README.md",
    "scripts/_common.ps1",
    "scripts/check.ps1",
    "scripts/test-unit.ps1",
    "scripts/test-integration.ps1",
    "scripts/test-smoke.ps1",
    "scripts/test-e2e.ps1",
    "scripts/compose-up.ps1",
    "scripts/compose-down.ps1",
    "tests/README.md",
    "tests/contracts/README.md",
    "tests/e2e/README.md",
    "tests/fixtures/README.md",
    "tests/fixtures/json/README.md",
    "tests/fixtures/pdfs/README.md"
)

foreach ($relativePath in $requiredFiles) {
    Assert-RequiredPath -RepoRoot $repoRoot -RelativePath $relativePath -PathType "Leaf"
}

$contractsPath = Join-Path $repoRoot "tests/contracts"
$contractFiles = @(Get-ChildItem -LiteralPath $contractsPath -File -Recurse)
if ($contractFiles.Count -eq 0) {
    throw "Nenhum contrato versionado encontrado em tests/contracts."
}

Write-Info "Estrutura raiz verificada."
Write-Info ("Contratos versionados encontrados: {0}" -f $contractFiles.Count)
Write-Info "Placeholders operacionais disponiveis em scripts/."

exit 0

