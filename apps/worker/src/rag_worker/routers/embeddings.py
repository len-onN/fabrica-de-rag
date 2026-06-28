from fastapi import APIRouter, BackgroundTasks, status
from fastapi.responses import JSONResponse
from rag_worker.contracts.embeddings import EmbeddingsTextRequest
from rag_worker.services.embeddings import generate_embeddings_and_callback, generate_embeddings_sync
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

router = APIRouter(prefix="/api/v1/embeddings", tags=["embeddings"])

@router.post("/text", status_code=status.HTTP_202_ACCEPTED)
async def embed_text(request: EmbeddingsTextRequest, background_tasks: BackgroundTasks):
    """
    Generates text embeddings for a batch of chunks.
    Supports asynchronous callback via `callbackUrl`.
    """
    if request.callbackUrl:
        background_tasks.add_task(generate_embeddings_and_callback, request, request.callbackUrl)
        return {"message": "Embedding generation accepted", "requestId": request.requestId}
    else:
        try:
            result = generate_embeddings_sync(request)
            return JSONResponse(status_code=status.HTTP_200_OK, content=result.model_dump())
        except Exception as e:
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="embeddings_text_failed",
                    message=str(e),
                    retryable=True,
                    safeDetails={"workerContractVersion": "worker.embeddings.text.response.v1"}
                )
            )
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content=err.model_dump())
