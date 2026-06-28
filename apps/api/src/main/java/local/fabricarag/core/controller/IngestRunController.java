package local.fabricarag.core.controller;

import local.fabricarag.core.dto.IngestRunDetailResponse;
import local.fabricarag.core.dto.IngestRunLogResponse;
import local.fabricarag.core.security.CurrentActor;
import local.fabricarag.core.service.IngestRunOperationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/ingest-runs")
public class IngestRunController {

    private final IngestRunOperationService ingestRunOperationService;

    public IngestRunController(IngestRunOperationService ingestRunOperationService) {
        this.ingestRunOperationService = ingestRunOperationService;
    }

    @GetMapping("/{runPublicId}")
    @PreAuthorize("@authorizationPolicy.hasPermission(#actor, #workspaceId, 'ingest_run.read')")
    public ResponseEntity<IngestRunDetailResponse> getRunDetails(
            @PathVariable UUID workspaceId,
            @PathVariable String runPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        return ResponseEntity.ok(ingestRunOperationService.getRunDetails(workspaceId, runPublicId));
    }

    @GetMapping("/{runPublicId}/logs")
    @PreAuthorize("@authorizationPolicy.hasPermission(#actor, #workspaceId, 'ingest_run.read')")
    public ResponseEntity<Page<IngestRunLogResponse>> getRunLogs(
            @PathVariable UUID workspaceId,
            @PathVariable String runPublicId,
            Pageable pageable,
            @AuthenticationPrincipal CurrentActor actor) {
        
        return ResponseEntity.ok(ingestRunOperationService.getRunLogs(workspaceId, runPublicId, pageable));
    }

    @PostMapping("/{runPublicId}/cancel")
    @PreAuthorize("@authorizationPolicy.hasPermission(#actor, #workspaceId, 'ingest_run.update')")
    public ResponseEntity<Void> cancelRun(
            @PathVariable UUID workspaceId,
            @PathVariable String runPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        ingestRunOperationService.cancelRun(workspaceId, runPublicId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runPublicId}/retry")
    @PreAuthorize("@authorizationPolicy.hasPermission(#actor, #workspaceId, 'ingest_run.update')")
    public ResponseEntity<IngestRunDetailResponse> retryRun(
            @PathVariable UUID workspaceId,
            @PathVariable String runPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        IngestRunDetailResponse newRun = ingestRunOperationService.retryRun(workspaceId, runPublicId, actor.getUserId());
        return ResponseEntity.ok(newRun);
    }
}
