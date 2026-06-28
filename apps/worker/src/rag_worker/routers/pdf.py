from fastapi import APIRouter, BackgroundTasks, status
from fastapi.responses import JSONResponse
from rag_worker.contracts.pdf_inspect import PdfInspectRequest
from rag_worker.services.pdf_inspector import inspect_pdf_and_callback, inspect_pdf_sync
from rag_worker.contracts.pdf_render import PdfRenderRequest, PdfExtractTextRequest
from rag_worker.services.pdf_renderer import render_page_and_callback, render_page_sync
from rag_worker.services.pdf_text_extractor import extract_text_and_callback, extract_text_sync
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

@router.post("/render-page", status_code=status.HTTP_202_ACCEPTED)
async def render_pdf_page(request: PdfRenderRequest, background_tasks: BackgroundTasks):
    """
    Renders a single page of a PDF to an image.
    Supports asynchronous callback via `callbackUrl`.
    """
    if request.callbackUrl:
        background_tasks.add_task(render_page_and_callback, request)
        return {"message": "Render accepted", "requestId": request.requestId}
    else:
        try:
            result = render_page_sync(request)
            return JSONResponse(status_code=status.HTTP_200_OK, content=result.model_dump())
        except Exception as e:
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_render_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.render.response.v1"}
                )
            )
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content=err.model_dump())

@router.post("/extract-text", status_code=status.HTTP_202_ACCEPTED)
async def extract_pdf_text(request: PdfExtractTextRequest, background_tasks: BackgroundTasks):
    """
    Extracts text from a single page of a PDF, with optional OCR.
    Supports asynchronous callback via `callbackUrl`.
    """
    if request.callbackUrl:
        background_tasks.add_task(extract_text_and_callback, request)
        return {"message": "Extraction accepted", "requestId": request.requestId}
    else:
        try:
            result = extract_text_sync(request)
            return JSONResponse(status_code=status.HTTP_200_OK, content=result.model_dump())
        except Exception as e:
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_extract_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.extract.response.v1"}
                )
            )
            return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content=err.model_dump())
