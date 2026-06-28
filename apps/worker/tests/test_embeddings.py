import json
import pytest
from fastapi.testclient import TestClient
from rag_worker.main import app

client = TestClient(app)

def test_embeddings_text_sync():
    # We call it without callbackUrl so it executes synchronously
    request_data = {
        "contractVersion": "worker.embeddings.text.request.v1",
        "requestId": "req_123",
        "workspaceId": "wsp_abc",
        "items": [
            {
                "itemId": "chk_1",
                "content": "test text A",
                "contentKind": "text",
                "sourceHash": "hashA"
            },
            {
                "itemId": "chk_2",
                "content": "test text B",
                "contentKind": "text",
                "sourceHash": "hashB"
            }
        ],
        "embeddingModel": "mock-text-embedding",
        "vectorSpace": "text_chunks_v1"
    }

    response = client.post("/api/v1/embeddings/text", json=request_data)
    
    assert response.status_code == 200
    data = response.json()
    assert data["contractVersion"] == "worker.embeddings.text.response.v1"
    assert data["requestId"] == "req_123"
    assert data["dimension"] == 16
    assert len(data["items"]) == 2

    # Check that vectors have the right dimension
    vector1 = data["items"][0]["vector"]
    assert len(vector1) == 16

def test_embeddings_mock_is_deterministic():
    request_data = {
        "contractVersion": "worker.embeddings.text.request.v1",
        "requestId": "req_123",
        "workspaceId": "wsp_abc",
        "items": [
            {
                "itemId": "chk_1",
                "content": "test text A",
                "contentKind": "text",
                "sourceHash": "same_hash"
            }
        ],
        "embeddingModel": "mock-text-embedding",
        "vectorSpace": "text_chunks_v1"
    }

    resp1 = client.post("/api/v1/embeddings/text", json=request_data)
    resp2 = client.post("/api/v1/embeddings/text", json=request_data)
    
    data1 = resp1.json()
    data2 = resp2.json()
    
    # Should yield exact same vectors
    assert data1["items"][0]["vector"] == data2["items"][0]["vector"]
