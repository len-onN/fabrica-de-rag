"""Worker core application."""

from fastapi import FastAPI
from fastapi.responses import JSONResponse

from rag_worker.api import health
from rag_worker.routers import pdf


def create_app() -> FastAPI:
    app = FastAPI(
        title="RagCreator Worker API",
        description="Internal Python worker API for heavy AI and PDF tasks.",
        version="0.1.0",
    )

    # Handlers or middlewares can be added here
    @app.exception_handler(Exception)
    async def global_exception_handler(request, exc: Exception) -> JSONResponse:
        return JSONResponse(
            status_code=500,
            content={
                "contractVersion": "worker.error.v1",
                "requestId": "internal_unhandled",
                "error": {
                    "code": "internal_server_error",
                    "message": "An unexpected error occurred.",
                    "retryable": False,
                    "safeDetails": {"exception": str(exc)}
                }
            }
        )

    # Mount routers
    app.include_router(health.router)
    app.include_router(pdf.router)

    return app

app = create_app()
