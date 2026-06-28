package local.fabricarag.core.dto.rag;

import java.util.List;

public record ContextAssembleResponse(
        String contractVersion,
        String workspaceId,
        String collectionId,
        String policy,
        int tokenBudget,
        boolean budgetHit,
        List<ContextItemResponse> items,
        List<ContextItemResponse> discarded,
        List<Citation> citations,
        ContextMetrics metrics
) {
    public record ContextMetrics(
            int anchorChunks,
            int contextItems,
            int estimatedTokens
    ) {}
}
