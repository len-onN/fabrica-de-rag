import pytest
from fastapi.testclient import TestClient
from rag_worker.main import app

client = TestClient(app)

def test_pdf_inspect_sync_not_found():
    """Test standard sync mode error when file does not exist."""
    response = client.post("/api/v1/pdf/inspect", json={
        "contractVersion": "worker.pdf.inspect.request.v1",
        "requestId": "req_123",
        "workspaceId": "wsp_1",
        "documentId": "doc_1",
        "storageUri": "file:///path/does/not/exist.pdf"
    })
    assert response.status_code == 400
    data = response.json()
    assert "error" in data
    assert data["error"]["code"] == "pdf_inspection_failed"

from unittest.mock import patch

def test_pdf_inspect_async_accepted():
    """Test async mode returns 202 instantly."""
    with patch("httpx.AsyncClient.post") as mock_post:
        response = client.post("/api/v1/pdf/inspect", json={
            "contractVersion": "worker.pdf.inspect.request.v1",
            "requestId": "req_123",
            "workspaceId": "wsp_1",
            "documentId": "doc_1",
            "storageUri": "file:///path/does/not/exist.pdf",
            "callbackUrl": "http://localhost:8080/callback"
        })
        # Since it runs in background, it should just accept it
        assert response.status_code == 202
        data = response.json()
        assert data["message"] == "Inspection accepted"
