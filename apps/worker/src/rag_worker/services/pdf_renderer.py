"""Service for rendering PDF pages to images."""

import logging
import httpx
import re
import pypdfium2 as pdfium
from pathlib import Path
from rag_worker.contracts.pdf_render import PdfRenderRequest, PdfRenderResponse
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

logger = logging.getLogger(__name__)

def render_page_sync(request: PdfRenderRequest) -> PdfRenderResponse:
    """
    Synchronously renders a PDF page to an image.
    In MVP, storageUri is treated as a local file path inside the worker container.
    """
    logger.info(f"Rendering page {request.pageNumber} of document {request.documentId}")
    
    file_path = Path(request.storageUri)
    if not file_path.exists():
        raise FileNotFoundError(f"File not found at storageUri: {request.storageUri}")
    
    pdf = pdfium.PdfDocument(str(file_path))
    if request.pageNumber < 1 or request.pageNumber > len(pdf):
        raise ValueError(f"Page number {request.pageNumber} is out of bounds (1-{len(pdf)})")
    
    # Render the page
    # pypdfium2 uses 0-based indexing for pages
    page_index = request.pageNumber - 1
    page = pdf[page_index]
    
    # Scale based on DPI (default PDFium DPI is 72, so scale = target_dpi / 72)
    scale = request.dpi / 72.0
    
    bitmap = page.render(
        scale=scale,
        rev_byteorder=False, # RGB
    )
    pil_image = bitmap.to_pil()
    
    # Save the image
    # We will save it in the same directory as the PDF, with a _pX.png suffix
    image_path = file_path.with_name(f"{file_path.stem}_p{request.pageNumber}.png")
    pil_image.save(str(image_path), format="PNG")
    
    return PdfRenderResponse(
        requestId=request.requestId,
        documentId=request.documentId,
        pageNumber=request.pageNumber,
        imageUri=str(image_path)
    )

async def render_page_and_callback(request: PdfRenderRequest):
    """
    Renders a PDF page and sends the result or error to the callback URL.
    """
    try:
        response = render_page_sync(request)
        
        # Send successful callback
        if request.callbackUrl:
            async with httpx.AsyncClient() as client:
                await client.post(request.callbackUrl, json=response.model_dump())
                logger.info(f"Sent success callback for request {request.requestId}")
                
    except Exception as e:
        logger.error(f"Error rendering page for request {request.requestId}: {e}", exc_info=True)
        if request.callbackUrl:
            error_url = re.sub(r'(/ingest-runs/[^/]+)/.*', r'\1/error', request.callbackUrl)
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_render_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.render.response.v1"}
                )
            )
            try:
                async with httpx.AsyncClient() as client:
                    await client.post(error_url, json=err.model_dump())
                    logger.info(f"Sent error callback for request {request.requestId}")
            except Exception as ce:
                logger.error(f"Failed to send error callback: {ce}")
