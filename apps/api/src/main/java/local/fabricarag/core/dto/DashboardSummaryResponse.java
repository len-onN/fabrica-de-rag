package local.fabricarag.core.dto;

public record DashboardSummaryResponse(
    String schemaVersion,
    String workspaceId,
    long collectionCount,
    long documentCount,
    long activeRunsCount
) {}
