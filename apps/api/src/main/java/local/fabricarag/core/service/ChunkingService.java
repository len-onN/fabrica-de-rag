package local.fabricarag.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.domain.ChunkRelation;
import local.fabricarag.core.dto.worker.ChunksBuildRequest;
import local.fabricarag.core.dto.worker.ChunksBuildResponse;
import local.fabricarag.core.repository.ChunkRelationRepository;
import local.fabricarag.core.repository.ChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChunkingService {
    private static final Logger logger = LoggerFactory.getLogger(ChunkingService.class);

    private final WorkerClient workerClient;
    private final ChunkRepository chunkRepository;
    private final ChunkRelationRepository chunkRelationRepository;
    private final ObjectMapper objectMapper;

    public ChunkingService(WorkerClient workerClient, ChunkRepository chunkRepository,
                           ChunkRelationRepository chunkRelationRepository, ObjectMapper objectMapper) {
        this.workerClient = workerClient;
        this.chunkRepository = chunkRepository;
        this.chunkRelationRepository = chunkRelationRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processChunksForDocument(ChunksBuildRequest request) {
        logger.info("Sending chunks build request for document {}", request.documentId());

        ChunksBuildResponse response = workerClient.requestBuildChunksSync(request);

        if (response != null && response.chunks() != null) {
            logger.info("Received {} chunks for document {}", response.chunks().size(), request.documentId());

            for (ChunksBuildResponse.ChunkDto dto : response.chunks()) {
                String sourceLocatorJson;
                String headingPathJson;
                try {
                    sourceLocatorJson = objectMapper.writeValueAsString(dto.sourceLocator());
                    headingPathJson = objectMapper.writeValueAsString(dto.headingPath());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to serialize chunk metadata to JSON", e);
                }

                Chunk chunk = new Chunk(
                        UUID.randomUUID(),
                        dto.chunkId(),
                        response.workspaceId(),
                        response.documentId(),
                        response.chunkingStrategy(),
                        response.chunkingVersion(),
                        dto.sequenceNumber(),
                        dto.contentKind(),
                        dto.content(),
                        headingPathJson,
                        dto.tokenCount(),
                        dto.sourceHash(),
                        sourceLocatorJson
                );
                chunkRepository.save(chunk);
            }

            if (response.relations() != null) {
                // To save relations properly we need the UUIDs of the saved chunks.
                // But fromChunkId and toChunkId in relation are string public_ids.
                // We'll look them up from db (since we just saved them in this transaction).
                for (ChunksBuildResponse.ChunkRelationDto rDto : response.relations()) {
                    Chunk fromChunk = chunkRepository.findByDocumentId(response.documentId()).stream()
                            .filter(c -> c.getPublicId().equals(rDto.fromChunkId()))
                            .findFirst().orElse(null);
                    Chunk toChunk = chunkRepository.findByDocumentId(response.documentId()).stream()
                            .filter(c -> c.getPublicId().equals(rDto.toChunkId()))
                            .findFirst().orElse(null);

                    if (fromChunk != null && toChunk != null) {
                        ChunkRelation relation = new ChunkRelation(
                                UUID.randomUUID(),
                                fromChunk.getId(),
                                toChunk.getId(),
                                rDto.relationType(),
                                rDto.weight()
                        );
                        chunkRelationRepository.save(relation);
                    } else {
                        logger.warn("Could not find chunks to create relation {} -> {}", rDto.fromChunkId(), rDto.toChunkId());
                    }
                }
            }
        }
    }
}
