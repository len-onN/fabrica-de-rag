from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from reportlab.platypus import SimpleDocTemplate, Paragraph, Table, TableStyle, Image
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib import colors

def generate_pdf(filename):
    doc = SimpleDocTemplate(filename, pagesize=letter)
    elements = []
    styles = getSampleStyleSheet()

    # Title
    elements.append(Paragraph("E2E Test Document", styles['Title']))

    # Text paragraph
    text = "Este é um documento de teste para a validação E2E do RAG. Ele contém texto semântico sobre a infraestrutura do RAGCreator. A Fábrica de RAG processa documentos extraindo chunks, calculando embeddings e os armazenando no Qdrant para busca vetorial e geração de respostas enriquecidas via LLM."
    elements.append(Paragraph(text, styles['Normal']))

    # A simple table
    data = [
        ["Componente", "Função"],
        ["Spring Boot", "API RAG Publica e Core"],
        ["Angular", "Interface Visual e Laboratório"],
        ["Python Worker", "Extração e Chunking Visual"]
    ]
    
    t = Table(data)
    t.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.grey),
        ('TEXTCOLOR', (0,0), (-1,0), colors.whitesmoke),
        ('ALIGN', (0,0), (-1,-1), 'CENTER'),
        ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'),
        ('BOTTOMPADDING', (0,0), (-1,0), 12),
        ('BACKGROUND', (0,1), (-1,-1), colors.beige),
        ('GRID', (0,0), (-1,-1), 1, colors.black)
    ]))
    elements.append(t)

    # Some more text
    elements.append(Paragraph("Abaixo está um diagrama simulado.", styles['Normal']))

    doc.build(elements)
    print(f"Generated {filename}")

if __name__ == '__main__':
    generate_pdf("e2e-test-doc.pdf")
