package local.fabricarag.core.dto.worker;

import com.fasterxml.jackson.annotation.JsonProperty;
import local.fabricarag.core.dto.worker.components.SourceLocator;

import java.util.List;

public record ChunksBuildRequest(
        String contractVersion,
        String requestId,
        String workspaceId,
        String documentId,
        String chunkingStrategy,
        String chunkingVersion,
        String profile,
        Integer targetTokens,
        Integer overlapTokens,
        List<ChunkPage> pages,
        List<ChunkElement> elements,
        List<VisualInterpretation> visualInterpretations,
        String callbackUrl
) {
    public record ChunkPage(
            String id,
            Integer filePageNumber,
            String effectivePrintedLabel,
            Boolean includeInSearch,
            String pageRole
    ) {}

    public record ChunkElement(
            String id,
            String elementType,
            Integer readingOrder,
            String textContent,
            Object structuredContent,
            SourceLocator sourceLocator
    ) {}

    public record VisualInterpretation(
            String id,
            String interpretationText,
            Object structuredText,
            SourceLocator sourceLocator
    ) {}
}
