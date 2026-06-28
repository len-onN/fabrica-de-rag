package local.fabricarag.core.service;

import local.fabricarag.core.dto.rag.*;
import local.fabricarag.core.port.out.AnswerProviderPort;
import local.fabricarag.core.port.out.AnswerProviderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RagAskServiceTest {

    private VectorSearchService vectorSearchService;
    private ContextBuilderService contextBuilderService;
    private AnswerProviderPort answerProviderPort;
    private RagAskService ragAskService;

    @BeforeEach
    void setUp() {
        vectorSearchService = mock(VectorSearchService.class);
        contextBuilderService = mock(ContextBuilderService.class);
        answerProviderPort = mock(AnswerProviderPort.class);
        ragAskService = new RagAskService(vectorSearchService, contextBuilderService, answerProviderPort);
    }

    @Test
    void shouldReturnInsufficientEvidenceWhenSearchIsLowConfidence() {
        AskRequest request = new AskRequest("wsp_1", "col_1", "O que é RAG?", 8, null, null, null, null, null);
        
        SearchResponse searchResponse = new SearchResponse(
                "rag.search.response.v1", "wsp_1", "col_1", "qry_1", "O que é RAG?",
                "vector_search_v1", 8, Map.of(),
                List.of(new SearchResult(1, "chk_1", "doc_1", 0.1, true, "text", "snippet", null)),
                new SearchResponse.SearchMetrics(10, 10, 20, 1)
        );
        
        when(vectorSearchService.search(any())).thenReturn(searchResponse);

        AskResponse response = ragAskService.ask(request);

        assertEquals("insufficient_evidence", response.status());
        assertNull(response.answer());
        assertEquals("no_reliable_context", response.reason());
        assertTrue(response.citations().isEmpty());
    }

    @Test
    void shouldReturnSuccessWhenContextIsAvailable() {
        AskRequest request = new AskRequest("wsp_1", "col_1", "O que é RAG?", 8, null, null, null, null, null);

        Citation citation = new Citation("rag.citation.v1", "cit_1", "doc_1", "chk_1", null, null, "pdf_upload", 1, "1", "PDF p. 1", "snippet", "text", true, true, Map.of());
        SearchResult searchResult = new SearchResult(1, "chk_1", "doc_1", 0.9, false, "text", "snippet", citation);
        
        SearchResponse searchResponse = new SearchResponse(
                "rag.search.response.v1", "wsp_1", "col_1", "qry_1", "O que é RAG?",
                "vector_search_v1", 8, Map.of(),
                List.of(searchResult),
                new SearchResponse.SearchMetrics(10, 10, 20, 1)
        );

        ContextItemResponse contextItem = new ContextItemResponse(
                "ctx_1", "chk_1", "anchor", "text", "RAG is cool", 10, false, "cit_1"
        );
        
        ContextAssembleResponse contextResponse = new ContextAssembleResponse(
                "rag.context_assemble.response.v1", "wsp_1", "col_1", "conservative", 5000, false,
                List.of(contextItem), Collections.emptyList(), List.of(citation),
                new ContextAssembleResponse.ContextMetrics(1, 1, 10)
        );

        AnswerProviderResponse llmResponse = new AnswerProviderResponse(
                "success", "RAG is a technique.", List.of("cit_1"), null
        );

        when(vectorSearchService.search(any())).thenReturn(searchResponse);
        when(contextBuilderService.assembleContext(any())).thenReturn(contextResponse);
        when(answerProviderPort.generateAnswer(any(), any())).thenReturn(llmResponse);

        AskResponse response = ragAskService.ask(request);

        assertEquals("success", response.status());
        assertEquals("RAG is a technique.", response.answer());
        assertNull(response.reason());
        assertEquals(1, response.citations().size());
        assertEquals("cit_1", response.citations().get(0).citationId());
    }
}
