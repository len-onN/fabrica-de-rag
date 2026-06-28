package local.fabricarag.core.adapter.out.qdrant;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.domain.ChunkEmbedding;
import local.fabricarag.core.domain.Document;
import local.fabricarag.core.domain.KnowledgeCollection;
import local.fabricarag.core.domain.VectorIndexBinding;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.qdrant.QdrantContainer;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Testcontainers
class QdrantVectorStoreAdapterIT {

    @Container
    private static final QdrantContainer qdrant = new QdrantContainer("qdrant/qdrant:v1.18.2");

    private static QdrantClient qdrantClient;
    private static QdrantVectorStoreAdapter adapter;

    @BeforeAll
    static void setUp() throws Exception {
        qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(qdrant.getHost(), qdrant.getGrpcPort(), false).build());
        ObjectMapper objectMapper = new ObjectMapper();
        adapter = new QdrantVectorStoreAdapter(qdrantClient, objectMapper);

        qdrantClient.createCollectionAsync("ragcreator_chunks_v1",
                Collections.VectorParams.newBuilder().setSize(16).setDistance(Collections.Distance.Cosine).build()).get();
    }

    @Test
    void testUpsertAndDelete() {
        UUID chunkId = UUID.randomUUID();
        String pointId = UUID.nameUUIDFromBytes(chunkId.toString().getBytes()).toString();
        
        Chunk chunk = new Chunk(
            chunkId, "chk_fixture", "wsp_fixture", "doc_fixture", 
            "semantic_block", "semantic_block_v1", 1, "text", "mock content", 
            "[\"Capitulo 1\"]", 10, "sha256:123", "{\"filePageNumber\": 3, \"printedLabel\": \"2\"}"
        );
        
        ChunkEmbedding embedding = new ChunkEmbedding(
            UUID.randomUUID(), "emb_fixture", "wsp_fixture", chunkId, UUID.randomUUID(),
            "mock-text-embedding", "mock-text-embedding-v1", 16, "cosine", "text_chunks_v1", 
            pointId, "qdrant.chunk.v1", "embedded"
        );
        
        Document document = new Document(
            UUID.randomUUID(), "doc_fixture", UUID.randomUUID(), UUID.randomUUID(), 
            "pdf_upload", "storage://original.pdf", "sha256:456", "file.pdf",
            "application/pdf", 1024L, "ready", 1, "storage://file.pdf", UUID.randomUUID()
        );
        
        KnowledgeCollection collection = new KnowledgeCollection(
            UUID.randomUUID(), "col_fixture", UUID.randomUUID(), "name", "desc", "general", "ready",
            "balanced", "conservative", "mock", UUID.randomUUID()
        );
        
        VectorIndexBinding binding = new VectorIndexBinding(
            UUID.randomUUID(), "vbd_fixture", "wsp_fixture", "col_fixture", UUID.randomUUID(),
            "ragcreator_chunks_v1", null, "text_chunks_v1", "mock-text-embedding", "mock-text-embedding-v1",
            16, "cosine", "qdrant.chunk.v1", "ready"
        );
        
        List<Float> vector = List.of(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f);

        assertDoesNotThrow(() -> adapter.upsert(embedding, chunk, document, collection, binding, vector));
        
        assertDoesNotThrow(() -> adapter.delete(binding, pointId));
    }
}
