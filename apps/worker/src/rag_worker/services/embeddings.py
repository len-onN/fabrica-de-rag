import logging
import random
import httpx
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail
from rag_worker.contracts.embeddings import (
    EmbeddingsTextRequest,
    EmbeddingsTextResponse,
    EmbeddingsTextResponseItem,
)

logger = logging.getLogger(__name__)

def generate_embeddings_sync(request: EmbeddingsTextRequest) -> EmbeddingsTextResponse:
    logger.info(f"Generating mock embeddings for requestId={request.requestId}, items={len(request.items)}")
    
    # Simulate batch processing limitation
    batch_size = request.batchSize or 32
    items_to_process = request.items[:batch_size]

    response_items = []
    
    for item in items_to_process:
        # Seed random based on sourceHash to make it deterministic
        random.seed(item.sourceHash)
        
        # mock-text-embedding-v1 requires dimension 16
        dimension = 16
        vector = [round(random.uniform(-1.0, 1.0), 4) for _ in range(dimension)]
        
        response_items.append(
            EmbeddingsTextResponseItem(
                itemId=item.itemId,
                sourceHash=item.sourceHash,
                vector=vector
            )
        )
    
    return EmbeddingsTextResponse(
        requestId=request.requestId,
        workspaceId=request.workspaceId,
        embeddingModel="mock-text-embedding",
        embeddingModelVersion="mock-text-embedding-v1",
        dimension=16,
        distanceMetric="cosine",
        vectorSpace=request.vectorSpace,
        items=response_items,
        errors=[]
    )

async def generate_embeddings_and_callback(request: EmbeddingsTextRequest, callback_url: str):
    logger.info(f"Starting async embeddings generation for requestId={request.requestId}")
    try:
        response_payload = generate_embeddings_sync(request).model_dump()
        
        async with httpx.AsyncClient() as client:
            await client.post(callback_url, json=response_payload, timeout=30.0)
            logger.info(f"Callback successful for requestId={request.requestId}")
            
    except Exception as e:
        logger.error(f"Error generating embeddings for {request.requestId}: {str(e)}", exc_info=True)
        err = WorkerErrorResponse(
            requestId=request.requestId,
            error=WorkerErrorDetail(
                code="embedding_generation_failed",
                message=str(e),
                retryable=True,
                safeDetails={"workerContractVersion": "worker.embeddings.text.response.v1"}
            )
        )
        try:
            async with httpx.AsyncClient() as client:
                await client.post(callback_url, json=err.model_dump(), timeout=10.0)
                logger.info(f"Error callback successful for {request.requestId}")
        except Exception as ce:
            logger.error(f"Failed to deliver error callback for {request.requestId}: {str(ce)}", exc_info=True)
