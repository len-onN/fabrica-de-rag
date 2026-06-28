package local.fabricarag.core.repository;

import local.fabricarag.core.domain.ChunkRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChunkRelationRepository extends JpaRepository<ChunkRelation, UUID> {
    List<ChunkRelation> findByFromChunkIdInAndRelationTypeIn(List<UUID> fromChunkIds, List<String> relationTypes);
    List<ChunkRelation> findByToChunkIdInAndRelationTypeIn(List<UUID> toChunkIds, List<String> relationTypes);
}
