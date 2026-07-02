package local.fabricarag.core.controller;

import local.fabricarag.core.dto.IngestRunDetailResponse;
import local.fabricarag.core.dto.IngestRunLogResponse;
import local.fabricarag.core.security.CurrentActor;
import local.fabricarag.core.service.IngestRunOperationService;
import local.fabricarag.core.web.ResolvePublicId;
import local.fabricarag.core.domain.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/ingest-runs")
public class IngestRunController {

    private final IngestRunOperationService ingestRunOperationService;

    public IngestRunController(IngestRunOperationService ingestRunOperationService) {
        this.ingestRunOperationService = ingestRunOperationService;
    }

    @GetMapping("/{runPublicId}")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'ingest_run.read')")
    public ResponseEntity<IngestRunDetailResponse> getRunDetails(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable String runPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        return ResponseEntity.ok(ingestRunOperationService.getRunDetails(internalWorkspaceId, runPublicId));
    }

    @GetMapping
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'ingest_run.read')")
    public ResponseEntity<List<IngestRunDetailResponse>> listRuns(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @RequestParam("documentId") String documentPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        return ResponseEntity.ok(ingestRunOperationService.listRunsForDocument(internalWorkspaceId, documentPublicId));
    }

    @GetMapping("/{runPublicId}/logs")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'ingest_run.read')")
    public ResponseEntity<Page<IngestRunLogResponse>> getRunLogs(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable String runPublicId,
            Pageable pageable,
            @AuthenticationPrincipal CurrentActor actor) {
        
        return ResponseEntity.ok(ingestRunOperationService.getRunLogs(internalWorkspaceId, runPublicId, pageable));
    }

    @PostMapping("/{runPublicId}/cancel")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'ingest_run.update')")
    public ResponseEntity<Void> cancelRun(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable String runPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        ingestRunOperationService.cancelRun(internalWorkspaceId, runPublicId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runPublicId}/retry")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'ingest_run.update')")
    public ResponseEntity<IngestRunDetailResponse> retryRun(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable String runPublicId,
            @AuthenticationPrincipal CurrentActor actor) {
        
        IngestRunDetailResponse newRun = ingestRunOperationService.retryRun(internalWorkspaceId, runPublicId, actor.getUserId());
        return ResponseEntity.ok(newRun);
    }
}
