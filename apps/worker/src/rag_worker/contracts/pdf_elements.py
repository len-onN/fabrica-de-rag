"""Contracts for PDF elements extraction (tables, images)."""

from typing import Any, List, Optional
from pydantic import BaseModel, Field

from rag_worker.contracts.base import WorkerBaseRequest

class SourceLocator(BaseModel):
    sourceType: str
    documentId: str
    filePageNumber: int
    printedLabel: str
    unit: str
    pageWidth: float
    pageHeight: float
    rotation: int
    bbox: List[float]
    readingOrder: int
    elementId: str
    assetId: Optional[str] = None

class DocumentElement(BaseModel):
    contractVersion: str = "pdf.document_element.v1"
    id: str
    elementType: str
    filePageNumber: int
    bbox: List[float]
    readingOrder: int
    text: Optional[str] = None
    structuredContent: dict[str, Any]
    confidence: float
    extractionMethod: str
    relatedElementIds: List[str] = Field(default_factory=list)
    warnings: List[str] = Field(default_factory=list)
    sourceLocator: SourceLocator
    assetId: Optional[str] = None

class Asset(BaseModel):
    contractVersion: str = "pdf.asset.v1"
    assetId: str
    workspaceId: str
    documentId: str
    filePageNumber: int
    assetType: str
    storageUri: str
    mimeType: str
    contentHash: str
    widthPx: int
    heightPx: int
    createdFrom: str
    retentionClass: str
    sourceLocator: SourceLocator

class PageSummary(BaseModel):
    filePageNumber: int
    tablesCount: int
    figuresCount: int
    imagesCount: int
    captionsCount: int
    unknownCount: int
    warningsCount: int

class PageExtractContext(BaseModel):
    filePageNumber: int
    pageAssetId: str
    renderStorageUri: str
    pageWidth: float
    pageHeight: float
    rotation: int

class PdfExtractOptions(BaseModel):
    detectTables: bool = True
    detectImages: bool = True
    detectCaptions: bool = True
    includeCellBBoxes: bool = True
    renderAssetReferences: bool = True
    maxElements: int = 1000
    maxTableCells: int = 2000

class PdfExtractElementsRequest(WorkerBaseRequest):
    documentId: str
    runId: str
    storageUri: str
    callbackUrl: Optional[str] = Field(None, description="Webhook URL to notify when extraction completes")
    pages: List[PageExtractContext]
    options: PdfExtractOptions = Field(default_factory=PdfExtractOptions)

class PdfExtractElementsResponse(BaseModel):
    contractVersion: str = "worker.pdf.extract_elements.response.v1"
    requestId: str
    workspaceId: str
    documentId: str
    runId: str
    elements: List[DocumentElement] = Field(default_factory=list)
    assets: List[Asset] = Field(default_factory=list)
    pageSummaries: List[PageSummary] = Field(default_factory=list)
    warnings: List[str] = Field(default_factory=list)
