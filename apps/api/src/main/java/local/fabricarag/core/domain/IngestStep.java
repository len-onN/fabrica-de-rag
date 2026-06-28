package local.fabricarag.core.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ingest_steps")
public class IngestStep {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "run_id")
    private UUID runId;

    @Column(name = "step_name")
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IngestStepStatus status;

    @Column(name = "attempt")
    private int attempt;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "safe_details", columnDefinition = "jsonb")
    private Map<String, Object> safeDetails;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected IngestStep() {
    }

    public IngestStep(UUID id, String publicId, UUID runId, String stepName, String idempotencyKey) {
        this.id = id;
        this.publicId = publicId;
        this.runId = runId;
        this.stepName = stepName;
        this.status = IngestStepStatus.QUEUED;
        this.attempt = 1;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void updateStatus(IngestStepStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markStarted() {
        this.status = IngestStepStatus.RUNNING;
        this.startedAt = OffsetDateTime.now();
        this.updatedAt = this.startedAt;
    }

    public void markCompleted(Map<String, Object> details) {
        this.status = IngestStepStatus.COMPLETED;
        this.finishedAt = OffsetDateTime.now();
        this.durationMs = java.time.Duration.between(this.startedAt, this.finishedAt).toMillis();
        this.safeDetails = details;
        this.updatedAt = this.finishedAt;
    }

    public void markFailed(String errorCode, String errorMessage, Map<String, Object> details) {
        this.status = IngestStepStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.safeDetails = details;
        this.finishedAt = OffsetDateTime.now();
        if (this.startedAt != null) {
            this.durationMs = java.time.Duration.between(this.startedAt, this.finishedAt).toMillis();
        }
        this.updatedAt = this.finishedAt;
    }

    public UUID getId() { return id; }
    public String getPublicId() { return publicId; }
    public UUID getRunId() { return runId; }
    public String getStepName() { return stepName; }
    public IngestStepStatus getStatus() { return status; }
    public int getAttempt() { return attempt; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public OffsetDateTime getFinishedAt() { return finishedAt; }
    public Long getDurationMs() { return durationMs; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public Map<String, Object> getSafeDetails() { return safeDetails; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
