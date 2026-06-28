import pytest
from fastapi.testclient import TestClient
from rag_worker.main import app

client = TestClient(app)

def test_interpret_visual_sync():
    payload = {
        "contractVersion": "worker.pdf.interpret_visual.request.v1",
        "requestId": "req-abc",
        "workspaceId": "ws-1",
        "documentId": "doc-1",
        "runId": "run-1",
        "items": [
            {
                "elementId": "elem-1",
                "assetId": "asset-1",
                "assetStorageUri": "file:///tmp/asset.png",
                "elementType": "figure",
                "detectedText": ["hello", "world"],
                "sourceLocator": {
                    "sourceType": "pdf_upload",
                    "documentId": "doc-1",
                    "filePageNumber": 1,
                    "printedLabel": "1",
                    "unit": "pdf_points_top_left",
                    "pageWidth": 612.0,
                    "pageHeight": 792.0,
                    "rotation": 0,
                    "bbox": [10.0, 20.0, 30.0, 40.0],
                    "readingOrder": 1,
                    "elementId": "elem-1"
                }
            }
        ],
        "budget": {
            "maxItems": 20
        }
    }
    
    response = client.post("/api/v1/pdf/interpret-visual", json=payload)
    assert response.status_code == 200
    data = response.json()
    
    assert data["contractVersion"] == "worker.pdf.interpret_visual.response.v1"
    assert len(data["interpretations"]) == 1
    
    interp = data["interpretations"][0]
    assert interp["elementId"] == "elem-1"
    assert interp["status"] == "completed"
    assert interp["provider"] == "mock-vision-interpreter"
    
    budget = data["budget"]
    assert budget["itemsRequested"] == 1
    assert budget["itemsProcessed"] == 1
    assert budget["limitHit"] == False

def test_interpret_visual_async_accepted():
    payload = {
        "contractVersion": "worker.pdf.interpret_visual.request.v1",
        "requestId": "req-abc",
        "workspaceId": "ws-1",
        "documentId": "doc-1",
        "runId": "run-1",
        "callbackUrl": "http://localhost:8080/callback",
        "items": []
    }
    
    response = client.post("/api/v1/pdf/interpret-visual", json=payload)
    assert response.status_code == 202
    data = response.json()
    assert data["message"] == "Interpretation accepted"
