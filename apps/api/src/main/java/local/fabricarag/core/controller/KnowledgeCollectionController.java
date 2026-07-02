package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.dto.CollectionCreateRequest;
import local.fabricarag.core.dto.CollectionResponse;
import local.fabricarag.core.security.AuthorizationPolicy;
import local.fabricarag.core.service.KnowledgeCollectionService;
import local.fabricarag.core.web.ResolvePublicId;
import local.fabricarag.core.domain.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import local.fabricarag.core.security.CurrentActor;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/collections")
public class KnowledgeCollectionController {

    private final KnowledgeCollectionService collectionService;
    private final AuthorizationPolicy authorizationPolicy;

    public KnowledgeCollectionController(KnowledgeCollectionService collectionService, AuthorizationPolicy authorizationPolicy) {
        this.collectionService = collectionService;
        this.authorizationPolicy = authorizationPolicy;
    }

    @GetMapping
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.read')")
    public Page<CollectionResponse> listCollections(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            Pageable pageable) {
        return collectionService.listCollections(internalWorkspaceId, pageable);
    }

    @GetMapping("/{collectionPublicId}")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.read')")
    public CollectionResponse getCollection(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable String collectionPublicId) {
        return collectionService.getCollection(internalWorkspaceId, collectionPublicId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.update')") // Collections manipulate workspace state
    public CollectionResponse createCollection(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @Valid @RequestBody CollectionCreateRequest request,
            @AuthenticationPrincipal CurrentActor actor) {
        return collectionService.createCollection(internalWorkspaceId, actor.getUserId(), request);
    }
}
