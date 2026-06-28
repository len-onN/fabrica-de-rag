package local.fabricarag.core.domain;

public enum IngestRunStatus {
    QUEUED,
    RUNNING,
    WAITING_FOR_REVIEW,
    COMPLETED,
    FAILED,
    CANCELLED
}
