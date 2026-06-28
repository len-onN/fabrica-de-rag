package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chunk_relations")
public class ChunkRelation {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "from_chunk_id")
    private UUID fromChunkId;

    @Column(name = "to_chunk_id")
    private UUID toChunkId;

    @Column(name = "relation_type")
    private String relationType;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected ChunkRelation() {
    }

    public ChunkRelation(UUID id, UUID fromChunkId, UUID toChunkId, String relationType, Double weight) {
        this.id = id;
        this.fromChunkId = fromChunkId;
        this.toChunkId = toChunkId;
        this.relationType = relationType;
        this.weight = weight != null ? weight : 1.0;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getFromChunkId() {
        return fromChunkId;
    }

    public UUID getToChunkId() {
        return toChunkId;
    }

    public String getRelationType() {
        return relationType;
    }

    public Double getWeight() {
        return weight;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
