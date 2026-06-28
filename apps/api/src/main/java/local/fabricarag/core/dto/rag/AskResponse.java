package local.fabricarag.core.dto.rag;

import java.util.List;

public record AskResponse(
        String contractVersion,
        String workspaceId,
        String collectionId,
        String queryId,
        String status,
        String answer,
        List<Citation> citations,
        List<SearchResult> retrievedChunks,
        List<ContextItemResponse> expandedContext,
        String reason,
        AskMetrics metrics
) {
    public record AskMetrics(
            int retrievedChunks,
            int contextItems,
            int estimatedTokens,
            long latencyMs
    ) {}
}
