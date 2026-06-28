package local.fabricarag.core.repository;

import local.fabricarag.core.domain.Chunk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
    List<Chunk> findByDocumentId(String documentId);
    Optional<Chunk> findByPublicId(String publicId);
    List<Chunk> findByPublicIdIn(List<String> publicIds);

    @Query("SELECT c FROM Chunk c " +
           "WHERE c.workspaceId = :workspaceId " +
           "AND c.documentId IN (SELECT d.publicId FROM Document d WHERE d.knowledgeCollectionId = :collectionId AND d.workspaceId = :workspaceUuid) " +
           "AND (:documentId IS NULL OR c.documentId = :documentId) " +
           "AND (:query IS NULL OR LOWER(c.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Chunk> findChunksForBrowser(
            @Param("workspaceId") String workspaceId,
            @Param("workspaceUuid") UUID workspaceUuid,
            @Param("collectionId") UUID collectionId,
            @Param("documentId") String documentId,
            @Param("query") String query,
            Pageable pageable
    );

    @Query("SELECT c FROM Chunk c WHERE c.workspaceId = :workspaceId AND c.documentId = :documentId AND c.sequenceNumber = :sequenceNumber")
    Optional<Chunk> findByWorkspaceIdAndDocumentIdAndSequenceNumber(
            @Param("workspaceId") String workspaceId,
            @Param("documentId") String documentId,
            @Param("sequenceNumber") Integer sequenceNumber
    );
}
