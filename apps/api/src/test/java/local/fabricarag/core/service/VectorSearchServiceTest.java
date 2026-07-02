package local.fabricarag.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.domain.analytics.AnalyticsEventService;
import local.fabricarag.core.dto.rag.SearchRequest;
import local.fabricarag.core.dto.rag.SearchResponse;
import local.fabricarag.core.dto.worker.EmbeddingsTextRequest;
import local.fabricarag.core.dto.worker.EmbeddingsTextResponse;
import local.fabricarag.core.port.out.VectorStorePort;
import local.fabricarag.core.port.out.VectorStoreSearchResult;
import local.fabricarag.core.repository.ChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class VectorSearchServiceTest {

    private WorkerClient workerClient;
    private VectorStorePort vectorStorePort;
    private ChunkRepository chunkRepository;
    private ObjectMapper objectMapper;
    private AnalyticsEventService analyticsEventService;
    private VectorSearchService service;

    @BeforeEach
    void setUp() {
        workerClient = Mockito.mock(WorkerClient.class);
        vectorStorePort = Mockito.mock(VectorStorePort.class);
        chunkRepository = Mockito.mock(ChunkRepository.class);
        analyticsEventService = Mockito.mock(AnalyticsEventService.class);
        objectMapper = new ObjectMapper();
        service = new VectorSearchService(workerClient, vectorStorePort, chunkRepository, objectMapper, analyticsEventService);
    }

    @Test
    void shouldPerformVectorSearchAndMapResults() {
        // Arrange
        String workspaceId = "ws-123";
        String collectionId = "col-456";
        String query = "test query";

        SearchRequest request = new SearchRequest(workspaceId, collectionId, query, 5, Map.of("contentKind", "text"));

        EmbeddingsTextResponse embedRes = new EmbeddingsTextResponse();
        EmbeddingsTextResponse.Item item = new EmbeddingsTextResponse.Item();
        item.setVector(List.of(0.1, 0.2, 0.3));
        embedRes.setItems(List.of(item));

        when(workerClient.requestEmbeddingsSync(any(EmbeddingsTextRequest.class))).thenReturn(embedRes);

        Map<String, Object> payload1 = new HashMap<>();
        payload1.put("chunkId", "chunk-1");
        
        Map<String, Object> payload2 = new HashMap<>();
        payload2.put("chunkId", "chunk-2");

        VectorStoreSearchResult result1 = new VectorStoreSearchResult("point-1", 0.9, payload1);
        VectorStoreSearchResult result2 = new VectorStoreSearchResult("point-2", 0.2, payload2); // Low score

        when(vectorStorePort.search(anyString(), anyList(), eq(5), anyMap()))
                .thenReturn(List.of(result1, result2));

        Chunk chunk1 = new Chunk(UUID.randomUUID(), "chunk-1", workspaceId, "doc-1",
                "strategy", "v1", 1, "text", "Snippet 1", null, 10, "hash1",
                "{\"filePageNumber\": 1, \"printedLabel\": \"10\"}");

        Chunk chunk2 = new Chunk(UUID.randomUUID(), "chunk-2", workspaceId, "doc-1",
                "strategy", "v1", 2, "text", "Snippet 2", null, 10, "hash2",
                "{}");

        when(chunkRepository.findByPublicIdIn(anyList())).thenReturn(List.of(chunk1, chunk2));

        // Act
        SearchResponse response = service.search(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.results().size());
        
        // Since one score is 0.2 (< 0.35), the lowConfidenceAll might be triggered if not for the size > 1 condition?
        // Wait, condition is: (bestScore < 0.35 || qdrantResults.size() < 2).
        // bestScore is 0.9, size is 2, so lowConfidenceAll should be false.
        assertFalse(response.results().get(0).lowConfidence());
        
        assertEquals("chunk-1", response.results().get(0).chunkId());
        assertEquals(0.9, response.results().get(0).score());
        assertEquals("Snippet 1", response.results().get(0).contentSnippet());
        assertEquals(1, response.results().get(0).citation().filePageNumber());
        assertEquals("10", response.results().get(0).citation().printedLabel());

        assertEquals("chunk-2", response.results().get(1).chunkId());
        assertEquals(0.2, response.results().get(1).score());
        assertNull(response.results().get(1).citation().filePageNumber());
    }
}
