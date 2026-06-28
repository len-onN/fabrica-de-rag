package local.fabricarag.core.dto.rag;

import java.util.List;

public record ContextAssembleRequest(
        String workspaceId,
        String collectionId,
        List<SearchResult> anchorChunks,
        String policy, // "conservative_neighbors_v1" or "sequential_neighbors_v1"
        int neighborBefore,
        int neighborAfter,
        int tokenBudget,
        boolean includeVisuals,
        boolean includeTables
) {
}
