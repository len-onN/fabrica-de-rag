package local.fabricarag.core.port.out;

import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.domain.ChunkEmbedding;
import local.fabricarag.core.domain.Document;
import local.fabricarag.core.domain.KnowledgeCollection;
import local.fabricarag.core.domain.VectorIndexBinding;

import java.util.List;

public interface VectorStorePort {
    void upsert(ChunkEmbedding embedding, Chunk chunk, Document document, KnowledgeCollection collection, VectorIndexBinding binding, List<Float> vector);
    void delete(VectorIndexBinding binding, String qdrantPointId);
}
