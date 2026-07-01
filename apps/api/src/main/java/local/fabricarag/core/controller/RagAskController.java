package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.dto.rag.AskRequest;
import local.fabricarag.core.dto.rag.AskResponse;
import local.fabricarag.core.service.RagAskService;
import local.fabricarag.core.security.CurrentActor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/rag")
public class RagAskController {

    private final RagAskService ragAskService;

    public RagAskController(RagAskService ragAskService) {
        this.ragAskService = ragAskService;
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
}
