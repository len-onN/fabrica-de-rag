package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chunk_embeddings")
public class ChunkEmbedding {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "chunk_id", nullable = false)
    private UUID chunkId;

    @Column(name = "vector_binding_id", nullable = false)
    private UUID vectorBindingId;

    @Column(name = "embedding_model", nullable = false)
    private String embeddingModel;

    @Column(name = "embedding_model_version", nullable = false)
    private String embeddingModelVersion;

    @Column(name = "dimension", nullable = false)
    private Integer dimension;

    @Column(name = "distance_metric", nullable = false)
    private String distanceMetric;

    @Column(name = "vector_space", nullable = false)
    private String vectorSpace;

    @Column(name = "qdrant_point_id", nullable = false)
    private String qdrantPointId;

    @Column(name = "payload_contract_version", nullable = false)
    private String payloadContractVersion;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected ChunkEmbedding() {}

    public ChunkEmbedding(UUID id, String publicId, String workspaceId, UUID chunkId, UUID vectorBindingId,
                          String embeddingModel, String embeddingModelVersion, Integer dimension,
                          String distanceMetric, String vectorSpace, String qdrantPointId,
                          String payloadContractVersion, String status) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.chunkId = chunkId;
        this.vectorBindingId = vectorBindingId;
        this.embeddingModel = embeddingModel;
        this.embeddingModelVersion = embeddingModelVersion;
        this.dimension = dimension;
        this.distanceMetric = distanceMetric;
        this.vectorSpace = vectorSpace;
        this.qdrantPointId = qdrantPointId;
        this.payloadContractVersion = payloadContractVersion;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public String getPublicId() { return publicId; }
    public String getWorkspaceId() { return workspaceId; }
    public UUID getChunkId() { return chunkId; }
    public UUID getVectorBindingId() { return vectorBindingId; }
    public String getEmbeddingModel() { return embeddingModel; }
    public String getEmbeddingModelVersion() { return embeddingModelVersion; }
    public Integer getDimension() { return dimension; }
    public String getDistanceMetric() { return distanceMetric; }
    public String getVectorSpace() { return vectorSpace; }
    public String getQdrantPointId() { return qdrantPointId; }
    public String getPayloadContractVersion() { return payloadContractVersion; }
    public String getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
