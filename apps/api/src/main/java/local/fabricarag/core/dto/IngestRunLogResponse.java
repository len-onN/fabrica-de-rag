package local.fabricarag.core.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record IngestRunLogResponse(
        String level,
        String message,
        Map<String, Object> details,
        OffsetDateTime createdAt,
        String stepPublicId
) {}
