"""Contracts for PDF render and OCR endpoints."""

from typing import Optional
from pydantic import BaseModel, Field
from rag_worker.contracts.base import WorkerBaseRequest

class PdfRenderRequest(WorkerBaseRequest):
    documentId: str = Field(..., description="Document identifier")
    storageUri: str = Field(..., description="URI to the PDF file in storage")
    pageNumber: int = Field(..., description="Page number to render (1-based index)")
    dpi: int = Field(default=150, description="DPI resolution for rendering")
    callbackUrl: Optional[str] = Field(None, description="Webhook URL to notify when rendering completes")

class PdfRenderResponse(BaseModel):
    contractVersion: str = Field(default="worker.pdf.render.response.v1", description="Contract version")
    requestId: str = Field(..., description="Request identifier passed by the caller")
    documentId: str = Field(..., description="Document identifier")
    pageNumber: int = Field(..., description="Page number that was rendered (1-based index)")
    imageUri: str = Field(..., description="URI to the rendered image in storage")

class PdfExtractTextRequest(WorkerBaseRequest):
    documentId: str = Field(..., description="Document identifier")
    storageUri: str = Field(..., description="URI to the PDF file in storage")
    pageNumber: int = Field(..., description="Page number to extract text from (1-based index)")
    useOcr: bool = Field(default=False, description="Whether to use OCR as a fallback")
    forceOcr: bool = Field(default=False, description="Whether to force OCR, ignoring native text")
    callbackUrl: Optional[str] = Field(None, description="Webhook URL to notify when extraction completes")

class PdfExtractTextResponse(BaseModel):
    contractVersion: str = Field(default="worker.pdf.extract.response.v1", description="Contract version")
    requestId: str = Field(..., description="Request identifier passed by the caller")
    documentId: str = Field(..., description="Document identifier")
    pageNumber: int = Field(..., description="Page number extracted (1-based index)")
    text: str = Field(..., description="Extracted text")
    extractionMethod: str = Field(..., description="Method used for extraction (e.g., 'native', 'ocr', 'none')")
