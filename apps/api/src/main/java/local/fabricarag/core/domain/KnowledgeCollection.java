package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "knowledge_collections")
@SQLRestriction("deleted_at IS NULL")
public class KnowledgeCollection {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "status")
    private String status;

    @Column(name = "default_ingestion_profile")
    private String defaultIngestionProfile;

    @Column(name = "default_context_policy")
    private String defaultContextPolicy;

    @Column(name = "default_embedding_model")
    private String defaultEmbeddingModel;

    @Column(name = "default_vector_binding_id")
    private UUID defaultVectorBindingId;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    protected KnowledgeCollection() {
    }

    public KnowledgeCollection(UUID id, String publicId, UUID workspaceId, String name, String description,
                               String purpose, String status, String defaultIngestionProfile,
                               String defaultContextPolicy, String defaultEmbeddingModel, UUID createdByUserId) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.name = name;
        this.description = description;
        this.purpose = purpose;
        this.status = status;
        this.defaultIngestionProfile = defaultIngestionProfile;
        this.defaultContextPolicy = defaultContextPolicy;
        this.defaultEmbeddingModel = defaultEmbeddingModel;
        this.createdByUserId = createdByUserId;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String name, String description, String purpose, String defaultIngestionProfile, String defaultContextPolicy) {
        this.name = name;
        this.description = description;
        this.purpose = purpose;
        this.defaultIngestionProfile = defaultIngestionProfile;
        this.defaultContextPolicy = defaultContextPolicy;
        this.updatedAt = OffsetDateTime.now();
    }

    public void delete() {
        this.deletedAt = OffsetDateTime.now();
        this.updatedAt = this.deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

    public String getDefaultIngestionProfile() {
        return defaultIngestionProfile;
    }

    public String getDefaultContextPolicy() {
        return defaultContextPolicy;
    }

    public String getDefaultEmbeddingModel() {
        return defaultEmbeddingModel;
    }

    public UUID getDefaultVectorBindingId() {
        return defaultVectorBindingId;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }
}
