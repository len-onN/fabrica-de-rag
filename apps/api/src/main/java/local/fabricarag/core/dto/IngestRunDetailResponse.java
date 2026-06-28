package local.fabricarag.core.dto;

import local.fabricarag.core.domain.IngestRunStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record IngestRunDetailResponse(
        String publicId,
        String documentPublicId,
        IngestRunStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String retryOfRunPublicId,
        String reprocessOfRunPublicId,
        List<IngestStepResponse> steps
) {}
