package local.fabricarag.core.dto.rag;

import java.util.List;
import java.util.Map;

public record SearchResponse(
        String contractVersion,
        String workspaceId,
        String collectionId,
        String queryId,
        String query,
        String algorithm,
        Integer topK,
        Map<String, String> filtersApplied,
        List<SearchResult> results,
        SearchMetrics metrics
) {
    public record SearchMetrics(
            long qdrantLatencyMs,
            long sqlEnrichmentLatencyMs,
            long totalLatencyMs,
            int returnedResults
    ) {}
}
