"""Health check API router."""

from fastapi import APIRouter

router = APIRouter()

@router.get("/healthz", tags=["health"])
async def liveness_probe() -> dict[str, str]:
    """Basic liveness probe."""
    return {"status": "ok"}

@router.get("/worker/v1/health", tags=["health"])
async def api_health() -> dict[str, str]:
    """API health check following v1 prefix convention."""
    return {"status": "ok", "version": "v1"}
