package local.fabricarag.core.dto.rag;

import java.util.Map;

public record SearchRequest(
        String workspaceId,
        String collectionId,
        String query,
        Integer topK,
        Map<String, Object> filters
) {
}
