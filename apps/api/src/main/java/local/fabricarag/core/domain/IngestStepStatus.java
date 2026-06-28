package local.fabricarag.core.domain;

public enum IngestStepStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    SKIPPED,
    WAITING_FOR_REVIEW,
    FAILED,
    CANCELLED
}
