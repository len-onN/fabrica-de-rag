from fastapi import APIRouter, BackgroundTasks, status
from fastapi.responses import JSONResponse
from rag_worker.contracts.pdf_inspect import PdfInspectRequest
from rag_worker.services.pdf_inspector import inspect_pdf_and_callback, inspect_pdf_sync
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

router = APIRouter(prefix="/api/v1/pdf", tags=["pdf"])

@router.post("/inspect", status_code=status.HTTP_202_ACCEPTED)
async def inspect_pdf(request: PdfInspectRequest, background_tasks: BackgroundTasks):
    """
    Inspects a PDF document to extract basic metadata and page count.
    Supports asynchronous callback via `callbackUrl`.
    """
    if request.callbackUrl:
        # Async mode: return 202 Accepted and run in background
        background_tasks.add_task(inspect_pdf_and_callback, request)
        return {"message": "Inspection accepted", "requestId": request.requestId}
    else:
        # Sync mode for testing/simplicity if callbackUrl is not provided
        try:
            result = inspect_pdf_sync(request)
            return JSONResponse(status_code=status.HTTP_200_OK, content=result.model_dump())
        except Exception as e:
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_inspection_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.inspect.response.v1"}
                )
            )
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content=err.model_dump())
