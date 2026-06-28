package local.fabricarag.core.dto.rag;

public record ContextItemResponse(
        String contextItemId,
        String chunkId,
        String rankSource, // "anchor", "direct_relation", "neighbor_before", "neighbor_after"
        String contentKind,
        String content,
        int tokenCount,
        boolean truncated,
        String citationId
) {
}
