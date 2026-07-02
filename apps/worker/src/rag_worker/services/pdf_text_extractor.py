"""Service for extracting text from PDF pages, with OCR fallback."""

import logging
import httpx
import pypdfium2 as pdfium
import pytesseract
import re
from pathlib import Path
from rag_worker.contracts.pdf_render import PdfExtractTextRequest, PdfExtractTextResponse
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

logger = logging.getLogger(__name__)

def extract_text_sync(request: PdfExtractTextRequest) -> PdfExtractTextResponse:
    """
    Synchronously extracts text from a PDF page, with optional OCR fallback.
    """
    logger.info(f"Extracting text from page {request.pageNumber} of document {request.documentId}")
    
    file_path = Path(request.storageUri)
    if not file_path.exists():
        raise FileNotFoundError(f"File not found at storageUri: {request.storageUri}")
    
    pdf = pdfium.PdfDocument(str(file_path))
    if request.pageNumber < 1 or request.pageNumber > len(pdf):
        raise ValueError(f"Page number {request.pageNumber} is out of bounds (1-{len(pdf)})")
    
    page_index = request.pageNumber - 1
    page = pdf[page_index]
    
    text = ""
    extraction_method = "none"
    
    # Try native text extraction first, unless forceOcr is true
    if not request.forceOcr:
        textpage = page.get_textpage()
        text = textpage.get_text_bounded() or ""
        text = text.strip()
        if text:
            extraction_method = "native"
            
    # If no text found (or forceOcr), try OCR if enabled
    if (not text or request.forceOcr) and (request.useOcr or request.forceOcr):
        logger.info(f"Using OCR for page {request.pageNumber} of document {request.documentId}")
        
        # Render page to image for OCR (300 DPI is usually good for OCR)
        scale = 300 / 72.0
        bitmap = page.render(
            scale=scale,
            rev_byteorder=False, # RGB
        )
        pil_image = bitmap.to_pil()
        
        # Run Tesseract OCR
        ocr_text = pytesseract.image_to_string(pil_image)
        text = ocr_text.strip()
        extraction_method = "ocr" if text else "none"

    return PdfExtractTextResponse(
        requestId=request.requestId,
        documentId=request.documentId,
        pageNumber=request.pageNumber,
        text=text,
        extractionMethod=extraction_method
    )

async def extract_text_and_callback(request: PdfExtractTextRequest):
    """
    Extracts text from a PDF page and sends the result or error to the callback URL.
    """
    try:
        response = extract_text_sync(request)
        
        if request.callbackUrl:
            async with httpx.AsyncClient() as client:
                await client.post(request.callbackUrl, json=response.model_dump())
                logger.info(f"Sent success callback for text extraction request {request.requestId}")
                
    except Exception as e:
        logger.error(f"Error extracting text for request {request.requestId}: {e}", exc_info=True)
        if request.callbackUrl:
            error_url = re.sub(r'(/ingest-runs/[^/]+)/.*', r'\1/error', request.callbackUrl)
            err = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_extract_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.extract.response.v1"}
                )
            )
            try:
                async with httpx.AsyncClient() as client:
                    await client.post(error_url, json=err.model_dump())
                    logger.info(f"Sent error callback for request {request.requestId}")
            except Exception as ce:
                logger.error(f"Failed to send error callback: {ce}")
