"""Contracts for visual interpretation of PDF elements (VLM / LLM mocked)."""

from typing import Any, List, Optional
from pydantic import BaseModel, Field

from rag_worker.contracts.base import WorkerBaseRequest
from rag_worker.contracts.pdf_elements import SourceLocator

class PrivacyPolicy(BaseModel):
    remoteProviderEnabled: bool = False
    includeNearbyText: bool = False
    sendFullPageRender: bool = False
    sendOriginalPdf: bool = False
    historyMode: str = "metadata_only"

class VisualBudget(BaseModel):
    maxItems: int = 20
    maxImagePixelsLongestSide: int = 2048
    timeoutPerItemMs: int = 30000
    maxOutputTokensPerItem: int = 800

class VisualInterpretationItem(BaseModel):
    elementId: str
    assetId: str
    assetStorageUri: str
    elementType: str
    caption: Optional[str] = None
    detectedText: List[str] = Field(default_factory=list)
    sourceLocator: SourceLocator

class PdfInterpretVisualRequest(WorkerBaseRequest):
    documentId: str
    runId: str
    callbackUrl: Optional[str] = Field(None, description="Webhook URL to notify when interpretation completes")
    language: str = "pt-BR"
    provider: str = "mock-vision-interpreter"
    model: str = "mock-vision-interpreter-v1"
    promptVersion: str = "visual_interpreter_pdf_v1"
    privacyPolicy: PrivacyPolicy = Field(default_factory=PrivacyPolicy)
    budget: VisualBudget = Field(default_factory=VisualBudget)
    items: List[VisualInterpretationItem]

class VisualUsage(BaseModel):
    inputTokensEstimate: int
    outputTokensEstimate: int
    cacheHit: bool

class WorkerError(BaseModel):
    code: str
    message: str
    retryable: bool

class VisualInterpretation(BaseModel):
    contractVersion: str = "pdf.visual_interpretation.v1"
    interpretationId: str
    elementId: str
    assetId: str
    status: str
    interpretationText: Optional[str] = None
    structuredText: Optional[dict[str, Any]] = None
    provider: str
    model: str
    promptVersion: str
    inputHash: str
    confidence: float
    usage: Optional[VisualUsage] = None
    error: Optional[WorkerError] = None
    sourceLocator: SourceLocator

class VisualBudgetResult(BaseModel):
    itemsRequested: int
    itemsProcessed: int
    itemsSkipped: int
    limitHit: bool

class PdfInterpretVisualResponse(BaseModel):
    contractVersion: str = "worker.pdf.interpret_visual.response.v1"
    requestId: str
    workspaceId: str
    documentId: str
    runId: str
    interpretations: List[VisualInterpretation] = Field(default_factory=list)
    budget: VisualBudgetResult
    warnings: List[str] = Field(default_factory=list)
