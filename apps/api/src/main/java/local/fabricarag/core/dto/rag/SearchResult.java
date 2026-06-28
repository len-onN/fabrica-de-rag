package local.fabricarag.core.dto.rag;

public record SearchResult(
        int rank,
        String chunkId,
        String documentId,
        double score,
        boolean lowConfidence,
        String contentKind,
        String contentSnippet,
        Citation citation
) {
}
