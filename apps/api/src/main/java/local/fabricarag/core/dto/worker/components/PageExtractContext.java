package local.fabricarag.core.dto.worker.components;

public class PageExtractContext {
    private Integer filePageNumber;
    private String pageAssetId;
    private String renderStorageUri;
    private Double pageWidth;
    private Double pageHeight;
    private Integer rotation;

    public Integer getFilePageNumber() { return filePageNumber; }
    public void setFilePageNumber(Integer filePageNumber) { this.filePageNumber = filePageNumber; }

    public String getPageAssetId() { return pageAssetId; }
    public void setPageAssetId(String pageAssetId) { this.pageAssetId = pageAssetId; }

    public String getRenderStorageUri() { return renderStorageUri; }
    public void setRenderStorageUri(String renderStorageUri) { this.renderStorageUri = renderStorageUri; }

    public Double getPageWidth() { return pageWidth; }
    public void setPageWidth(Double pageWidth) { this.pageWidth = pageWidth; }

    public Double getPageHeight() { return pageHeight; }
    public void setPageHeight(Double pageHeight) { this.pageHeight = pageHeight; }

    public Integer getRotation() { return rotation; }
    public void setRotation(Integer rotation) { this.rotation = rotation; }
}
