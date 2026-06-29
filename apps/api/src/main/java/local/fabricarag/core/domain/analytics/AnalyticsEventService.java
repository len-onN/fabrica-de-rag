package local.fabricarag.core.domain.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class AnalyticsEventService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsEventService.class);

    private final AnalyticsEventRepository repository;

    public AnalyticsEventService(AnalyticsEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Publica evento assincronamente (evita bloquear o cliente).
     */
    @Async
    @Transactional
    public void publishEvent(AnalyticsEvent event) {
        try {
            sanitizeEvent(event);
            repository.save(event);
        } catch (Exception e) {
            logger.error("Failed to persist analytics event: {}", event.getEventName(), e);
        }
    }

    private void sanitizeEvent(AnalyticsEvent event) {
        if (event.getProperties() instanceof Map propertiesMap) {
            // Remove known sensitive properties if they leak by accident
            propertiesMap.remove("password");
            propertiesMap.remove("token");
            propertiesMap.remove("secret");
            propertiesMap.remove("authorization");
        }
    }

    @Transactional
    public void enforceRetentionPolicy(String workspaceId) {
        // Enforce time-based retention
        repository.deleteEventsOlderThan(RetentionClass.ANALYTICS, Instant.now().minus(90, ChronoUnit.DAYS));
        repository.deleteEventsOlderThan(RetentionClass.HISTORY, Instant.now().minus(30, ChronoUnit.DAYS));
        repository.deleteEventsOlderThan(RetentionClass.RUN_LOG, Instant.now().minus(30, ChronoUnit.DAYS));
        repository.deleteEventsOlderThan(RetentionClass.AUDIT_MINIMUM, Instant.now().minus(365, ChronoUnit.DAYS));

        // Enforce volume limits securely for this specific workspace
        if (workspaceId != null) {
            repository.enforceMaxVolumeLimit(workspaceId, RetentionClass.ANALYTICS.name(), 10000);
            repository.enforceMaxVolumeLimit(workspaceId, RetentionClass.HISTORY.name(), 10000);
            repository.enforceMaxVolumeLimit(workspaceId, RetentionClass.RUN_LOG.name(), 10000);
        }
    }
}
