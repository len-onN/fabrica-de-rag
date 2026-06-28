package local.fabricarag.core.dto.worker;

import java.util.List;

public class EmbeddingsTextResponse {
    private String contractVersion;
    private String requestId;
    private String workspaceId;
    private String embeddingModel;
    private String embeddingModelVersion;
    private Integer dimension;
    private String distanceMetric;
    private String vectorSpace;
    private List<Item> items;
    private List<Object> errors;

    public static class Item {
        private String itemId;
        private String sourceHash;
        private List<Double> vector;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }

        public String getSourceHash() { return sourceHash; }
        public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }

        public List<Double> getVector() { return vector; }
        public void setVector(List<Double> vector) { this.vector = vector; }
    }

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getEmbeddingModel() { return embeddingModel; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }

    public String getEmbeddingModelVersion() { return embeddingModelVersion; }
    public void setEmbeddingModelVersion(String embeddingModelVersion) { this.embeddingModelVersion = embeddingModelVersion; }

    public Integer getDimension() { return dimension; }
    public void setDimension(Integer dimension) { this.dimension = dimension; }

    public String getDistanceMetric() { return distanceMetric; }
    public void setDistanceMetric(String distanceMetric) { this.distanceMetric = distanceMetric; }

    public String getVectorSpace() { return vectorSpace; }
    public void setVectorSpace(String vectorSpace) { this.vectorSpace = vectorSpace; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public List<Object> getErrors() { return errors; }
    public void setErrors(List<Object> errors) { this.errors = errors; }
}
