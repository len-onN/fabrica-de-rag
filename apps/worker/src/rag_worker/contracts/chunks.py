from typing import List, Optional, Any
from pydantic import BaseModel, Field

from rag_worker.contracts.base import WorkerBaseRequest
from rag_worker.contracts.pdf_elements import SourceLocator

class ChunkPage(BaseModel):
    id: str
    filePageNumber: int
    effectivePrintedLabel: Optional[str] = None
    includeInSearch: bool
    pageRole: str

class ChunkElement(BaseModel):
    id: str
    elementType: str
    readingOrder: int
    textContent: Optional[str] = None
    structuredContent: Optional[dict[str, Any]] = None
    sourceLocator: SourceLocator

class VisualInterpretation(BaseModel):
    id: str
    interpretationText: str
    structuredText: Optional[dict[str, Any]] = None
    sourceLocator: SourceLocator

class ChunksBuildRequest(WorkerBaseRequest):
    documentId: str
    chunkingStrategy: str
    chunkingVersion: str
    profile: str
    targetTokens: int
    overlapTokens: int
    pages: List[ChunkPage] = Field(default_factory=list)
    elements: List[ChunkElement] = Field(default_factory=list)
    visualInterpretations: List[VisualInterpretation] = Field(default_factory=list)
    callbackUrl: Optional[str] = None

class Chunk(BaseModel):
    chunkId: str
    sequenceNumber: int
    contentKind: str
    content: str
    headingPath: List[str] = Field(default_factory=list)
    tokenCount: int
    sourceHash: str
    sourceLocator: SourceLocator

class ChunkRelation(BaseModel):
    fromChunkId: str
    toChunkId: str
    relationType: str
    weight: Optional[float] = None

class ChunkMetrics(BaseModel):
    inputBlocks: int
    chunksCreated: int
    estimatedTokens: int

class ChunksBuildResponse(BaseModel):
    contractVersion: str = "worker.chunks.build.response.v1"
    requestId: str
    workspaceId: str
    documentId: str
    chunkingStrategy: str
    chunkingVersion: str
    chunks: List[Chunk] = Field(default_factory=list)
    relations: List[ChunkRelation] = Field(default_factory=list)
    metrics: ChunkMetrics
