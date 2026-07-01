package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.dto.rag.AskRequest;
import local.fabricarag.core.dto.rag.AskResponse;
import local.fabricarag.core.dto.rag.ContextAssembleRequest;
import local.fabricarag.core.dto.rag.ContextAssembleResponse;
import local.fabricarag.core.dto.rag.SearchRequest;
import local.fabricarag.core.dto.rag.SearchResponse;
import local.fabricarag.core.service.RagAskService;
import local.fabricarag.core.service.VectorSearchService;
import local.fabricarag.core.service.ContextBuilderService;
import local.fabricarag.core.security.CurrentActor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/rag")
public class RagAskController {

    private final RagAskService ragAskService;
    private final VectorSearchService vectorSearchService;
    private final ContextBuilderService contextBuilderService;

    public RagAskController(
            RagAskService ragAskService,
            VectorSearchService vectorSearchService,
            ContextBuilderService contextBuilderService
    ) {
        this.ragAskService = ragAskService;
        this.vectorSearchService = vectorSearchService;
        this.contextBuilderService = contextBuilderService;
    }

    @PostMapping("/ask")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'rag.ask')")
    public ResponseEntity<AskResponse> ask(
            @PathVariable String workspaceId,
            @Valid @RequestBody AskRequest request,
            @AuthenticationPrincipal CurrentActor actor
    ) {
        Integer topK = request.topK();
        Integer budget = request.tokenBudget();

        if ("agent".equals(actor.getType())) {
            topK = topK == null ? 8 : Math.min(topK, 12);
            budget = budget == null ? 5000 : Math.min(budget, 6000);
        }

        // Assegura que o workspaceId da URL e do body sao consistentes (ou forca o da URL)
        AskRequest securedRequest = new AskRequest(
                workspaceId,
                request.collectionId(),
                request.query(),
                topK,
                request.policy(),
                budget,
                request.filters(),
                request.includeTables(),
                request.includeVisuals()
        );
        
        AskResponse response = ragAskService.ask(securedRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'rag.ask')")
    public ResponseEntity<SearchResponse> search(
            @PathVariable String workspaceId,
            @Valid @RequestBody SearchRequest request,
            @AuthenticationPrincipal CurrentActor actor
    ) {
        Integer topK = request.topK();
        if ("agent".equals(actor.getType())) {
            topK = topK == null ? 8 : Math.min(topK, 12);
        }

        SearchRequest securedRequest = new SearchRequest(
                workspaceId,
                request.collectionId(),
                request.query(),
                topK,
                request.filters()
        );

        SearchResponse response = vectorSearchService.search(securedRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/context")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'rag.ask')")
    public ResponseEntity<ContextAssembleResponse> assembleContext(
            @PathVariable String workspaceId,
            @Valid @RequestBody ContextAssembleRequest request,
            @AuthenticationPrincipal CurrentActor actor
    ) {
        Integer budget = request.tokenBudget();
        Integer topK = request.topK();
        
        if ("agent".equals(actor.getType())) {
            budget = budget == null ? 5000 : Math.min(budget, 6000);
            topK = topK == null ? 8 : Math.min(topK, 12);
        }

        ContextAssembleRequest securedRequest = new ContextAssembleRequest(
                workspaceId,
                request.collectionId(),
                request.query(),
                topK,
                request.policy(),
                budget,
                request.filters(),
                request.includeTables(),
                request.includeVisuals()
        );

        ContextAssembleResponse response = contextBuilderService.assembleContext(securedRequest);
        return ResponseEntity.ok(response);
    }
}
