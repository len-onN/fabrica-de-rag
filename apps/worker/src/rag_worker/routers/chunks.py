from fastapi import APIRouter, BackgroundTasks, status
from fastapi.responses import JSONResponse

from rag_worker.contracts.chunks import ChunksBuildRequest
from rag_worker.services.chunking import build_chunks_sync, build_chunks_and_callback
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

router = APIRouter(prefix="/api/v1/chunks", tags=["chunks"])

@router.post("/build", status_code=status.HTTP_202_ACCEPTED)
async def build_chunks(request: ChunksBuildRequest, background_tasks: BackgroundTasks):
    """
    Build semantic chunks from text blocks and visual elements.
    Supports asynchronous callback via `callbackUrl`.
    """
    if request.callbackUrl:
        background_tasks.add_task(build_chunks_and_callback, request)
        return {"message": "Chunking accepted", "requestId": request.requestId}
    else:
        try:
            result = build_chunks_sync(request)
            return JSONResponse(status_code=status.HTTP_200_OK, content=result.model_dump())
        except Exception as e:
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="chunk_building_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.chunks.build.response.v1"}
                )
            )
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content=err.model_dump())
