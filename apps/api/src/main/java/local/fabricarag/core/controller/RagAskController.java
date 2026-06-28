package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.dto.rag.AskRequest;
import local.fabricarag.core.dto.rag.AskResponse;
import local.fabricarag.core.service.RagAskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/rag")
public class RagAskController {

    private final RagAskService ragAskService;

    public RagAskController(RagAskService ragAskService) {
        this.ragAskService = ragAskService;
    }

    @PostMapping("/ask")
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'rag.ask')")
    public ResponseEntity<AskResponse> ask(
            @PathVariable String workspaceId,
            @Valid @RequestBody AskRequest request
    ) {
        // Assegura que o workspaceId da URL e do body sao consistentes (ou forca o da URL)
        AskRequest securedRequest = new AskRequest(
                workspaceId,
                request.collectionId(),
                request.query(),
                request.topK(),
                request.policy(),
                request.tokenBudget(),
                request.filters(),
                request.includeTables(),
                request.includeVisuals()
        );
        
        AskResponse response = ragAskService.ask(securedRequest);
        return ResponseEntity.ok(response);
    }
}
