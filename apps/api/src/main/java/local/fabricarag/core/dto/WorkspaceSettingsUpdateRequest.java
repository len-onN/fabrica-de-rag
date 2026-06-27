package local.fabricarag.core.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record WorkspaceSettingsUpdateRequest(
    String name,
    String purpose,
    String slug,
    JsonNode settings
) {}
