import os
import re
import httpx
import pdfplumber
import logging

from rag_worker.contracts.pdf_elements import (
    PdfExtractElementsRequest,
    PdfExtractElementsResponse,
    PageSummary,
    DocumentElement,
    Asset,
    SourceLocator
)
from rag_worker.contracts.base import WorkerErrorResponse, WorkerErrorDetail

def resolve_file_path(storage_uri: str) -> str:
    if storage_uri.startswith("file://"):
        return storage_uri[7:]
    if storage_uri.startswith("storage://"):
        return storage_uri[10:]
    return storage_uri

async def extract_elements_and_callback(request: PdfExtractElementsRequest, callback_url: str) -> None:
    try:
        response = extract_elements_sync(request)
        async with httpx.AsyncClient() as client:
            await client.post(callback_url, json=response.model_dump())
    except Exception as e:
        if request.callbackUrl:
            error_url = re.sub(r'(/ingest-runs/[^/]+)/.*', r'\1/error', request.callbackUrl)
            error_resp = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_extract_elements_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.extract_elements.response.v1"}
                )
            )
            async with httpx.AsyncClient() as client:
                await client.post(error_url, json=error_resp.model_dump())
        else:
            error_resp = WorkerErrorResponse(
                requestId=request.requestId,
                error=WorkerErrorDetail(
                    code="pdf_extract_elements_failed",
                    message=str(e),
                    retryable=False,
                    safeDetails={"workerContractVersion": "worker.pdf.extract_elements.response.v1"}
                )
            )
            async with httpx.AsyncClient() as client:
                await client.post(callback_url, json=error_resp.model_dump())

def extract_elements_sync(request: PdfExtractElementsRequest) -> PdfExtractElementsResponse:
    file_path = resolve_file_path(request.storageUri)
    if not os.path.exists(file_path):
        raise FileNotFoundError(f"File not found: {file_path}")

    elements = []
    page_summaries = []

    # Using pdfplumber to safely open and detect basic elements
    with pdfplumber.open(file_path) as pdf:
        for page_ctx in request.pages:
            file_page_num = page_ctx.filePageNumber
            
            # pdfplumber pages are 0-indexed, filePageNumber is typically 1-indexed
            plumber_page_idx = file_page_num - 1
            if 0 <= plumber_page_idx < len(pdf.pages):
                page = pdf.pages[plumber_page_idx]
                
                tables_count = 0
                if request.options.detectTables:
                    tables = page.find_tables()
                    tables_count = len(tables) if tables else 0

                # Mock basic figures/images extraction count for base feature
                figures_count = 0
                images_count = 0
                captions_count = 0

                summary = PageSummary(
                    filePageNumber=file_page_num,
                    tablesCount=tables_count,
                    figuresCount=figures_count,
                    imagesCount=images_count,
                    captionsCount=captions_count,
                    unknownCount=0,
                    warningsCount=0
                )
                page_summaries.append(summary)
            else:
                page_summaries.append(
                    PageSummary(
                        filePageNumber=file_page_num,
                        tablesCount=0,
                        figuresCount=0,
                        imagesCount=0,
                        captionsCount=0,
                        unknownCount=0,
                        warningsCount=1
                    )
                )

    return PdfExtractElementsResponse(
        requestId=request.requestId,
        workspaceId=request.workspaceId,
        documentId=request.documentId,
        runId=request.runId,
        elements=elements,
        assets=[],
        pageSummaries=page_summaries,
        warnings=[]
    )
