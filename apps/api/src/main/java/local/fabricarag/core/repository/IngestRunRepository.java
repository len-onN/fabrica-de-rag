package local.fabricarag.core.repository;

import local.fabricarag.core.domain.IngestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IngestRunRepository extends JpaRepository<IngestRun, UUID> {
    Optional<IngestRun> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    Optional<IngestRun> findByWorkspaceIdAndPublicId(UUID workspaceId, String publicId);
}
