package local.fabricarag.core.service;

import local.fabricarag.core.dto.rag.*;
import local.fabricarag.core.port.out.AnswerProviderPort;
import local.fabricarag.core.port.out.AnswerProviderResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RagAskService {

    private final VectorSearchService vectorSearchService;
    private final ContextBuilderService contextBuilderService;
    private final AnswerProviderPort answerProviderPort;

    public RagAskService(VectorSearchService vectorSearchService,
                         ContextBuilderService contextBuilderService,
                         AnswerProviderPort answerProviderPort) {
        this.vectorSearchService = vectorSearchService;
        this.contextBuilderService = contextBuilderService;
        this.answerProviderPort = answerProviderPort;
    }

    public AskResponse ask(AskRequest request) {
        long startTime = System.currentTimeMillis();

        // 1. Vetorial Search
        SearchRequest searchRequest = new SearchRequest(
                request.workspaceId(),
                request.collectionId(),
                request.query(),
                request.topK(),
                request.filters()
        );

        SearchResponse searchResponse = vectorSearchService.search(searchRequest);

        // Check if there are no results or all results have low confidence
        boolean hasLowConfidence = searchResponse.results().isEmpty() || searchResponse.results().get(0).lowConfidence();
        if (searchResponse.results().isEmpty() || hasLowConfidence) {
            return buildInsufficientEvidenceResponse(request, searchResponse, null, startTime);
        }

        // 2. Assemble Context
        ContextAssembleRequest contextRequest = new ContextAssembleRequest(
                request.workspaceId(),
                request.collectionId(),
                searchResponse.results(),
                request.policy(),
                2, // Default neighborBefore
                2, // Default neighborAfter
                request.tokenBudget(),
                request.includeVisuals(),
                request.includeTables()
        );

        ContextAssembleResponse contextResponse = contextBuilderService.assembleContext(contextRequest);

        if (contextResponse.items().isEmpty()) {
            return buildInsufficientEvidenceResponse(request, searchResponse, contextResponse, startTime);
        }

        // 3. Generate Answer via LLM
        AnswerProviderResponse llmResponse = answerProviderPort.generateAnswer(request.query(), contextResponse.items());

        if ("insufficient_evidence".equals(llmResponse.status())) {
            return buildInsufficientEvidenceResponse(request, searchResponse, contextResponse, startTime);
        }

        // Filter citations to only those used by the LLM
        List<Citation> finalCitations = contextResponse.citations().stream()
                .filter(c -> llmResponse.citationIds().contains(c.citationId()))
                .collect(Collectors.toList());

        long totalLatencyMs = System.currentTimeMillis() - startTime;

        AskResponse.AskMetrics metrics = new AskResponse.AskMetrics(
                searchResponse.metrics().returnedResults(),
                contextResponse.metrics().contextItems(),
                contextResponse.metrics().estimatedTokens(),
                totalLatencyMs
        );

        return new AskResponse(
                "rag.ask.response.v1",
                request.workspaceId(),
                request.collectionId(),
                "qry_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                llmResponse.status(),
                llmResponse.answer(),
                finalCitations,
                searchResponse.results(),
                contextResponse.items(),
                llmResponse.reason(),
                metrics
        );
    }

    private AskResponse buildInsufficientEvidenceResponse(AskRequest request, SearchResponse searchResponse, ContextAssembleResponse contextResponse, long startTime) {
        long latencyMs = System.currentTimeMillis() - startTime;
        
        int contextItems = contextResponse != null ? contextResponse.metrics().contextItems() : 0;
        int estimatedTokens = contextResponse != null ? contextResponse.metrics().estimatedTokens() : 0;

        AskResponse.AskMetrics metrics = new AskResponse.AskMetrics(
                searchResponse.metrics().returnedResults(),
                contextItems,
                estimatedTokens,
                latencyMs
        );

        return new AskResponse(
                "rag.ask.response.v1",
                request.workspaceId(),
                request.collectionId(),
                "qry_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                "insufficient_evidence",
                null,
                Collections.emptyList(),
                searchResponse.results(),
                contextResponse != null ? contextResponse.items() : Collections.emptyList(),
                "no_reliable_context",
                metrics
        );
    }
}
