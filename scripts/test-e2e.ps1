param(
    [switch] $NoTeardown
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")
$repoRoot = Get-RepoRoot

Write-Host "--- Teste E2E do MVP ---" -ForegroundColor Cyan

# 1. Limpeza
Write-Host "Derrubando containers E2E antigos..." -ForegroundColor Yellow
$downScript = Join-Path $PSScriptRoot "compose-down.ps1"
& powershell -File $downScript -Profile e2e -RemoveVolumes

# 2. Build e Up
Write-Host "Construindo e iniciando ambiente E2E (Isolado no Docker)..." -ForegroundColor Yellow
$composeArgs = @(
    "-f", "$repoRoot/infra/compose/compose.e2e.yml",
    "--project-directory", $repoRoot,
    "-p", "ragcreator-e2e"
)

# Constroi as imagens
& docker compose @composeArgs build
if ($LASTEXITCODE -ne 0) { throw "Falha no build do docker compose" }

# Inicia os containers
& docker compose @composeArgs up -d
if ($LASTEXITCODE -ne 0) { throw "Falha ao iniciar docker compose" }

# 3. Aguardar serviços
Write-Host "Aguardando Angular Web (porta 4200)..." -ForegroundColor Yellow
$maxRetries = 30
$retryCount = 0
$webReady = $false
while (-not $webReady -and $retryCount -lt $maxRetries) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:4200" -UseBasicParsing -Method Head -ErrorAction Stop
        $webReady = $true
    } catch {
        $retryCount++
        Start-Sleep -Seconds 2
    }
}
if (-not $webReady) { throw "Serviço Web não respondeu a tempo." }

Write-Host "Aguardando API Spring Boot (porta 8080)..." -ForegroundColor Yellow
$retryCount = 0
$apiReady = $false
while (-not $apiReady -and $retryCount -lt $maxRetries) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -Method Get -ErrorAction Stop
        $apiReady = $true
    } catch {
        $retryCount++
        Start-Sleep -Seconds 2
    }
}
# A API pode não ter o actuator configurado publicamente, então se falhar, apenas damos um aviso e seguimos
if (-not $apiReady) { Write-Host "Aviso: Actuator da API não respondeu, mas vamos prosseguir." -ForegroundColor Magenta }

# Inicializa colecoes base no qdrant (se necessário)
Write-Host "Inicializando coleções do Qdrant..." -ForegroundColor Yellow
$qdrantInit = Join-Path $PSScriptRoot "qdrant-init.ps1"
& powershell -File $qdrantInit
if ($LASTEXITCODE -ne 0) { Write-Host "Aviso: script qdrant-init falhou." -ForegroundColor Magenta }

# 4. Executar Playwright
Write-Host "Iniciando testes automatizados com Playwright..." -ForegroundColor Cyan
Push-Location "$repoRoot/tests/e2e"
try {
    & npx playwright test
    $testResult = $LASTEXITCODE
} finally {
    Pop-Location
}

# 5. Teardown
if (-not $NoTeardown) {
    Write-Host "Removendo containers E2E..." -ForegroundColor Yellow
    & powershell -File $downScript -Profile e2e -RemoveVolumes
} else {
    Write-Host "Containers preservados devido a flag -NoTeardown" -ForegroundColor Yellow
}

if ($testResult -ne 0) {
    throw "Testes E2E falharam!"
} else {
    Write-Host "Todos os testes E2E passaram com sucesso!" -ForegroundColor Green
}
