from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient
from rag_worker.main import app

client = TestClient(app)

def test_extract_pdf_text_sync():
    with patch("rag_worker.services.pdf_text_extractor.pdfium.PdfDocument") as mock_pdf_doc, \
         patch("rag_worker.services.pdf_text_extractor.Path.exists", return_value=True):
        
        mock_pdf = MagicMock()
        mock_pdf.__len__.return_value = 2
        
        mock_page = MagicMock()
        mock_textpage = MagicMock()
        mock_textpage.get_text_bounded.return_value = "Hello World"
        
        mock_page.get_textpage.return_value = mock_textpage
        mock_pdf.__getitem__.return_value = mock_page
        mock_pdf_doc.return_value = mock_pdf
        
        request_data = {
            "contractVersion": "worker.pdf.extract.request.v1",
            "requestId": "req-123",
            "workspaceId": "ws-1",
            "documentId": "doc-1",
            "storageUri": "/tmp/test.pdf",
            "pageNumber": 1
        }
        
        response = client.post("/api/v1/pdf/extract-text", json=request_data)
        
        assert response.status_code == 200
        data = response.json()
        assert data["text"] == "Hello World"
        assert data["extractionMethod"] == "native"

def test_extract_pdf_text_ocr():
    with patch("rag_worker.services.pdf_text_extractor.pdfium.PdfDocument") as mock_pdf_doc, \
         patch("rag_worker.services.pdf_text_extractor.Path.exists", return_value=True), \
         patch("rag_worker.services.pdf_text_extractor.pytesseract.image_to_string", return_value="OCR Text"):
        
        mock_pdf = MagicMock()
        mock_pdf.__len__.return_value = 2
        
        mock_page = MagicMock()
        mock_textpage = MagicMock()
        mock_textpage.get_text_bounded.return_value = "" # No native text
        
        mock_bitmap = MagicMock()
        mock_pil_image = MagicMock()
        mock_bitmap.to_pil.return_value = mock_pil_image
        mock_page.render.return_value = mock_bitmap
        
        mock_page.get_textpage.return_value = mock_textpage
        mock_pdf.__getitem__.return_value = mock_page
        mock_pdf_doc.return_value = mock_pdf
        
        request_data = {
            "contractVersion": "worker.pdf.extract.request.v1",
            "requestId": "req-123",
            "workspaceId": "ws-1",
            "documentId": "doc-1",
            "storageUri": "/tmp/test.pdf",
            "pageNumber": 1,
            "useOcr": True
        }
        
        response = client.post("/api/v1/pdf/extract-text", json=request_data)
        
        assert response.status_code == 200
        data = response.json()
        assert data["text"] == "OCR Text"
        assert data["extractionMethod"] == "ocr"
