package local.fabricarag.core.repository;

import local.fabricarag.core.domain.IngestRunLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IngestRunLogRepository extends JpaRepository<IngestRunLog, UUID> {
    Page<IngestRunLog> findByRunIdOrderByCreatedAtAsc(UUID runId, Pageable pageable);
}
