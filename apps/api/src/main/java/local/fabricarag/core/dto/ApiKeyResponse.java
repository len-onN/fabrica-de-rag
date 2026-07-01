package local.fabricarag.core.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ApiKeyResponse(
    UUID id,
    String publicId,
    List<String> capabilities,
    OffsetDateTime expiresAt,
    OffsetDateTime revokedAt,
    OffsetDateTime createdAt
) {}
