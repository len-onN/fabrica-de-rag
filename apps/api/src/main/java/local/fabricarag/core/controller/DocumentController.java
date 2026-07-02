package local.fabricarag.core.controller;

import local.fabricarag.core.dto.DocumentResponse;
import local.fabricarag.core.security.CurrentActor;
import local.fabricarag.core.web.ResolvePublicId;
import local.fabricarag.core.domain.Workspace;
import local.fabricarag.core.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/collections/{collectionId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @PreAuthorize("@authorizationPolicy.hasPermission(#actor, #workspaceId, 'document.create')")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable("collectionId") String collectionId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CurrentActor actor) {
        
        DocumentResponse response = documentService.uploadDocument(internalWorkspaceId, collectionId, file, actor.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("@authorizationPolicy.hasPermission(#actor, #workspaceId, 'document.read')")
    public ResponseEntity<Page<DocumentResponse>> listDocuments(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable("collectionId") String collectionId,
            Pageable pageable,
            @AuthenticationPrincipal CurrentActor actor) {
        
        return ResponseEntity.ok(documentService.listDocuments(internalWorkspaceId, collectionId, pageable));
    }
}
