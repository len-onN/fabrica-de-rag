import pytest
from fastapi.testclient import TestClient
from rag_worker.main import app

client = TestClient(app)

def test_extract_elements_sync_missing_file():
    payload = {
        "contractVersion": "worker.pdf.extract_elements.request.v1",
        "requestId": "req-123",
        "workspaceId": "ws-1",
        "documentId": "doc-1",
        "runId": "run-1",
        "storageUri": "file:///tmp/does_not_exist.pdf",
        "pages": [
            {
                "filePageNumber": 1,
                "pageAssetId": "asset-1",
                "renderStorageUri": "file:///tmp/img.png",
                "pageWidth": 800,
                "pageHeight": 1200,
                "rotation": 0
            }
        ],
        "options": {
            "detectTables": True
        }
    }
    response = client.post("/api/v1/pdf/extract-elements", json=payload)
    assert response.status_code == 400
    data = response.json()
    assert "error" in data
    assert data["error"]["code"] == "pdf_extract_elements_failed"

def test_extract_elements_async_accepted():
    payload = {
        "contractVersion": "worker.pdf.extract_elements.request.v1",
        "requestId": "req-123",
        "workspaceId": "ws-1",
        "documentId": "doc-1",
        "runId": "run-1",
        "storageUri": "file:///tmp/does_not_exist.pdf",
        "callbackUrl": "http://localhost:8080/internal/callbacks",
        "pages": [
            {
                "filePageNumber": 1,
                "pageAssetId": "asset-1",
                "renderStorageUri": "file:///tmp/img.png",
                "pageWidth": 800,
                "pageHeight": 1200,
                "rotation": 0
            }
        ]
    }
    response = client.post("/api/v1/pdf/extract-elements", json=payload)
    assert response.status_code == 202
    data = response.json()
    assert data["message"] == "Extraction accepted"
    assert data["requestId"] == "req-123"
