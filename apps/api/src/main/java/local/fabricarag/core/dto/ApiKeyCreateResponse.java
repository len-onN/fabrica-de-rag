package local.fabricarag.core.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ApiKeyCreateResponse(
    UUID id,
    String publicId,
    String secret,
    List<String> capabilities,
    OffsetDateTime expiresAt
) {}
