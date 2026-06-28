package local.fabricarag.core.dto.worker;

import java.util.List;

public class EmbeddingsTextRequest {
    private String contractVersion = "worker.embeddings.text.request.v1";
    private String requestId;
    private String workspaceId;
    private String callbackUrl;
    private List<Item> items;
    private String embeddingModel;
    private String vectorSpace;
    private Integer timeoutMs = 10000;
    private Integer batchSize = 32;

    public static class Item {
        private String itemId;
        private String content;
        private String contentKind;
        private String sourceHash;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getContentKind() { return contentKind; }
        public void setContentKind(String contentKind) { this.contentKind = contentKind; }

        public String getSourceHash() { return sourceHash; }
        public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }
    }

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }

    public String getVectorSpace() { return vectorSpace; }
    public void setVectorSpace(String vectorSpace) { this.vectorSpace = vectorSpace; }

    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
}
