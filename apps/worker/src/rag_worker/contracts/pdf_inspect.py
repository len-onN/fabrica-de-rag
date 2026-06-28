"""Contracts for PDF inspect endpoints."""

from typing import Optional
from pydantic import BaseModel, Field
from rag_worker.contracts.base import WorkerBaseRequest

class PdfInspectMetadata(BaseModel):
    title: Optional[str] = None
    author: Optional[str] = None

class PdfInspectRequest(WorkerBaseRequest):
    documentId: str = Field(..., description="Document identifier")
    storageUri: str = Field(..., description="URI to the PDF file in storage")
    callbackUrl: Optional[str] = Field(None, description="Webhook URL to notify when inspection completes")

class PdfInspectResponse(BaseModel):
    contractVersion: str = Field(default="worker.pdf.inspect.response.v1", description="Contract version")
    requestId: str = Field(..., description="Request identifier passed by the caller")
    documentId: str = Field(..., description="Document identifier")
    pageCount: int = Field(..., description="Number of pages in the PDF")
    encrypted: bool = Field(..., description="Whether the PDF is encrypted")
    metadata: PdfInspectMetadata = Field(default_factory=PdfInspectMetadata, description="Extracted metadata")
