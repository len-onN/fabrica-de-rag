package local.fabricarag.core.dto.worker.components;

import java.util.List;
import java.util.Map;

public class DocumentElement {
    private String contractVersion = "pdf.document_element.v1";
    private String id;
    private String elementType;
    private Integer filePageNumber;
    private List<Double> bbox;
    private Integer readingOrder;
    private String text;
    private Map<String, Object> structuredContent;
    private Double confidence;
    private String extractionMethod;
    private List<String> relatedElementIds;
    private List<String> warnings;
    private SourceLocator sourceLocator;
    private String assetId;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getElementType() { return elementType; }
    public void setElementType(String elementType) { this.elementType = elementType; }

    public Integer getFilePageNumber() { return filePageNumber; }
    public void setFilePageNumber(Integer filePageNumber) { this.filePageNumber = filePageNumber; }

    public List<Double> getBbox() { return bbox; }
    public void setBbox(List<Double> bbox) { this.bbox = bbox; }

    public Integer getReadingOrder() { return readingOrder; }
    public void setReadingOrder(Integer readingOrder) { this.readingOrder = readingOrder; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Map<String, Object> getStructuredContent() { return structuredContent; }
    public void setStructuredContent(Map<String, Object> structuredContent) { this.structuredContent = structuredContent; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getExtractionMethod() { return extractionMethod; }
    public void setExtractionMethod(String extractionMethod) { this.extractionMethod = extractionMethod; }

    public List<String> getRelatedElementIds() { return relatedElementIds; }
    public void setRelatedElementIds(List<String> relatedElementIds) { this.relatedElementIds = relatedElementIds; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public SourceLocator getSourceLocator() { return sourceLocator; }
    public void setSourceLocator(SourceLocator sourceLocator) { this.sourceLocator = sourceLocator; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
}
