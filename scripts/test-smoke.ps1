$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

. (Join-Path $PSScriptRoot "_common.ps1")

Write-Info "Iniciando Teste de Fumaca da Stack Local"

# 1. Subir stack
Write-Info "Subindo dependencias de infraestrutura via Compose (Profile: dev)..."
& (Join-Path $PSScriptRoot "compose-up.ps1") -Profile dev
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao subir o Compose. Coletando logs:"
    & docker compose --project-name ragcreator --file (Join-Path $PSScriptRoot "..\infra\compose\compose.yml") --file (Join-Path $PSScriptRoot "..\infra\compose\compose.dev.yml") logs
    exit $LASTEXITCODE
}

# 2. Validar health de servicos expostos localmente "quando existirem"
$success = $true

# Qdrant HTTP API (sempre existe no dev profile)
try {
    Write-Info "Verificando Qdrant HTTP (localhost:6333)..."
    $response = Invoke-RestMethod -Uri "http://localhost:6333/healthz" -Method Get -ErrorAction Stop
    Write-Info "Qdrant OK."
} catch {
    Write-Warning "Qdrant nao respondeu corretamente no HTTP: $_"
    $success = $false
}

# Postgres: o compose-up ja espera o healthcheck (pg_isready), 
# mas podemos checar se a porta esta acessivel se quisermos, 
# embora o fato do compose up retornar 0 signifique que o DB esta healthy.
Write-Info "Postgres OK (validado pelo healthcheck do Compose)."

# API Spring (porta padrao 8080)
try {
    $apiResp = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 2 -ErrorAction Stop
    Write-Info "API Spring OK (Rodando localmente)."
} catch {
    Write-Info "API Spring nao detectada em localhost:8080. Ignorando."
}

# Worker Python (porta padrao 8000)
try {
    $workerResp = Invoke-RestMethod -Uri "http://localhost:8000/healthz" -Method Get -TimeoutSec 2 -ErrorAction Stop
    Write-Info "Worker Python OK (Rodando localmente)."
} catch {
    Write-Info "Worker Python nao detectado em localhost:8000. Ignorando."
}

# Angular Web (porta padrao 4200)
try {
    $webResp = Invoke-WebRequest -Uri "http://localhost:4200" -Method Get -TimeoutSec 2 -ErrorAction Stop
    Write-Info "Web Angular OK (Rodando localmente)."
} catch {
    Write-Info "Web Angular nao detectada em localhost:4200. Ignorando."
}

if (-not $success) {
    Write-Error "Teste de fumaca falhou em uma ou mais verificacoes vitais."
    Write-Info "Logs do Docker Compose:"
    & docker compose --project-name ragcreator --file (Join-Path $PSScriptRoot "..\infra\compose\compose.yml") --file (Join-Path $PSScriptRoot "..\infra\compose\compose.dev.yml") logs
    exit 1
}

Write-Info "Teste de fumaca concluido com sucesso!"
exit 0
