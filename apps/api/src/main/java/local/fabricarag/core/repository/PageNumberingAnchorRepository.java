package local.fabricarag.core.repository;

import local.fabricarag.core.domain.PageNumberingAnchor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PageNumberingAnchorRepository extends JpaRepository<PageNumberingAnchor, UUID> {
    List<PageNumberingAnchor> findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(UUID workspaceId, UUID documentId);
    Optional<PageNumberingAnchor> findByWorkspaceIdAndDocumentPageId(UUID workspaceId, UUID documentPageId);
    void deleteByWorkspaceIdAndDocumentId(UUID workspaceId, UUID documentId);
}
