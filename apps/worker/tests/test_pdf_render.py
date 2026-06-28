from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient
from rag_worker.main import app

client = TestClient(app)

def test_render_pdf_page_sync(tmp_path):
    # Mock pypdfium2 behavior
    with patch("rag_worker.services.pdf_renderer.pdfium.PdfDocument") as mock_pdf_doc, \
         patch("rag_worker.services.pdf_renderer.Path.exists", return_value=True):
        
        # Setup mock PDF
        mock_pdf = MagicMock()
        mock_pdf.__len__.return_value = 2 # 2 pages
        
        mock_page = MagicMock()
        mock_bitmap = MagicMock()
        mock_pil_image = MagicMock()
        
        mock_bitmap.to_pil.return_value = mock_pil_image
        mock_page.render.return_value = mock_bitmap
        
        # pdf[0] returns mock_page
        mock_pdf.__getitem__.return_value = mock_page
        mock_pdf_doc.return_value = mock_pdf
        
        request_data = {
            "contractVersion": "worker.pdf.render.request.v1",
            "requestId": "req-123",
            "workspaceId": "ws-1",
            "documentId": "doc-1",
            "storageUri": "/tmp/test.pdf",
            "pageNumber": 1,
            "dpi": 150
        }
        
        response = client.post("/api/v1/pdf/render-page", json=request_data)
        
        assert response.status_code == 200
        data = response.json()
        assert data["documentId"] == "doc-1"
        assert data["pageNumber"] == 1
        assert "imageUri" in data

def test_render_pdf_page_async(tmp_path):
    request_data = {
        "contractVersion": "worker.pdf.render.request.v1",
        "requestId": "req-123",
        "workspaceId": "ws-1",
        "documentId": "doc-1",
        "storageUri": "/tmp/test.pdf",
        "pageNumber": 1,
        "callbackUrl": "http://example.com/callback"
    }
    
    response = client.post("/api/v1/pdf/render-page", json=request_data)
    
    assert response.status_code == 202
    assert response.json()["message"] == "Render accepted"
