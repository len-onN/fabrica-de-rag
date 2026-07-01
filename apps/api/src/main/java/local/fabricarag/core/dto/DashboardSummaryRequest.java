package local.fabricarag.core.dto;

import java.time.Instant;
import java.util.List;

public record DashboardSummaryRequest(
    String preset,
    Instant from,
    Instant to,
    String collectionId,
    List<String> origins
) {}
