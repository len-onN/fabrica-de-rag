package local.fabricarag.core.dto.worker.components;

import java.util.List;

public class SourceLocator {
    private String sourceType;
    private String documentId;
    private Integer filePageNumber;
    private String printedLabel;
    private String unit;
    private Double pageWidth;
    private Double pageHeight;
    private Integer rotation;
    private List<Double> bbox;
    private Integer readingOrder;
    private String elementId;
    private String assetId;

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public Integer getFilePageNumber() { return filePageNumber; }
    public void setFilePageNumber(Integer filePageNumber) { this.filePageNumber = filePageNumber; }

    public String getPrintedLabel() { return printedLabel; }
    public void setPrintedLabel(String printedLabel) { this.printedLabel = printedLabel; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getPageWidth() { return pageWidth; }
    public void setPageWidth(Double pageWidth) { this.pageWidth = pageWidth; }

    public Double getPageHeight() { return pageHeight; }
    public void setPageHeight(Double pageHeight) { this.pageHeight = pageHeight; }

    public Integer getRotation() { return rotation; }
    public void setRotation(Integer rotation) { this.rotation = rotation; }

    public List<Double> getBbox() { return bbox; }
    public void setBbox(List<Double> bbox) { this.bbox = bbox; }

    public Integer getReadingOrder() { return readingOrder; }
    public void setReadingOrder(Integer readingOrder) { this.readingOrder = readingOrder; }

    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
}
