package local.fabricarag.core.service;

import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.domain.ChunkEmbedding;
import local.fabricarag.core.domain.VectorIndexBinding;
import local.fabricarag.core.dto.worker.EmbeddingsTextRequest;
import local.fabricarag.core.dto.worker.EmbeddingsTextResponse;
import local.fabricarag.core.repository.ChunkEmbeddingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmbeddingsService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingsService.class);

    private final WorkerClient workerClient;
    private final ChunkEmbeddingRepository chunkEmbeddingRepository;

    public EmbeddingsService(WorkerClient workerClient, ChunkEmbeddingRepository chunkEmbeddingRepository) {
        this.workerClient = workerClient;
        this.chunkEmbeddingRepository = chunkEmbeddingRepository;
    }

    public void requestEmbeddings(EmbeddingsTextRequest request) {
        logger.info("Requesting embeddings for requestId: {}", request.getRequestId());
        // Webhook callback URL should be configured in the request by the caller
        workerClient.requestEmbeddings(request);
    }

    @Transactional
    public void handleEmbeddingsCallback(EmbeddingsTextResponse response) {
        logger.info("Handling embeddings callback for requestId: {}", response.getRequestId());
        
        for (EmbeddingsTextResponse.Item item : response.getItems()) {
            // Note: in a full implementation we would find the chunk, find the binding, 
            // and update/insert the ChunkEmbedding entity accordingly.
            // Since this is the base implementation (contract), we log it.
            logger.info("Received vector of dimension {} for item {}", 
                response.getDimension(), item.getItemId());
        }
    }
}
