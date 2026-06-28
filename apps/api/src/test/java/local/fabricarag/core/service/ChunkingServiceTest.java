package local.fabricarag.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.dto.worker.ChunksBuildRequest;
import local.fabricarag.core.dto.worker.ChunksBuildResponse;
import local.fabricarag.core.repository.ChunkRelationRepository;
import local.fabricarag.core.repository.ChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChunkingServiceTest {

    @Mock
    private WorkerClient workerClient;

    @Mock
    private ChunkRepository chunkRepository;

    @Mock
    private ChunkRelationRepository chunkRelationRepository;

    private ChunkingService chunkingService;

    @BeforeEach
    void setUp() {
        chunkingService = new ChunkingService(workerClient, chunkRepository, chunkRelationRepository, new ObjectMapper());
    }

    @Test
    void testProcessChunksForDocument() {
        ChunksBuildRequest request = new ChunksBuildRequest(
                "worker.chunks.build.request.v1",
                "req-1",
                "workspace-1",
                "doc-1",
                "semantic_block",
                "semantic_block_v1",
                "fast",
                450,
                40,
                List.of(),
                List.of(),
                List.of(),
                null
        );

        ChunksBuildResponse response = new ChunksBuildResponse(
                "worker.chunks.build.response.v1",
                "req-1",
                "workspace-1",
                "doc-1",
                "semantic_block",
                "semantic_block_v1",
                List.of(
                        new ChunksBuildResponse.ChunkDto(
                                "chunk-1",
                                1,
                                "text",
                                "Hello World",
                                List.of(),
                                2,
                                "hash123",
                                null
                        )
                ),
                List.of(),
                new ChunksBuildResponse.ChunkMetrics(1, 1, 2)
        );

        when(workerClient.requestBuildChunksSync(any())).thenReturn(response);
        when(chunkRepository.save(any())).thenReturn(null);

        chunkingService.processChunksForDocument(request);

        verify(workerClient, times(1)).requestBuildChunksSync(request);
        
        ArgumentCaptor<Chunk> chunkCaptor = ArgumentCaptor.forClass(Chunk.class);
        verify(chunkRepository, times(1)).save(chunkCaptor.capture());
        
        Chunk savedChunk = chunkCaptor.getValue();
        assertEquals("chunk-1", savedChunk.getPublicId());
        assertEquals("text", savedChunk.getContentKind());
        assertEquals("Hello World", savedChunk.getContent());
    }
}
