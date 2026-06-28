package local.fabricarag.core.dto.worker.components;

public class Asset {
    private String contractVersion = "pdf.asset.v1";
    private String assetId;
    private String workspaceId;
    private String documentId;
    private Integer filePageNumber;
    private String assetType;
    private String storageUri;
    private String mimeType;
    private String contentHash;
    private Integer widthPx;
    private Integer heightPx;
    private String createdFrom;
    private String retentionClass;
    private SourceLocator sourceLocator;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public Integer getFilePageNumber() { return filePageNumber; }
    public void setFilePageNumber(Integer filePageNumber) { this.filePageNumber = filePageNumber; }

    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }

    public String getStorageUri() { return storageUri; }
    public void setStorageUri(String storageUri) { this.storageUri = storageUri; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public Integer getWidthPx() { return widthPx; }
    public void setWidthPx(Integer widthPx) { this.widthPx = widthPx; }

    public Integer getHeightPx() { return heightPx; }
    public void setHeightPx(Integer heightPx) { this.heightPx = heightPx; }

    public String getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(String createdFrom) { this.createdFrom = createdFrom; }

    public String getRetentionClass() { return retentionClass; }
    public void setRetentionClass(String retentionClass) { this.retentionClass = retentionClass; }

    public SourceLocator getSourceLocator() { return sourceLocator; }
    public void setSourceLocator(SourceLocator sourceLocator) { this.sourceLocator = sourceLocator; }
}
