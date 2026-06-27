package local.fabricarag.core.dto;

import java.time.OffsetDateTime;

public record CollectionResponse(
        String id,
        String workspaceId,
        String name,
        String description,
        String purpose,
        String status,
        String defaultIngestionProfile,
        String defaultContextPolicy,
        Long documentCount,
        Long chunkCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
