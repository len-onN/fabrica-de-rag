package local.fabricarag.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.dto.rag.ContextAssembleRequest;
import local.fabricarag.core.dto.rag.ContextAssembleResponse;
import local.fabricarag.core.dto.rag.SearchResult;
import local.fabricarag.core.repository.ChunkRelationRepository;
import local.fabricarag.core.repository.ChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class ContextBuilderServiceTest {

    private ChunkRepository chunkRepository;
    private ChunkRelationRepository chunkRelationRepository;
    private ObjectMapper objectMapper;
    private ContextBuilderService contextBuilderService;

    @BeforeEach
    void setUp() {
        chunkRepository = Mockito.mock(ChunkRepository.class);
        chunkRelationRepository = Mockito.mock(ChunkRelationRepository.class);
        objectMapper = new ObjectMapper();
        contextBuilderService = new ContextBuilderService(chunkRepository, chunkRelationRepository, objectMapper);
    }

    @Test
    void testAssembleContext_conservativePolicy() {
        SearchResult anchorResult = new SearchResult(1, "chk_1", "doc_1", 0.9, false, "text", "Content 1", null);
        ContextAssembleRequest request = new ContextAssembleRequest(
                "ws_1", "col_1", List.of(anchorResult), "conservative_neighbors_v1", 0, 0, 6000, false, false);

        Chunk chunk = new Chunk(UUID.randomUUID(), "chk_1", "ws_1", "doc_1", "semantic_block", "v1", 1, "text", "Content 1", null, 10, "hash", null);

        when(chunkRepository.findByPublicIdIn(anyList())).thenReturn(List.of(chunk));

        ContextAssembleResponse response = contextBuilderService.assembleContext(request);

        assertNotNull(response);
        assertEquals(1, response.items().size());
        assertEquals("chk_1", response.items().get(0).chunkId());
        assertEquals("anchor", response.items().get(0).rankSource());
        assertFalse(response.budgetHit());
    }

    @Test
    void testAssembleContext_budgetHit() {
        SearchResult anchorResult = new SearchResult(1, "chk_1", "doc_1", 0.9, false, "text", "Content 1", null);
        ContextAssembleRequest request = new ContextAssembleRequest(
                "ws_1", "col_1", List.of(anchorResult), "conservative_neighbors_v1", 0, 0, 5, false, false);

        Chunk chunk = new Chunk(UUID.randomUUID(), "chk_1", "ws_1", "doc_1", "semantic_block", "v1", 1, "text", "Very long content 1", null, 10, "hash", null);

        when(chunkRepository.findByPublicIdIn(anyList())).thenReturn(List.of(chunk));

        ContextAssembleResponse response = contextBuilderService.assembleContext(request);

        assertNotNull(response);
        assertTrue(response.budgetHit());
        assertEquals(0, response.items().size());
        assertEquals(1, response.discarded().size());
    }

    @Test
    void testAssembleContext_sequentialPolicy() {
        SearchResult anchorResult = new SearchResult(1, "chk_2", "doc_1", 0.9, false, "text", "Content 2", null);
        ContextAssembleRequest request = new ContextAssembleRequest(
                "ws_1", "col_1", List.of(anchorResult), "sequential_neighbors_v1", 1, 1, 6000, false, false);

        Chunk anchorChunk = new Chunk(UUID.randomUUID(), "chk_2", "ws_1", "doc_1", "semantic_block", "v1", 2, "text", "Content 2", null, 10, "hash", null);
        Chunk beforeChunk = new Chunk(UUID.randomUUID(), "chk_1", "ws_1", "doc_1", "semantic_block", "v1", 1, "text", "Content 1", null, 10, "hash", null);
        Chunk afterChunk = new Chunk(UUID.randomUUID(), "chk_3", "ws_1", "doc_1", "semantic_block", "v1", 3, "text", "Content 3", null, 10, "hash", null);

        when(chunkRepository.findByPublicIdIn(anyList())).thenReturn(List.of(anchorChunk));
        when(chunkRepository.findNeighbors(any(), any(), any(), any())).thenReturn(List.of(beforeChunk, anchorChunk, afterChunk));

        ContextAssembleResponse response = contextBuilderService.assembleContext(request);

        assertNotNull(response);
        assertEquals(3, response.items().size());
        assertEquals(1, response.items().stream().filter(i -> "anchor".equals(i.rankSource())).count());
        assertEquals(1, response.items().stream().filter(i -> "neighbor_before".equals(i.rankSource())).count());
        assertEquals(1, response.items().stream().filter(i -> "neighbor_after".equals(i.rankSource())).count());
    }
}
