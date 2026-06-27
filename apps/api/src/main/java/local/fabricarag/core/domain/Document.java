package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@SQLRestriction("deleted_at IS NULL")
public class Document {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "knowledge_collection_id")
    private UUID knowledgeCollectionId;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_uri")
    private String sourceUri;

    @Column(name = "source_hash")
    private String sourceHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_locator")
    private String sourceLocator;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "status")
    private String status;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "storage_uri")
    private String storageUri;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    protected Document() {
    }

    public Document(UUID id, String publicId, UUID workspaceId, UUID knowledgeCollectionId,
                    String sourceType, String sourceUri, String sourceHash, String originalFilename,
                    String mimeType, Long fileSizeBytes, String status, Integer pageCount,
                    String storageUri, UUID createdByUserId) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.knowledgeCollectionId = knowledgeCollectionId;
        this.sourceType = sourceType;
        this.sourceUri = sourceUri;
        this.sourceHash = sourceHash;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.fileSizeBytes = fileSizeBytes;
        this.status = status;
        this.pageCount = pageCount;
        this.storageUri = storageUri;
        this.createdByUserId = createdByUserId;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void updateStatus(String status) {
        this.status = status;
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

    public UUID getKnowledgeCollectionId() {
        return knowledgeCollectionId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public String getSourceHash() {
        return sourceHash;
    }

    public String getSourceLocator() {
        return sourceLocator;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public String getStatus() {
        return status;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public String getStorageUri() {
        return storageUri;
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
