param(
    [string]$HostUrl = "http://localhost:6333",
    [string]$CollectionName = "ragcreator_chunks_v1",
    [int]$Dimension = 16,
    [string]$Distance = "Cosine"
)

$ErrorActionPreference = "Stop"

Write-Host "Verificando se a coleção $CollectionName existe no Qdrant em $HostUrl..."

try {
    $response = Invoke-RestMethod -Uri "$HostUrl/collections/$CollectionName" -Method Get -ErrorAction Stop
    Write-Host "Coleção $CollectionName já existe."
} catch {
    if ($_.Exception.Response.StatusCode.value__ -eq 404) {
        Write-Host "Coleção $CollectionName não existe. Criando coleção com dimensão $Dimension e métrica $Distance..."
        
        $body = @{
            vectors = @{
                size = $Dimension
                distance = $Distance
            }
        } | ConvertTo-Json -Depth 5
        
        try {
            $createResponse = Invoke-RestMethod -Uri "$HostUrl/collections/$CollectionName" -Method Put -Body $body -ContentType "application/json"
            if ($createResponse.result) {
                Write-Host "Coleção $CollectionName criada com sucesso!"
            } else {
                Write-Host "Falha ao criar coleção: $($createResponse | ConvertTo-Json)" -ForegroundColor Red
                exit 1
            }
        } catch {
            Write-Host "Erro ao criar coleção: $_" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "Não foi possível conectar ao Qdrant ou houve um erro inesperado: $_" -ForegroundColor Red
        exit 1
    }
}
