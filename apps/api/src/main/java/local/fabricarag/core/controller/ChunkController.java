package local.fabricarag.core.controller;

import local.fabricarag.core.dto.ChunkResponse;
import local.fabricarag.core.service.ChunkBrowserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/collections/{collectionId}/chunks")
public class ChunkController {

    private final ChunkBrowserService chunkBrowserService;

    public ChunkController(ChunkBrowserService chunkBrowserService) {
        this.chunkBrowserService = chunkBrowserService;
    }

    @GetMapping
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'document.read')")
    public ResponseEntity<Page<ChunkResponse>> listChunks(
            @PathVariable String workspaceId,
            @PathVariable UUID collectionId,
            @RequestParam(required = false) String documentId,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        Page<ChunkResponse> chunks = chunkBrowserService.listChunks(workspaceId, collectionId, documentId, q, pageable);
        return ResponseEntity.ok(chunks);
    }

    @GetMapping("/{chunkId}/neighbors")
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'document.read')")
    public ResponseEntity<ChunkResponse> getNeighbor(
            @PathVariable String workspaceId,
            @PathVariable UUID collectionId, // Not explicitly used but part of path for context
            @PathVariable String chunkId,
            @RequestParam int offset
    ) {
        return chunkBrowserService.getNeighbor(workspaceId, chunkId, offset)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{chunkId}")
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'document.read')")
    public ResponseEntity<ChunkResponse> getChunk(
            @PathVariable String workspaceId,
            @PathVariable UUID collectionId,
            @PathVariable String chunkId
    ) {
        return chunkBrowserService.getChunk(workspaceId, chunkId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
