package local.fabricarag.core.dto.worker;

import local.fabricarag.core.dto.worker.components.SourceLocator;

import java.util.List;

public record ChunksBuildResponse(
        String contractVersion,
        String requestId,
        String workspaceId,
        String documentId,
        String chunkingStrategy,
        String chunkingVersion,
        List<ChunkDto> chunks,
        List<ChunkRelationDto> relations,
        ChunkMetrics metrics
) {
    public record ChunkDto(
            String chunkId,
            Integer sequenceNumber,
            String contentKind,
            String content,
            List<String> headingPath,
            Integer tokenCount,
            String sourceHash,
            SourceLocator sourceLocator
    ) {}

    public record ChunkRelationDto(
            String fromChunkId,
            String toChunkId,
            String relationType,
            Double weight
    ) {}

    public record ChunkMetrics(
            Integer inputBlocks,
            Integer chunksCreated,
            Integer estimatedTokens
    ) {}
}
