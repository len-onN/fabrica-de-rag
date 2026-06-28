package local.fabricarag.core.dto;

import java.time.OffsetDateTime;

public record DocumentResponse(
    String publicId,
    String originalFilename,
    String mimeType,
    Long fileSizeBytes,
    String status,
    Integer pageCount,
    OffsetDateTime createdAt
) {}
