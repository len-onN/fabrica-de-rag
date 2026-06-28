package local.fabricarag.core.repository;

import local.fabricarag.core.domain.IngestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IngestStepRepository extends JpaRepository<IngestStep, UUID> {
    List<IngestStep> findByRunIdOrderByCreatedAtAsc(UUID runId);
}
