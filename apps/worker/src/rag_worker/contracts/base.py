"""Base contracts for all worker endpoints."""

from typing import Any

from pydantic import BaseModel, Field


class WorkerErrorDetail(BaseModel):
    """Standardized error details for worker responses."""
    code: str = Field(..., description="Machine-readable error code")
    message: str = Field(..., description="Human-readable error message")
    retryable: bool = Field(..., description="Whether the operation can be retried")
    safeDetails: dict[str, Any] | None = Field(
        default=None, description="Additional safe details without sensitive data"
    )

class WorkerErrorResponse(BaseModel):
    """Standardized error response envelope."""
    contractVersion: str = Field(default="worker.error.v1", description="Contract version")
    requestId: str = Field(..., description="Request identifier passed by the caller")
    error: WorkerErrorDetail = Field(..., description="Error details")

class WorkerBaseRequest(BaseModel):
    """Base model for worker requests."""
    contractVersion: str = Field(..., description="Expected contract version")
    requestId: str = Field(..., description="Request identifier for tracking")
    workspaceId: str = Field(..., description="Workspace ID for context")

    class Config:
        extra = "forbid"
