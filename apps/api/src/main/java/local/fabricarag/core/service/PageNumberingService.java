package local.fabricarag.core.service;

import local.fabricarag.core.domain.DocumentPage;
import local.fabricarag.core.domain.PageNumberingAnchor;
import local.fabricarag.core.dto.PageNumberingAnchorDto;
import local.fabricarag.core.dto.UpdateNumberingAnchorsRequest;
import local.fabricarag.core.repository.DocumentPageRepository;
import local.fabricarag.core.repository.PageNumberingAnchorRepository;
import local.fabricarag.core.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PageNumberingService {

    private final DocumentPageRepository pageRepository;
    private final PageNumberingAnchorRepository anchorRepository;
    private final IdGenerator idGenerator;

    public PageNumberingService(DocumentPageRepository pageRepository,
                                PageNumberingAnchorRepository anchorRepository,
                                IdGenerator idGenerator) {
        this.pageRepository = pageRepository;
        this.anchorRepository = anchorRepository;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public void applyAnchorsAndInferNumbering(UUID workspaceId, UUID documentId, UUID userId, UpdateNumberingAnchorsRequest request) {
        // 1. Delete old anchors for this document
        anchorRepository.deleteByWorkspaceIdAndDocumentId(workspaceId, documentId);

        // 2. Load pages
        List<DocumentPage> pages = pageRepository.findByWorkspaceIdAndDocumentIdOrderByFilePageNumberAsc(workspaceId, documentId);

        // 3. Create and save new anchors
        List<PageNumberingAnchor> anchors = request.anchors().stream().map(dto -> {
            DocumentPage page = pages.stream()
                .filter(p -> p.getFilePageNumber().equals(dto.filePageNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid file page number: " + dto.filePageNumber()));

            return new PageNumberingAnchor(
                idGenerator.generateUuid(),
                idGenerator.generatePublicId("anc"),
                workspaceId,
                documentId,
                page.getId(),
                dto.filePageNumber(),
                dto.printedLabel(),
                dto.numberingStyle(),
                dto.applyDirection(),
                userId
            );
        }).collect(Collectors.toList());

        anchorRepository.saveAll(anchors);

        // 4. Infer effective numbering for each page
        for (int i = 0; i < pages.size(); i++) {
            DocumentPage page = pages.get(i);
            
            if (!page.getIncludeInNumbering()) {
                page.updateLabels(page.getDetectedPrintedLabel(), null, null);
                continue;
            }

            Optional<PageNumberingAnchor> exactAnchor = anchors.stream()
                .filter(a -> a.getFilePageNumber().equals(page.getFilePageNumber()))
                .findFirst();

            if (exactAnchor.isPresent()) {
                page.updateLabels(page.getDetectedPrintedLabel(), exactAnchor.get().getPrintedLabel(), exactAnchor.get().getNumberingStyle());
            } else {
                // Find preceding anchor
                Optional<PageNumberingAnchor> preceding = anchors.stream()
                    .filter(a -> a.getFilePageNumber() < page.getFilePageNumber())
                    .filter(a -> a.getApplyDirection().equals("forward") || a.getApplyDirection().equals("both"))
                    .reduce((first, second) -> second); // get last (closest)

                if (preceding.isPresent()) {
                    PageNumberingAnchor anchor = preceding.get();
                    // calculate offset (how many includeInNumbering pages between anchor and here)
                    long offset = pages.stream()
                        .filter(p -> p.getFilePageNumber() > anchor.getFilePageNumber() && p.getFilePageNumber() <= page.getFilePageNumber())
                        .filter(DocumentPage::getIncludeInNumbering)
                        .count();
                    
                    String inferred = incrementLabel(anchor.getPrintedLabel(), anchor.getNumberingStyle(), (int) offset);
                    page.updateLabels(page.getDetectedPrintedLabel(), inferred, anchor.getNumberingStyle());
                } else {
                    // fallback to detected if available
                    page.updateLabels(page.getDetectedPrintedLabel(), page.getDetectedPrintedLabel(), "unknown");
                }
            }
        }

        pageRepository.saveAll(pages);
    }

    private String incrementLabel(String baseLabel, String style, int offset) {
        if (offset == 0) return baseLabel;
        try {
            if ("arabic".equals(style)) {
                return String.valueOf(Integer.parseInt(baseLabel) + offset);
            }
            if ("roman".equals(style)) {
                // Minimal roman implementation for MVP
                int arabic = romanToArabic(baseLabel.toUpperCase());
                return arabicToRoman(arabic + offset).toLowerCase();
            }
        } catch (Exception e) {
            // Ignore parse errors, fallback to base
        }
        return baseLabel;
    }

    private int romanToArabic(String roman) {
        if (roman.equals("I")) return 1;
        if (roman.equals("II")) return 2;
        if (roman.equals("III")) return 3;
        if (roman.equals("IV")) return 4;
        if (roman.equals("V")) return 5;
        if (roman.equals("VI")) return 6;
        if (roman.equals("VII")) return 7;
        if (roman.equals("VIII")) return 8;
        if (roman.equals("IX")) return 9;
        if (roman.equals("X")) return 10;
        return 1; // Simplification for MVP
    }

    private String arabicToRoman(int number) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] units = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        if (number <= 0 || number > 3999) return String.valueOf(number);
        return thousands[number / 1000] + hundreds[(number % 1000) / 100] + tens[(number % 100) / 10] + units[number % 10];
    }
}
