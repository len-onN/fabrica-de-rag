package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.domain.RetrievalFeedback;
import local.fabricarag.core.dto.RetrievalFeedbackRequest;
import local.fabricarag.core.repository.RetrievalFeedbackRepository;
import local.fabricarag.core.security.CurrentActor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/retrieval-feedback")
public class RetrievalFeedbackController {

    private final RetrievalFeedbackRepository feedbackRepository;

    public RetrievalFeedbackController(RetrievalFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'feedback.create')")
    public ResponseEntity<Void> createFeedback(
            @PathVariable String workspaceId,
            @Valid @RequestBody RetrievalFeedbackRequest request,
            Authentication authentication
    ) {
        UUID currentUserId = null;
        if (authentication instanceof CurrentActor actor) {
            currentUserId = actor.getUserId();
        }

        RetrievalFeedback feedback = new RetrievalFeedback(
                UUID.randomUUID(),
                "fdb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16),
                workspaceId,
                request.collectionId(),
                request.documentId(),
                request.chunkId(),
                request.queryEventId(),
                request.feedbackType(),
                request.note(),
                currentUserId
        );

        feedbackRepository.save(feedback);
        return ResponseEntity.accepted().build();
    }
}
