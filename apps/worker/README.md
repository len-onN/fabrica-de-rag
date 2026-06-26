# RagCreator Python Worker

This is the Python worker for RagCreator. It handles heavy operations such as PDF inspection, OCR, image processing, chunking, and embeddings. 

It is orchestrated by the Spring Boot backend via an internal REST API (`/worker/v1/*`).

## Prerequisites

- [Python 3.13](https://www.python.org/downloads/)
- [uv](https://github.com/astral-sh/uv) (for ultra-fast dependency management)

## Setup

1. Sync dependencies and create the virtual environment:
   ```powershell
   uv sync
   ```

2. Activate the virtual environment (optional, since `uv run` handles this):
   ```powershell
   .venv\Scripts\activate
   ```

## Running the API locally

Run the FastAPI application via uvicorn:

```powershell
uv run uvicorn src.rag_worker.main:app --reload
```

The API will be available at `http://127.0.0.1:8000`. You can access the Swagger UI at `http://127.0.0.1:8000/docs`.

## Testing

Run the test suite with pytest:

```powershell
uv run pytest
```

## Linting and Formatting

This project uses `ruff` for fast linting and formatting.

```powershell
# Check lint errors
uv run ruff check .

# Fix lint errors automatically (where possible)
uv run ruff check --fix .
```

## Docker

Build the local image:

```powershell
docker build -t ragcreator-worker .
```

Run the container:

```powershell
docker run -p 8000:8000 ragcreator-worker
```
