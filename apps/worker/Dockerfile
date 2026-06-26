# Base image for building the environment
FROM python:3.13-slim AS builder

WORKDIR /app

# Install uv for fast dependency management
RUN pip install uv==0.5.0

# Copy project files needed for installing dependencies
COPY pyproject.toml README.md ./
# Create dummy src structure to allow initial dependency resolution if needed
RUN mkdir -p src/rag_worker && touch src/rag_worker/__init__.py

# Install dependencies using uv to create a virtualenv in /app/.venv
RUN uv venv && uv pip install .

# Runner stage
FROM python:3.13-slim AS runner

WORKDIR /app

# Copy the built virtualenv
COPY --from=builder /app/.venv /app/.venv

# Copy source code
COPY src /app/src

# Make sure we use the virtualenv
ENV PATH="/app/.venv/bin:$PATH"
ENV PYTHONPATH="/app/src"

# Expose API port
EXPOSE 8000

# Run uvicorn server
CMD ["uvicorn", "rag_worker.main:app", "--host", "0.0.0.0", "--port", "8000"]
