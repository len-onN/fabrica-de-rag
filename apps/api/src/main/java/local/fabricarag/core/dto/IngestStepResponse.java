package local.fabricarag.core.dto;

import local.fabricarag.core.domain.IngestStepStatus;

import java.time.OffsetDateTime;
import java.util.Map;

public record IngestStepResponse(
        String publicId,
        String stepName,
        IngestStepStatus status,
        int attempt,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        Long durationMs,
        String errorCode,
        String errorMessage,
        Map<String, Object> safeDetails
) {}
