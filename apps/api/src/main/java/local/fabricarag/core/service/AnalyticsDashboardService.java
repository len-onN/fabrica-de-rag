package local.fabricarag.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.domain.analytics.AnalyticsEvent;
import local.fabricarag.core.domain.analytics.AnalyticsEventRepository;
import local.fabricarag.core.dto.DashboardSummaryRequest;
import local.fabricarag.core.dto.DashboardSummaryResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsDashboardService {

    private final AnalyticsEventRepository analyticsEventRepository;
    private final ObjectMapper objectMapper;

    public AnalyticsDashboardService(AnalyticsEventRepository analyticsEventRepository, ObjectMapper objectMapper) {
        this.analyticsEventRepository = analyticsEventRepository;
        this.objectMapper = objectMapper;
    }

    public DashboardSummaryResponse getDashboardSummary(String workspaceId, DashboardSummaryRequest request) {
        Instant from = request.from() != null ? request.from() : Instant.now().minus(java.time.Duration.ofDays(7));
        Instant to = request.to() != null ? request.to() : Instant.now();
        String preset = request.preset() != null ? request.preset() : "7d";

        List<AnalyticsEvent> events = analyticsEventRepository.findByWorkspaceIdAndOccurredAtBetween(workspaceId, from, to);

        // Apply in-memory filters
        if (request.origins() != null && !request.origins().isEmpty()) {
            events = events.stream()
                    .filter(e -> request.origins().contains(e.getOrigin().name().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (request.collectionId() != null && !request.collectionId().isBlank()) {
            events = events.stream()
                    .filter(e -> {
                        if ("collection".equals(e.getResourceType()) && request.collectionId().equals(e.getResourceId())) {
                            return true;
                        }
                        Map<String, Object> props = parseProperties(e);
                        return request.collectionId().equals(props.get("collectionId"));
                    })
                    .collect(Collectors.toList());
        }

        boolean empty = events.isEmpty();

        // Ingestion
        long runsStarted = countEvents(events, "ingest_run_started");
        long runsCompleted = countEvents(events, "ingest_run_completed");
        long runsFailed = countEvents(events, "ingest_run_failed");
        long runsWaiting = countEvents(events, "ingest_run_waiting_for_review");
        long medianDurationMs = calculateMedianDuration(events, "ingest_run_completed");

        // Retrieval
        long queriesCount = countEvents(events, "retrieval_query_executed");
        long lowConfCount = countBooleanProperty(events, "retrieval_query_executed", "lowConfidence");
        long noResultsCount = events.stream()
                .filter(e -> "retrieval_query_executed".equals(e.getEventName()))
                .filter(e -> getLongProperty(e, "retrievedChunksCount") == 0)
                .count();
        double avgChunks = calculateAverage(events, "retrieval_query_executed", "retrievedChunksCount");
        long contextBudgetHitCount = countEvents(events, "context_budget_limit_hit");

        // Answers
        long generatedCount = countEvents(events, "retrieval_answer_generated");
        long insufficientEvidenceCount = countEvents(events, "retrieval_answer_insufficient_evidence");
        double avgCitations = calculateAverage(events, "retrieval_answer_generated", "citationsCount");
        long usefulFeedbackCount = countEvents(events, "retrieval_answer_marked_useful") + countEvents(events, "retrieval_result_marked_relevant");
        long notUsefulFeedbackCount = countEvents(events, "retrieval_answer_marked_not_useful") + countEvents(events, "retrieval_result_marked_irrelevant");

        // API and MCP
        long apiCallsCount = countEvents(events, "api_rag_query_executed");
        long mcpCallsCount = countEvents(events, "mcp_tool_invoked");
        long limitHitCount = countEvents(events, "api_rate_limit_hit") + countEvents(events, "mcp_context_budget_exceeded");
        long loopDetectedCount = countEvents(events, "mcp_agent_loop_detected");

        // Top Failures
        List<DashboardSummaryResponse.TopFailure> topFailures = calculateTopFailures(events);

        return new DashboardSummaryResponse(
                "analytics.dashboard.summary.v1",
                workspaceId,
                new DashboardSummaryResponse.DashboardRange(preset, from, to),
                new DashboardSummaryResponse.DashboardFilters(request.collectionId(), request.origins() != null ? request.origins() : Collections.emptyList()),
                new DashboardSummaryResponse.IngestionMetrics(runsStarted, runsCompleted, runsFailed, runsWaiting, medianDurationMs),
                new DashboardSummaryResponse.RetrievalMetrics(queriesCount, lowConfCount, noResultsCount, avgChunks, contextBudgetHitCount),
                new DashboardSummaryResponse.AnswerMetrics(generatedCount, insufficientEvidenceCount, avgCitations, usefulFeedbackCount, notUsefulFeedbackCount),
                new DashboardSummaryResponse.ApiAndMcpMetrics(apiCallsCount, mcpCallsCount, limitHitCount, loopDetectedCount),
                topFailures,
                empty
        );
    }

    private Map<String, Object> parseProperties(AnalyticsEvent event) {
        if (event.getProperties() == null) return Collections.emptyMap();
        try {
            if (event.getProperties() instanceof Map) {
                return (Map<String, Object>) event.getProperties();
            }
            if (event.getProperties() instanceof String) {
                return objectMapper.readValue((String) event.getProperties(), new TypeReference<Map<String, Object>>() {});
            }
            return objectMapper.convertValue(event.getProperties(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private long countEvents(List<AnalyticsEvent> events, String eventName) {
        return events.stream().filter(e -> eventName.equals(e.getEventName())).count();
    }

    private long countBooleanProperty(List<AnalyticsEvent> events, String eventName, String propertyName) {
        return events.stream()
                .filter(e -> eventName.equals(e.getEventName()))
                .filter(e -> {
                    Object val = parseProperties(e).get(propertyName);
                    return Boolean.TRUE.equals(val);
                })
                .count();
    }

    private long getLongProperty(AnalyticsEvent event, String propertyName) {
        Object val = parseProperties(event).get(propertyName);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return 0;
    }

    private double calculateAverage(List<AnalyticsEvent> events, String eventName, String propertyName) {
        return events.stream()
                .filter(e -> eventName.equals(e.getEventName()))
                .mapToLong(e -> getLongProperty(e, propertyName))
                .average()
                .orElse(0.0);
    }

    private long calculateMedianDuration(List<AnalyticsEvent> events, String eventName) {
        long[] durations = events.stream()
                .filter(e -> eventName.equals(e.getEventName()))
                .mapToLong(e -> getLongProperty(e, "durationMs"))
                .sorted()
                .toArray();
        if (durations.length == 0) return 0;
        if (durations.length % 2 == 0) {
            return (durations[durations.length / 2 - 1] + durations[durations.length / 2]) / 2;
        } else {
            return durations[durations.length / 2];
        }
    }

    private List<DashboardSummaryResponse.TopFailure> calculateTopFailures(List<AnalyticsEvent> events) {
        Map<String, Long> failureCounts = new HashMap<>();
        
        events.stream()
                .filter(e -> e.getEventName().endsWith("_failed"))
                .forEach(e -> {
                    Map<String, Object> props = parseProperties(e);
                    String stageName = (String) props.getOrDefault("stageName", "unknown");
                    String errorCode = (String) props.getOrDefault("errorCode", "unknown_error");
                    String key = stageName + "::" + errorCode;
                    failureCounts.put(key, failureCounts.getOrDefault(key, 0L) + 1);
                });

        return failureCounts.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("::");
                    return new DashboardSummaryResponse.TopFailure(parts[0], parts[1], entry.getValue());
                })
                .sorted(Comparator.comparing(DashboardSummaryResponse.TopFailure::count).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
