package local.fabricarag.core.dto;

import java.time.Instant;
import java.util.List;

public record DashboardSummaryResponse(
    String contractVersion,
    String workspaceId,
    DashboardRange range,
    DashboardFilters filters,
    IngestionMetrics ingestion,
    RetrievalMetrics retrieval,
    AnswerMetrics answers,
    ApiAndMcpMetrics apiAndMcp,
    List<TopFailure> topFailures,
    boolean empty
) {
    public record DashboardRange(
        String preset,
        Instant from,
        Instant to
    ) {}

    public record DashboardFilters(
        String collectionId,
        List<String> origins
    ) {}

    public record IngestionMetrics(
        long runsStarted,
        long runsCompleted,
        long runsFailed,
        long runsWaitingForReview,
        long medianDurationMs
    ) {}

    public record RetrievalMetrics(
        long queriesCount,
        long lowConfidenceCount,
        long noResultsCount,
        double averageRetrievedChunks,
        long contextBudgetHitCount
    ) {}

    public record AnswerMetrics(
        long generatedCount,
        long insufficientEvidenceCount,
        double averageCitations,
        long usefulFeedbackCount,
        long notUsefulFeedbackCount
    ) {}

    public record ApiAndMcpMetrics(
        long apiCallsCount,
        long mcpToolCallsCount,
        long limitHitCount,
        long agentLoopDetectedCount
    ) {}

    public record TopFailure(
        String stageName,
        String errorCode,
        long count
    ) {}
}
