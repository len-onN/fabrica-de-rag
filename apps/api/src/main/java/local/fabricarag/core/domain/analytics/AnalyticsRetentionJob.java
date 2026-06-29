package local.fabricarag.core.domain.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsRetentionJob {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsRetentionJob.class);

    private final AnalyticsEventService analyticsEventService;

    public AnalyticsRetentionJob(AnalyticsEventService analyticsEventService) {
        this.analyticsEventService = analyticsEventService;
    }

    // Run every day at 3 AM
    @Scheduled(cron = "0 0 3 * * ?")
    public void executeRetentionJob() {
        logger.info("Starting scheduled analytics retention job...");
        try {
            // A daily run across the board without filtering by workspaceId limits volume broadly
            // In a production app, we would loop over active workspaces, but for MVP, passing null applies only time retention
            analyticsEventService.enforceRetentionPolicy(null);
            logger.info("Scheduled analytics retention job completed.");
        } catch (Exception e) {
            logger.error("Failed to execute scheduled analytics retention job.", e);
        }
    }
}
