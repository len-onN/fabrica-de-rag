package local.fabricarag.core.service;

import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.domain.Document;
import local.fabricarag.core.domain.IngestRun;
import local.fabricarag.core.domain.IngestStep;
import local.fabricarag.core.dto.worker.PdfInspectRequest;
import local.fabricarag.core.port.out.VectorStorePort;
import local.fabricarag.core.repository.ChunkEmbeddingRepository;
import local.fabricarag.core.repository.ChunkRepository;
import local.fabricarag.core.repository.DocumentRepository;
import local.fabricarag.core.repository.IngestRunRepository;
import local.fabricarag.core.repository.IngestStepRepository;
import local.fabricarag.core.domain.analytics.AnalyticsEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngestRunPipelineServiceTest {

    private IngestRunRepository runRepository;
    private IngestStepRepository stepRepository;
    private DocumentRepository documentRepository;
    private WorkerClient workerClient;
    private ChunkEmbeddingRepository chunkEmbeddingRepository;
    private ChunkRepository chunkRepository;
    private VectorStorePort vectorStorePort;
    private AnalyticsEventService analyticsEventService;
    private IngestRunPipelineService pipelineService;

    @BeforeEach
    void setUp() {
        analyticsEventService = mock(AnalyticsEventService.class);
        runRepository = mock(IngestRunRepository.class);
        stepRepository = mock(IngestStepRepository.class);
        documentRepository = mock(DocumentRepository.class);
        workerClient = mock(WorkerClient.class);
        chunkEmbeddingRepository = mock(ChunkEmbeddingRepository.class);
        chunkRepository = mock(ChunkRepository.class);
        vectorStorePort = mock(VectorStorePort.class);

        pipelineService = new IngestRunPipelineService(
                runRepository,
                stepRepository,
                documentRepository,
                workerClient,
                vectorStorePort,
                chunkEmbeddingRepository,
                chunkRepository,
                analyticsEventService,
                "http://localhost:8080"
        );
    }

    @Test
    void shouldStartInspectPdfStepWhenPipelineStarts() {
        UUID runId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        
        IngestRun run = new IngestRun(runId, "run_123", workspaceId, documentId, "idemp_1", UUID.randomUUID());
        Document document = new Document(
                documentId, "doc_123", workspaceId, UUID.randomUUID(), "upload", "s3://uri",
                "hash", "file.pdf", "application/pdf", 1024L, "uploaded", 0, "s3://uri", UUID.randomUUID()
        );
        
        when(runRepository.findById(runId)).thenReturn(Optional.of(run));
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        when(stepRepository.findByRunIdOrderByCreatedAtAsc(runId)).thenReturn(Collections.emptyList());
        when(stepRepository.save(any(IngestStep.class))).thenAnswer(i -> i.getArgument(0));
        when(runRepository.save(any(IngestRun.class))).thenAnswer(i -> i.getArgument(0));
        when(documentRepository.save(any(Document.class))).thenAnswer(i -> i.getArgument(0));
        
        pipelineService.advancePipeline(runId);
        
        ArgumentCaptor<IngestStep> stepCaptor = ArgumentCaptor.forClass(IngestStep.class);
        verify(stepRepository, times(2)).save(stepCaptor.capture()); // saved twice during creation/start
        
        assertEquals("inspect_pdf", stepCaptor.getAllValues().get(0).getStepName());
        
        ArgumentCaptor<PdfInspectRequest> reqCaptor = ArgumentCaptor.forClass(PdfInspectRequest.class);
        verify(workerClient).inspectPdf(reqCaptor.capture());
        
        assertEquals("s3://uri", reqCaptor.getValue().storageUri());
        assertEquals("http://localhost:8080/api/v1/internal/callbacks/ingest-runs/" + runId + "/pdf/inspect", reqCaptor.getValue().callbackUrl());
    }
}
