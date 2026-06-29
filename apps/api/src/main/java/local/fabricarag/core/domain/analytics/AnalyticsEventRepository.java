package local.fabricarag.core.domain.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, UUID> {

    @Modifying
    @Query("DELETE FROM AnalyticsEvent a WHERE a.retentionClass = :retentionClass AND a.occurredAt < :cutoffDate")
    int deleteEventsOlderThan(@Param("retentionClass") RetentionClass retentionClass, @Param("cutoffDate") Instant cutoffDate);

    @Modifying
    @Query(value = "DELETE FROM analytics_events WHERE id IN (SELECT id FROM analytics_events WHERE workspace_id = :workspaceId AND retention_class = :retentionClass ORDER BY occurred_at DESC OFFSET :maxVolume)", nativeQuery = true)
    int enforceMaxVolumeLimit(@Param("workspaceId") String workspaceId, @Param("retentionClass") String retentionClass, @Param("maxVolume") int maxVolume);
}
