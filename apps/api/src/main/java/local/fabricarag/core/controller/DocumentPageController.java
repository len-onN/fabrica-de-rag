package local.fabricarag.core.controller;

import local.fabricarag.core.dto.DocumentPageDto;
import local.fabricarag.core.dto.PageNumberingAnchorDto;
import local.fabricarag.core.dto.UpdateNumberingAnchorsRequest;
import local.fabricarag.core.repository.DocumentPageRepository;
import local.fabricarag.core.repository.PageNumberingAnchorRepository;
import local.fabricarag.core.service.PageNumberingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId) {
        
        List<DocumentPageDto> pages = pageRepository.findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(workspaceId, documentId)
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
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId) {
        
        List<PageNumberingAnchorDto> anchors = anchorRepository.findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(workspaceId, documentId)
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
    public ResponseEntity<Void> updateAnchors(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @RequestBody UpdateNumberingAnchorsRequest request) {

        // Use a dummy UUID for MVP as the current auth context might be mock
        UUID userId = UUID.randomUUID(); 
        pageNumberingService.applyAnchorsAndInferNumbering(workspaceId, documentId, userId, request);
        
        return ResponseEntity.noContent().build();
    }
}
