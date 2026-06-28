package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vector_index_bindings")
public class VectorIndexBinding {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "knowledge_collection_id", nullable = false)
    private String knowledgeCollectionId;

    @Column(name = "vector_connection_id", nullable = false)
    private UUID vectorConnectionId;

    @Column(name = "remote_collection_name", nullable = false)
    private String remoteCollectionName;

    @Column(name = "remote_namespace")
    private String remoteNamespace;

    @Column(name = "vector_name", nullable = false)
    private String vectorName;

    @Column(name = "embedding_model", nullable = false)
    private String embeddingModel;

    @Column(name = "embedding_model_version", nullable = false)
    private String embeddingModelVersion;

    @Column(name = "dimension", nullable = false)
    private Integer dimension;

    @Column(name = "distance_metric", nullable = false)
    private String distanceMetric;

    @Column(name = "payload_contract_version", nullable = false)
    private String payloadContractVersion;

    @Column(name = "sync_status", nullable = false)
    private String syncStatus;

    @Column(name = "last_sync_at")
    private OffsetDateTime lastSyncAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    protected VectorIndexBinding() {}

    public VectorIndexBinding(UUID id, String publicId, String workspaceId, String knowledgeCollectionId,
                              UUID vectorConnectionId, String remoteCollectionName, String remoteNamespace,
                              String vectorName, String embeddingModel, String embeddingModelVersion,
                              Integer dimension, String distanceMetric, String payloadContractVersion,
                              String syncStatus) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.knowledgeCollectionId = knowledgeCollectionId;
        this.vectorConnectionId = vectorConnectionId;
        this.remoteCollectionName = remoteCollectionName;
        this.remoteNamespace = remoteNamespace;
        this.vectorName = vectorName;
        this.embeddingModel = embeddingModel;
        this.embeddingModelVersion = embeddingModelVersion;
        this.dimension = dimension;
        this.distanceMetric = distanceMetric;
        this.payloadContractVersion = payloadContractVersion;
        this.syncStatus = syncStatus;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public String getPublicId() { return publicId; }
    public String getWorkspaceId() { return workspaceId; }
    public String getKnowledgeCollectionId() { return knowledgeCollectionId; }
    public UUID getVectorConnectionId() { return vectorConnectionId; }
    public String getRemoteCollectionName() { return remoteCollectionName; }
    public String getRemoteNamespace() { return remoteNamespace; }
    public String getVectorName() { return vectorName; }
    public String getEmbeddingModel() { return embeddingModel; }
    public String getEmbeddingModelVersion() { return embeddingModelVersion; }
    public Integer getDimension() { return dimension; }
    public String getDistanceMetric() { return distanceMetric; }
    public String getPayloadContractVersion() { return payloadContractVersion; }
    public String getSyncStatus() { return syncStatus; }
    public OffsetDateTime getLastSyncAt() { return lastSyncAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
}
