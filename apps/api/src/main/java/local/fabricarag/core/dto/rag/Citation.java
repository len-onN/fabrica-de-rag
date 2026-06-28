package local.fabricarag.core.dto.rag;

import java.util.Map;

public record Citation(
        String contractVersion,
        String citationId,
        String documentId,
        String chunkId,
        String sourceElementId,
        String assetId,
        String sourceType,
        Integer filePageNumber,
        String printedLabel,
        String sourceLabel,
        String quote,
        String contentKind,
        boolean previewAvailable,
        boolean fallbackTextAvailable,
        Map<String, Object> sourceLocator
) {
}
