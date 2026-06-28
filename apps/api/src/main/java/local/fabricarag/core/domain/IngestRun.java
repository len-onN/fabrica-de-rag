package local.fabricarag.core.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ingest_runs")
@SQLRestriction("deleted_at IS NULL")
public class IngestRun {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "document_id")
    private UUID documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IngestRunStatus status;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "retry_of_run_id")
    private UUID retryOfRunId;

    @Column(name = "reprocess_of_run_id")
    private UUID reprocessOfRunId;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    protected IngestRun() {
    }

    public IngestRun(UUID id, String publicId, UUID workspaceId, UUID documentId, 
                     String idempotencyKey, UUID createdByUserId,
                     UUID retryOfRunId, UUID reprocessOfRunId) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.documentId = documentId;
        this.status = IngestRunStatus.QUEUED;
        this.idempotencyKey = idempotencyKey;
        this.createdByUserId = createdByUserId;
        this.retryOfRunId = retryOfRunId;
        this.reprocessOfRunId = reprocessOfRunId;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public IngestRun(UUID id, String publicId, UUID workspaceId, UUID documentId, 
                     String idempotencyKey, UUID createdByUserId) {
        this(id, publicId, workspaceId, documentId, idempotencyKey, createdByUserId, null, null);
    }

    public void updateStatus(IngestRunStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = OffsetDateTime.now();
    }

    public void delete() {
        this.deletedAt = OffsetDateTime.now();
        this.updatedAt = this.deletedAt;
    }

    public UUID getId() { return id; }
    public String getPublicId() { return publicId; }
    public UUID getWorkspaceId() { return workspaceId; }
    public UUID getDocumentId() { return documentId; }
    public IngestRunStatus getStatus() { return status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getRetryOfRunId() { return retryOfRunId; }
    public UUID getReprocessOfRunId() { return reprocessOfRunId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
}
