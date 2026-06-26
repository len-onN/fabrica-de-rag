from fastapi.testclient import TestClient

from rag_worker.main import app

client = TestClient(app)

def test_liveness_probe():
    response = client.get("/healthz")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

def test_api_health():
    response = client.get("/worker/v1/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok", "version": "v1"}
