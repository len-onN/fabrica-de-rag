package local.fabricarag.core.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiKeyCreateRequest(
    OffsetDateTime expiresAt,
    List<String> capabilities
) {}
