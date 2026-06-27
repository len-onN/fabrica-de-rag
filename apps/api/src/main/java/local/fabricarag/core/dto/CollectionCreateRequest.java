package local.fabricarag.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CollectionCreateRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1000) String description,
        @NotBlank @Size(max = 50) String purpose,
        @NotBlank @Size(max = 50) String defaultIngestionProfile,
        @NotBlank @Size(max = 50) String defaultContextPolicy
) {
}
