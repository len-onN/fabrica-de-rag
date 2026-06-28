package local.fabricarag.core.repository;

import local.fabricarag.core.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Optional<Document> findByWorkspaceIdAndId(UUID workspaceId, UUID id);
    
    Optional<Document> findByWorkspaceIdAndPublicId(UUID workspaceId, String publicId);
    
    Page<Document> findByWorkspaceIdAndKnowledgeCollectionId(UUID workspaceId, UUID knowledgeCollectionId, Pageable pageable);

    java.util.List<Document> findByPublicIdIn(java.util.List<String> publicIds);
}
