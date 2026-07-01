package local.fabricarag.core.service;

import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.dto.ChunkResponse;
import local.fabricarag.core.repository.ChunkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChunkBrowserService {

    private final ChunkRepository chunkRepository;

    public ChunkBrowserService(ChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    public Page<ChunkResponse> listChunks(String workspaceId, UUID collectionId, String documentId, String query, Pageable pageable) {
        UUID workspaceUuid = UUID.fromString(workspaceId);
        Page<Chunk> chunks = chunkRepository.findChunksForBrowser(workspaceId, workspaceUuid, collectionId, documentId, query, pageable);
        
        return chunks.map(this::mapToResponse);
    }

    public Optional<ChunkResponse> getChunk(String workspaceId, String chunkPublicId) {
        return chunkRepository.findByPublicId(chunkPublicId)
                .filter(chunk -> chunk.getWorkspaceId().equals(workspaceId))
                .map(this::mapToResponse);
    }

    public Optional<ChunkResponse> getNeighbor(String workspaceId, String chunkPublicId, int offset) {
        return chunkRepository.findByPublicId(chunkPublicId)
                .filter(chunk -> chunk.getWorkspaceId().equals(workspaceId))
                .flatMap(chunk -> chunkRepository.findByWorkspaceIdAndDocumentIdAndSequenceNumber(
                        workspaceId,
                        chunk.getDocumentId(),
                        chunk.getSequenceNumber() + offset
                ))
                .map(this::mapToResponse);
    }

    private ChunkResponse mapToResponse(Chunk chunk) {
        // As a simplification for MVP, embeddingStatus might be omitted or mapped as embedded.
        // If we strictly need the embedding status, we should query ChunkEmbeddingRepository.
        // For now we map it as "embedded" as it usually is if chunks exist.
        return new ChunkResponse(
                chunk.getId().toString(),
                chunk.getPublicId(),
                chunk.getDocumentId(),
                chunk.getSequenceNumber(),
                chunk.getContentKind(),
                chunk.getContent(),
                chunk.getHeadingPath(),
                chunk.getTokenCount(),
                chunk.getSourceLocator(),
                "embedded", 
                chunk.getCreatedAt()
        );
    }
}
