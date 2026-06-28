package local.fabricarag.core.dto.rag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record AskRequest(
        @NotBlank String workspaceId,
        String collectionId,
        @NotBlank String query,
        @Min(1) @Max(20) Integer topK,
        String policy,
        @Min(100) @Max(10000) Integer tokenBudget,
        Map<String, Object> filters,
        Boolean includeTables,
        Boolean includeVisuals
) {
    public AskRequest {
        if (topK == null) topK = 8;
        if (policy == null) policy = "conservative_neighbors_v1";
        if (tokenBudget == null) tokenBudget = 5000;
        if (includeTables == null) includeTables = true;
        if (includeVisuals == null) includeVisuals = false; // Visual is usually heavier, disabled by default unless explicitly asked
    }
}
