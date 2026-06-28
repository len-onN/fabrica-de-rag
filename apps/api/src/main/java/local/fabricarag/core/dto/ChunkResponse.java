package local.fabricarag.core.dto;

import java.time.OffsetDateTime;

public record ChunkResponse(
        String id,
        String publicId,
        String documentId,
        Integer sequenceNumber,
        String contentKind,
        String content,
        String headingPath,
        Integer tokenCount,
        String sourceLocator,
        String embeddingStatus,
        OffsetDateTime createdAt
) {
}
