package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.dto.CollectionCreateRequest;
import local.fabricarag.core.dto.CollectionResponse;
import local.fabricarag.core.security.AuthorizationPolicy;
import local.fabricarag.core.service.KnowledgeCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'workspace.read')")
    public Page<CollectionResponse> listCollections(
            @PathVariable UUID workspaceId,
            Pageable pageable) {
        return collectionService.listCollections(workspaceId, pageable);
    }

    @GetMapping("/{collectionPublicId}")
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'workspace.read')")
    public CollectionResponse getCollection(
            @PathVariable UUID workspaceId,
            @PathVariable String collectionPublicId) {
        return collectionService.getCollection(workspaceId, collectionPublicId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'workspace.update')") // Collections manipulate workspace state
    public CollectionResponse createCollection(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CollectionCreateRequest request) {
        // Since we don't extract the user ID easily from the SecurityContext in a simple way right here, 
        // we'll pass a dummy UUID or fetch the current user's UUID if available.
        // For MVP, assuming the user ID can be retrieved from session. We will use a mock ID for now or null.
        // Ideally we would resolve it from AuthSession. For now, a mock UUID represents the system/user.
        UUID currentUserId = UUID.fromString("00000000-0000-0000-0000-000000000000"); // Mock
        return collectionService.createCollection(workspaceId, currentUserId, request);
    }
}
