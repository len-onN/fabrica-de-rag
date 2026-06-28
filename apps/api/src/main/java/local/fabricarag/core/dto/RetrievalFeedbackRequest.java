package local.fabricarag.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RetrievalFeedbackRequest(
        @NotNull UUID collectionId,
        String documentId,
        String chunkId,
        String queryEventId,
        @NotBlank String feedbackType,
        String note
) {
}
