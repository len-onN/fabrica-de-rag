package local.fabricarag.core.repository;

import local.fabricarag.core.domain.KnowledgeCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeCollectionRepository extends JpaRepository<KnowledgeCollection, UUID> {
    
    Optional<KnowledgeCollection> findByWorkspaceIdAndId(UUID workspaceId, UUID id);
    
    Optional<KnowledgeCollection> findByWorkspaceIdAndPublicId(UUID workspaceId, String publicId);

    Page<KnowledgeCollection> findByWorkspaceId(UUID workspaceId, Pageable pageable);
}
