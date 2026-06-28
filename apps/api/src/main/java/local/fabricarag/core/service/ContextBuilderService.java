package local.fabricarag.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.domain.ChunkRelation;
import local.fabricarag.core.dto.rag.*;
import local.fabricarag.core.repository.ChunkRelationRepository;
import local.fabricarag.core.repository.ChunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContextBuilderService {

    private final ChunkRepository chunkRepository;
    private final ChunkRelationRepository chunkRelationRepository;
    private final ObjectMapper objectMapper;

    public ContextBuilderService(ChunkRepository chunkRepository, ChunkRelationRepository chunkRelationRepository, ObjectMapper objectMapper) {
        this.chunkRepository = chunkRepository;
        this.chunkRelationRepository = chunkRelationRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public ContextAssembleResponse assembleContext(ContextAssembleRequest request) {
        List<String> anchorPublicIds = request.anchorChunks().stream().map(SearchResult::chunkId).toList();
        List<Chunk> anchors = chunkRepository.findByPublicIdIn(anchorPublicIds);

        Map<String, Chunk> chunkByPublicId = anchors.stream()
                .collect(Collectors.toMap(Chunk::getPublicId, c -> c, (a, b) -> a));

        List<Chunk> anchorChunksOrdered = request.anchorChunks().stream()
                .map(r -> chunkByPublicId.get(r.chunkId()))
                .filter(Objects::nonNull)
                .toList();

        Map<UUID, Chunk> contextChunksMap = new LinkedHashMap<>();
        Map<UUID, String> chunkRankSource = new HashMap<>();
        
        // 1. Add anchors
        for (Chunk anchor : anchorChunksOrdered) {
            contextChunksMap.putIfAbsent(anchor.getId(), anchor);
            chunkRankSource.putIfAbsent(anchor.getId(), "anchor");
        }

        // 2. Fetch direct relations if requested (tables/visuals)
        if (request.includeTables() || request.includeVisuals()) {
            List<UUID> anchorUuids = anchorChunksOrdered.stream().map(Chunk::getId).toList();
            List<String> relationTypes = new ArrayList<>();
            if (request.includeTables()) relationTypes.add("table_continuation");
            if (request.includeVisuals()) {
                relationTypes.add("caption_of_image");
                relationTypes.add("derived_from_visual");
            }
            
            if (!relationTypes.isEmpty()) {
                List<ChunkRelation> relations = chunkRelationRepository.findByFromChunkIdInAndRelationTypeIn(anchorUuids, relationTypes);
                List<UUID> relatedIds = relations.stream().map(ChunkRelation::getToChunkId).toList();
                if (!relatedIds.isEmpty()) {
                    List<Chunk> relatedChunks = chunkRepository.findAllById(relatedIds);
                    for (Chunk rc : relatedChunks) {
                        contextChunksMap.putIfAbsent(rc.getId(), rc);
                        chunkRankSource.putIfAbsent(rc.getId(), "direct_relation");
                    }
                }
            }
        }

        // 3. Fetch neighbors if sequential policy
        if ("sequential_neighbors_v1".equals(request.policy())) {
            for (Chunk anchor : anchorChunksOrdered) {
                if (anchor.getSequenceNumber() != null) {
                    int startSeq = Math.max(0, anchor.getSequenceNumber() - request.neighborBefore());
                    int endSeq = anchor.getSequenceNumber() + request.neighborAfter();
                    
                    List<Chunk> neighbors = chunkRepository.findNeighbors(
                            anchor.getWorkspaceId(), anchor.getDocumentId(), startSeq, endSeq);
                            
                    for (Chunk n : neighbors) {
                        if (!contextChunksMap.containsKey(n.getId())) {
                            contextChunksMap.put(n.getId(), n);
                            if (n.getSequenceNumber() < anchor.getSequenceNumber()) {
                                chunkRankSource.put(n.getId(), "neighbor_before");
                            } else if (n.getSequenceNumber() > anchor.getSequenceNumber()) {
                                chunkRankSource.put(n.getId(), "neighbor_after");
                            }
                        }
                    }
                }
            }
        }

        // 4. Deduplicate and group and sort
        // We will keep them in the order: anchors, then relations, then before, then after.
        // Actually, it's better to sort by documentId, filePageNumber, sequenceNumber 
        // to maintain reading flow, but keep groups by anchor rank if required by policy.
        // For sequential policy: groups by anchor rank.
        
        List<ContextItemResponse> finalItems = new ArrayList<>();
        List<ContextItemResponse> discarded = new ArrayList<>();
        List<Citation> citations = new ArrayList<>();
        
        int currentTokens = 0;
        boolean budgetHit = false;
        
        for (Chunk chunk : contextChunksMap.values()) {
            String rankSource = chunkRankSource.get(chunk.getId());
            int chunkTokens = chunk.getTokenCount() != null ? chunk.getTokenCount() : 0;
            
            if (currentTokens + chunkTokens > request.tokenBudget()) {
                budgetHit = true;
                
                // Try truncation if table or visual
                boolean canTruncate = "table_text".equals(chunk.getContentKind()) || "visual_interpretation".equals(chunk.getContentKind());
                
                if (canTruncate && currentTokens < request.tokenBudget()) {
                    // Fit what we can
                    int available = request.tokenBudget() - currentTokens;
                    if (available > 50) { // arbitrary small minimum
                        String truncatedContent = chunk.getContent().substring(0, Math.min(chunk.getContent().length(), available * 4)) + "... [TRUNCATED]";
                        String citationId = "cit_" + chunk.getPublicId();
                        finalItems.add(new ContextItemResponse(
                                "ctx_" + chunk.getPublicId(),
                                chunk.getPublicId(),
                                rankSource,
                                chunk.getContentKind(),
                                truncatedContent,
                                available,
                                true,
                                citationId
                        ));
                        citations.add(buildCitation(chunk, citationId));
                        currentTokens += available;
                        continue;
                    }
                }
                
                // Discard
                discarded.add(new ContextItemResponse(
                        "ctx_" + chunk.getPublicId(),
                        chunk.getPublicId(),
                        rankSource,
                        chunk.getContentKind(),
                        null,
                        chunkTokens,
                        false,
                        null
                ));
            } else {
                String citationId = "cit_" + chunk.getPublicId();
                finalItems.add(new ContextItemResponse(
                        "ctx_" + chunk.getPublicId(),
                        chunk.getPublicId(),
                        rankSource,
                        chunk.getContentKind(),
                        chunk.getContent(),
                        chunkTokens,
                        false,
                        citationId
                ));
                citations.add(buildCitation(chunk, citationId));
                currentTokens += chunkTokens;
            }
        }

        ContextAssembleResponse.ContextMetrics metrics = new ContextAssembleResponse.ContextMetrics(
                request.anchorChunks().size(),
                finalItems.size(),
                currentTokens
        );

        return new ContextAssembleResponse(
                "rag.context_assemble.response.v1",
                request.workspaceId(),
                request.collectionId(),
                request.policy(),
                request.tokenBudget(),
                budgetHit,
                finalItems,
                discarded,
                citations,
                metrics
        );
    }
    
    private Citation buildCitation(Chunk chunk, String citationId) {
        Map<String, Object> locator = null;
        Integer filePageNumber = null;
        String printedLabel = null;
        String sourceLabel = null;
        
        try {
            if (chunk.getSourceLocator() != null) {
                locator = objectMapper.readValue(chunk.getSourceLocator(), new TypeReference<>() {});
                if (locator.containsKey("filePageNumber")) {
                    filePageNumber = (Integer) locator.get("filePageNumber");
                }
                if (locator.containsKey("printedLabel")) {
                    printedLabel = (String) locator.get("printedLabel");
                }
            }
        } catch (Exception e) {
            // ignore
        }
        
        if (printedLabel != null && filePageNumber != null) {
            sourceLabel = "PDF p. " + filePageNumber + " / impresso p. " + printedLabel;
        } else if (filePageNumber != null) {
            sourceLabel = "PDF p. " + filePageNumber;
        }
        
        // Quote fallback: just a snippet of the chunk
        String quote = chunk.getContent();
        if (quote != null && quote.length() > 100) {
            quote = quote.substring(0, 100) + "...";
        }
        
        return new Citation(
                "rag.citation.v1",
                citationId,
                chunk.getDocumentId(),
                chunk.getPublicId(),
                locator != null ? (String) locator.get("elementId") : null,
                locator != null ? (String) locator.get("assetId") : null,
                "pdf_upload",
                filePageNumber,
                printedLabel,
                sourceLabel,
                quote,
                chunk.getContentKind(),
                true,
                true,
                locator
        );
    }
}
