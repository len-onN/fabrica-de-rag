import os
import httpx
import re
from pypdf import PdfReader
from rag_worker.contracts.pdf_inspect import PdfInspectRequest, PdfInspectResponse, PdfInspectMetadata
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

def resolve_file_path(storage_uri: str) -> str:
    """Resolve file:// or storage:// URI to local path."""
    if storage_uri.startswith("file://"):
        return storage_uri[7:]
    if storage_uri.startswith("storage://"):
        # For local dev without absolute file://, fallback if needed
        return storage_uri[10:]
    return storage_uri

async def inspect_pdf_and_callback(request: PdfInspectRequest) -> None:
    """Inspects a PDF and sends the result to the callback URL."""
    try:
        response = inspect_pdf_sync(request)
        if request.callbackUrl:
            async with httpx.AsyncClient() as client:
                await client.post(request.callbackUrl, json=response.model_dump())
    except Exception as e:
        if request.callbackUrl:
            error_url = re.sub(r'(/ingest-runs/[^/]+)/.*', r'\1/error', request.callbackUrl)
            error_resp = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_inspection_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.inspect.response.v1"}
                )
            )
            async with httpx.AsyncClient() as client:
                await client.post(error_url, json=error_resp.model_dump())

def inspect_pdf_sync(request: PdfInspectRequest) -> PdfInspectResponse:
    """Synchronous inspection logic."""
    file_path = resolve_file_path(request.storageUri)
    
    if not os.path.exists(file_path):
        raise FileNotFoundError(f"File not found: {file_path}")
    
    with open(file_path, "rb") as f:
        reader = PdfReader(f)
        
        is_encrypted = reader.is_encrypted
        page_count = len(reader.pages)
        
        title = None
        author = None
        
        if not is_encrypted:
            meta = reader.metadata
            if meta:
                title = meta.title
                author = meta.author
                
        return PdfInspectResponse(
            requestId=request.requestId,
            documentId=request.documentId,
            pageCount=page_count,
            encrypted=is_encrypted,
            metadata=PdfInspectMetadata(
                title=title,
                author=author
            )
        )
