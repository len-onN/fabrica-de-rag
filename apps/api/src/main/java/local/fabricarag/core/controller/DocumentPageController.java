package local.fabricarag.core.controller;

import local.fabricarag.core.dto.DocumentPageDto;
import local.fabricarag.core.dto.PageNumberingAnchorDto;
import local.fabricarag.core.dto.UpdateNumberingAnchorsRequest;
import local.fabricarag.core.repository.DocumentPageRepository;
import local.fabricarag.core.repository.PageNumberingAnchorRepository;
import local.fabricarag.core.service.PageNumberingService;
import local.fabricarag.core.web.ResolvePublicId;
import local.fabricarag.core.domain.Workspace;
import local.fabricarag.core.domain.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import local.fabricarag.core.security.CurrentActor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/documents/{documentId}/pages")
public class DocumentPageController {

    private final DocumentPageRepository pageRepository;
    private final PageNumberingAnchorRepository anchorRepository;
    private final PageNumberingService pageNumberingService;

    public DocumentPageController(DocumentPageRepository pageRepository,
                                  PageNumberingAnchorRepository anchorRepository,
                                  PageNumberingService pageNumberingService) {
        this.pageRepository = pageRepository;
        this.anchorRepository = anchorRepository;
        this.pageNumberingService = pageNumberingService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentPageDto>> getPages(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable("documentId") String documentId,
            @ResolvePublicId(value = Document.class, pathVar = "documentId") UUID internalDocumentId) {
        
        List<DocumentPageDto> pages = pageRepository.findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(internalWorkspaceId, internalDocumentId)
                .stream()
                .map(p -> new DocumentPageDto(
                        p.getPublicId(),
                        p.getFilePageNumber(),
                        p.getDetectedPrintedLabel(),
                        p.getEffectivePrintedLabel(),
                        p.getNumberingStyle(),
                        p.getPageRole(),
                        p.getIncludeInSearch(),
                        p.getIncludeInNumbering(),
                        p.getOcrStatus()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(pages);
    }

    @GetMapping("/anchors")
    public ResponseEntity<List<PageNumberingAnchorDto>> getAnchors(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable("documentId") String documentId,
            @ResolvePublicId(value = Document.class, pathVar = "documentId") UUID internalDocumentId) {
        
        List<PageNumberingAnchorDto> anchors = anchorRepository.findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(internalWorkspaceId, internalDocumentId)
                .stream()
                .map(a -> new PageNumberingAnchorDto(
                        a.getPublicId(),
                        a.getFilePageNumber(),
                        a.getPrintedLabel(),
                        a.getNumberingStyle(),
                        a.getApplyDirection()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(anchors);
    }

    @PostMapping("/anchors")
    @PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'document.update')")
    public ResponseEntity<Void> updateAnchors(
            @PathVariable("workspaceId") String workspaceId,
            @ResolvePublicId(value = Workspace.class, pathVar = "workspaceId") UUID internalWorkspaceId,
            @PathVariable("documentId") String documentId,
            @ResolvePublicId(value = Document.class, pathVar = "documentId") UUID internalDocumentId,
            @RequestBody UpdateNumberingAnchorsRequest request,
            @AuthenticationPrincipal CurrentActor actor) {

        pageNumberingService.applyAnchorsAndInferNumbering(internalWorkspaceId, internalDocumentId, actor.getUserId(), request);
        
        return ResponseEntity.noContent().build();
    }
}
