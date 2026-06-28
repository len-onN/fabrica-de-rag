package local.fabricarag.core.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ingest_run_logs")
public class IngestRunLog {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "run_id")
    private UUID runId;

    @Column(name = "step_id")
    private UUID stepId;

    @Column(name = "level")
    private String level;

    @Column(name = "message")
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected IngestRunLog() {
    }

    public IngestRunLog(UUID id, UUID runId, UUID stepId, String level, String message, Map<String, Object> details) {
        this.id = id;
        this.runId = runId;
        this.stepId = stepId;
        this.level = level;
        this.message = message;
        this.details = details;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getRunId() { return runId; }
    public UUID getStepId() { return stepId; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
