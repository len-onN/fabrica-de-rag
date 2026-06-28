package local.fabricarag.core.service;

import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.dto.worker.EmbeddingsTextRequest;
import local.fabricarag.core.dto.worker.EmbeddingsTextResponse;
import local.fabricarag.core.repository.ChunkEmbeddingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class EmbeddingsServiceTest {

    private WorkerClient workerClient;
    private ChunkEmbeddingRepository chunkEmbeddingRepository;
    private EmbeddingsService embeddingsService;

    @BeforeEach
    void setUp() {
        workerClient = Mockito.mock(WorkerClient.class);
        chunkEmbeddingRepository = Mockito.mock(ChunkEmbeddingRepository.class);
        embeddingsService = new EmbeddingsService(workerClient, chunkEmbeddingRepository);
    }

    @Test
    void shouldRequestEmbeddingsViaWorkerClient() {
        EmbeddingsTextRequest req = new EmbeddingsTextRequest();
        req.setRequestId("req-123");
        req.setWorkspaceId("ws-1");

        embeddingsService.requestEmbeddings(req);

        ArgumentCaptor<EmbeddingsTextRequest> captor = ArgumentCaptor.forClass(EmbeddingsTextRequest.class);
        verify(workerClient).requestEmbeddings(captor.capture());
        
        assertEquals("req-123", captor.getValue().getRequestId());
    }

    @Test
    void shouldHandleEmbeddingsCallback() {
        EmbeddingsTextResponse res = new EmbeddingsTextResponse();
        res.setRequestId("req-123");
        EmbeddingsTextResponse.Item item = new EmbeddingsTextResponse.Item();
        item.setItemId("item-1");
        item.setVector(Collections.nCopies(16, 0.5));
        res.setItems(Collections.singletonList(item));

        embeddingsService.handleEmbeddingsCallback(res);
        // Just verify no exceptions for now. 
        // Real implementation will verify repository saves.
    }
}
