package local.fabricarag.core.dto.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import local.fabricarag.core.domain.analytics.ActorType;
import local.fabricarag.core.domain.analytics.EventOrigin;
import local.fabricarag.core.domain.analytics.RetentionClass;

import java.time.Instant;

public record AnalyticsEventRequest(
        @NotBlank String eventVersion,
        @NotBlank String eventName,
        @NotNull EventOrigin origin,
        @NotNull RetentionClass retentionClass,
        String workspaceId,
        AnalyticsActor actor,
        @NotBlank String correlationId,
        @NotNull Instant occurredAt,
        AnalyticsResource resource,
        @NotNull Object properties
) {
    public record AnalyticsActor(@NotNull ActorType type, String id) {}
    public record AnalyticsResource(String type, String id) {}
}
