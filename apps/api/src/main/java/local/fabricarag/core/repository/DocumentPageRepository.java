package local.fabricarag.core.repository;

import local.fabricarag.core.domain.DocumentPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentPageRepository extends JpaRepository<DocumentPage, UUID> {
    List<DocumentPage> findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(UUID workspaceId, UUID documentId);
    Optional<DocumentPage> findByWorkspaceIdAndDocumentIdAndFilePageNumber(UUID workspaceId, UUID documentId, Integer filePageNumber);
    void deleteByWorkspaceIdAndDocumentId(UUID workspaceId, UUID documentId);
}
