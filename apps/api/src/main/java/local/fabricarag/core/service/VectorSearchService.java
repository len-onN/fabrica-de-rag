package local.fabricarag.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.domain.Chunk;
import local.fabricarag.core.dto.rag.Citation;
import local.fabricarag.core.dto.rag.SearchRequest;
import local.fabricarag.core.dto.rag.SearchResponse;
import local.fabricarag.core.dto.rag.SearchResult;
import local.fabricarag.core.dto.worker.EmbeddingsTextRequest;
import local.fabricarag.core.dto.worker.EmbeddingsTextResponse;
import local.fabricarag.core.port.out.VectorStorePort;
import local.fabricarag.core.port.out.VectorStoreSearchResult;
import local.fabricarag.core.repository.ChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VectorSearchService.class);
    private static final String DEFAULT_COLLECTION_NAME = "ragcreator_chunks_v1";

    private final WorkerClient workerClient;
    private final VectorStorePort vectorStorePort;
    private final ChunkRepository chunkRepository;
    private final ObjectMapper objectMapper;

    public VectorSearchService(WorkerClient workerClient, VectorStorePort vectorStorePort, ChunkRepository chunkRepository, ObjectMapper objectMapper) {
        this.workerClient = workerClient;
        this.vectorStorePort = vectorStorePort;
        this.chunkRepository = chunkRepository;
        this.objectMapper = objectMapper;
    }

    public SearchResponse search(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 1. Embed query
        String reqId = UUID.randomUUID().toString();
        EmbeddingsTextRequest.Item reqItem = new EmbeddingsTextRequest.Item();
        reqItem.setItemId("query");
        reqItem.setContent(request.query());
        reqItem.setContentKind("text");
        reqItem.setSourceHash("hash_not_needed");

        EmbeddingsTextRequest embedReq = new EmbeddingsTextRequest();
        embedReq.setRequestId(reqId);
        embedReq.setWorkspaceId(request.workspaceId());
        embedReq.setEmbeddingModel("mock-text-embedding-v1");
        embedReq.setItems(List.of(reqItem));

        EmbeddingsTextResponse embedRes = workerClient.requestEmbeddingsSync(embedReq);
        if (embedRes.getItems() == null || embedRes.getItems().isEmpty()) {
            throw new RuntimeException("Worker failed to generate embedding for query");
        }
        
        List<Float> queryVector = embedRes.getItems().get(0).getVector().stream()
                .map(Double::floatValue)
                .collect(Collectors.toList());
        
        // 2. Prepare filters and search in Qdrant
        Map<String, Object> qdrantFilters = new HashMap<>();
        qdrantFilters.put("workspaceId", request.workspaceId());
        if (request.collectionId() != null) {
            qdrantFilters.put("collectionId", request.collectionId());
        }
        qdrantFilters.put("payloadContractVersion", "qdrant.chunk.v1");
        qdrantFilters.put("vectorSpace", "text_chunks_v1");
        
        if (request.filters() != null) {
            qdrantFilters.putAll(request.filters());
        }

        int topK = request.topK() != null ? request.topK() : 8;
        
        long qdrantStartTime = System.currentTimeMillis();
        List<VectorStoreSearchResult> qdrantResults = vectorStorePort.search(DEFAULT_COLLECTION_NAME, queryVector, topK, qdrantFilters);
        long qdrantLatencyMs = System.currentTimeMillis() - qdrantStartTime;

        if (qdrantResults.isEmpty()) {
            return buildEmptyResponse(request, qdrantFilters, qdrantLatencyMs, System.currentTimeMillis() - startTime);
        }

        // 3. Fetch chunks from SQL
        long sqlStartTime = System.currentTimeMillis();
        List<String> chunkIds = qdrantResults.stream()
                .map(r -> (String) r.payload().get("chunkId"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Chunk> sqlChunks = chunkRepository.findByPublicIdIn(chunkIds);
        Map<String, Chunk> chunksById = sqlChunks.stream().collect(Collectors.toMap(Chunk::getPublicId, c -> c));
        long sqlLatencyMs = System.currentTimeMillis() - sqlStartTime;

        // 4. Map to response
        List<SearchResult> finalResults = new ArrayList<>();
        int rank = 1;
        
        boolean lowConfidenceAll = false;
        double bestScore = qdrantResults.get(0).score();
        if (bestScore < 0.35 || qdrantResults.size() < 2) {
            lowConfidenceAll = true;
        }

        for (VectorStoreSearchResult pt : qdrantResults) {
            String chunkPublicId = (String) pt.payload().get("chunkId");
            Chunk chunk = chunksById.get(chunkPublicId);
            if (chunk == null) continue; // Skip if chunk was deleted but still in qdrant

            String contentSnippet = chunk.getContent();
            if (contentSnippet != null && contentSnippet.length() > 200) {
                contentSnippet = contentSnippet.substring(0, 200) + "...";
            }

            Citation citation = buildCitation(chunk, contentSnippet);
            
            SearchResult res = new SearchResult(
                    rank++,
                    chunk.getPublicId(),
                    chunk.getDocumentId(),
                    pt.score(),
                    lowConfidenceAll,
                    chunk.getContentKind(),
                    contentSnippet,
                    citation
            );
            finalResults.add(res);
        }
        
        if (finalResults.isEmpty()) {
            return buildEmptyResponse(request, qdrantFilters, qdrantLatencyMs, System.currentTimeMillis() - startTime);
        }

        long totalLatencyMs = System.currentTimeMillis() - startTime;
        SearchResponse.SearchMetrics metrics = new SearchResponse.SearchMetrics(qdrantLatencyMs, sqlLatencyMs, totalLatencyMs, finalResults.size());

        Map<String, String> appliedFiltersStr = qdrantFilters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));

        return new SearchResponse(
                "rag.search.response.v1",
                request.workspaceId(),
                request.collectionId(),
                "qry_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                request.query(),
                "vector_search_v1",
                topK,
                appliedFiltersStr,
                finalResults,
                metrics
        );
    }

    private Citation buildCitation(Chunk chunk, String quote) {
        Map<String, Object> sourceLocator = Collections.emptyMap();
        try {
            if (chunk.getSourceLocator() != null) {
                sourceLocator = objectMapper.readValue(chunk.getSourceLocator(), new TypeReference<>() {});
            }
        } catch (Exception e) {
            logger.warn("Failed to parse source locator for chunk {}", chunk.getPublicId());
        }

        Integer filePage = null;
        if (sourceLocator.get("filePageNumber") instanceof Number num) {
            filePage = num.intValue();
        }
        
        String printedLabel = (String) sourceLocator.get("printedLabel");
        
        String sourceLabel;
        if (printedLabel != null && !printedLabel.isEmpty()) {
            sourceLabel = String.format("PDF p. %s / impresso p. %s", filePage != null ? filePage : "?", printedLabel);
        } else {
            sourceLabel = String.format("PDF p. %s", filePage != null ? filePage : "?");
        }
        
        return new Citation(
                "rag.citation.v1",
                "cit_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                chunk.getDocumentId(),
                chunk.getPublicId(),
                (String) sourceLocator.get("sourceElementId"),
                (String) sourceLocator.get("assetId"),
                (String) sourceLocator.getOrDefault("sourceType", "pdf_upload"),
                filePage,
                printedLabel,
                sourceLabel,
                quote,
                chunk.getContentKind(),
                filePage != null,
                true,
                sourceLocator
        );
    }

    private SearchResponse buildEmptyResponse(SearchRequest request, Map<String, Object> filters, long qdrantLatency, long totalLatency) {
        Map<String, String> appliedFiltersStr = filters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
        
        return new SearchResponse(
                "rag.search.response.v1",
                request.workspaceId(),
                request.collectionId(),
                "qry_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                request.query(),
                "vector_search_v1",
                request.topK() != null ? request.topK() : 8,
                appliedFiltersStr,
                Collections.emptyList(),
                new SearchResponse.SearchMetrics(qdrantLatency, 0, totalLatency, 0)
        );
    }
}
