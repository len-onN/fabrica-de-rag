"""Contracts for Text Embeddings endpoints."""

from typing import Optional
from pydantic import BaseModel, Field
from rag_worker.contracts.base import WorkerBaseRequest

class EmbeddingsTextRequestItem(BaseModel):
    itemId: str = Field(..., description="Unique identifier for the item being embedded")
    content: str = Field(..., description="The textual content to embed")
    contentKind: str = Field(..., description="Kind of content (e.g. 'text', 'visual_interpretation')")
    sourceHash: str = Field(..., description="Deterministic hash of the content")

class EmbeddingsTextRequest(WorkerBaseRequest):
    callbackUrl: Optional[str] = Field(None, description="Webhook URL to notify when processing completes")
    items: list[EmbeddingsTextRequestItem] = Field(..., description="List of items to embed")
    embeddingModel: str = Field(..., description="Name of the model to use")
    vectorSpace: str = Field(..., description="The target vector space name")
    timeoutMs: Optional[int] = Field(10000, description="Timeout in milliseconds")
    batchSize: Optional[int] = Field(32, description="Maximum items per batch")

class EmbeddingsTextResponseItem(BaseModel):
    itemId: str = Field(..., description="Identifier matching the request")
    sourceHash: str = Field(..., description="Hash matching the request")
    vector: list[float] = Field(..., description="The computed embedding vector")

class EmbeddingsTextResponse(BaseModel):
    contractVersion: str = Field(default="worker.embeddings.text.response.v1", description="Contract version")
    requestId: str = Field(..., description="Request identifier passed by the caller")
    workspaceId: str = Field(..., description="Workspace ID for context")
    embeddingModel: str = Field(..., description="Name of the model actually used")
    embeddingModelVersion: str = Field(..., description="Specific version of the model used")
    dimension: int = Field(..., description="Vector dimension")
    distanceMetric: str = Field(..., description="Metric used by this space (cosine, dot, euclid)")
    vectorSpace: str = Field(..., description="The vector space name")
    items: list[EmbeddingsTextResponseItem] = Field(..., description="List of computed vectors")
    errors: list[dict] = Field(default_factory=list, description="List of per-item errors, if any")
