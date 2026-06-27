package local.fabricarag.core.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;

public record WorkspaceSettingsResponse(
    String publicId,
    String name,
    String purpose,
    String slug,
    JsonNode settings,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
