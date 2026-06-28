package local.fabricarag.core.repository;

import local.fabricarag.core.domain.ChunkRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChunkRelationRepository extends JpaRepository<ChunkRelation, UUID> {
}
