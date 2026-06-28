package local.fabricarag.core.service;

import local.fabricarag.core.domain.*;
import local.fabricarag.core.dto.worker.*;
import local.fabricarag.core.exception.ResourceNotFoundException;
import local.fabricarag.core.repository.*;
import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.port.out.VectorStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Service
public class IngestRunPipelineService {

    private static final Logger logger = LoggerFactory.getLogger(IngestRunPipelineService.class);

    private final IngestRunRepository runRepository;
    private final IngestStepRepository stepRepository;
    private final DocumentRepository documentRepository;
    private final WorkerClient workerClient;
    private final VectorStorePort vectorStorePort;
    private final ChunkEmbeddingRepository chunkEmbeddingRepository;
    private final ChunkRepository chunkRepository;
    private final String apiBaseUrl;

    public IngestRunPipelineService(
            IngestRunRepository runRepository,
            IngestStepRepository stepRepository,
            DocumentRepository documentRepository,
            WorkerClient workerClient,
            VectorStorePort vectorStorePort,
            ChunkEmbeddingRepository chunkEmbeddingRepository,
            ChunkRepository chunkRepository,
            @Value("${fabricarag.api.url:http://localhost:8080}") String apiBaseUrl
    ) {
        this.runRepository = runRepository;
        this.stepRepository = stepRepository;
        this.documentRepository = documentRepository;
        this.workerClient = workerClient;
        this.vectorStorePort = vectorStorePort;
        this.chunkEmbeddingRepository = chunkEmbeddingRepository;
        this.chunkRepository = chunkRepository;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Transactional
    public void advancePipeline(UUID runId) {
        IngestRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found: " + runId));

        if (run.getStatus() == IngestRunStatus.CANCELLED || run.getStatus() == IngestRunStatus.FAILED || run.getStatus() == IngestRunStatus.COMPLETED) {
            logger.info("Run {} is in terminal state {}. Stopping pipeline.", runId, run.getStatus());
            return;
        }

        Document doc = documentRepository.findById(run.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + run.getDocumentId()));

        List<IngestStep> steps = stepRepository.findByRunIdOrderByCreatedAtAsc(runId);

        if (advanceStep(run, doc, steps, "inspect_pdf")) return;
        if (advanceStep(run, doc, steps, "render_pages")) return;
        if (advanceStep(run, doc, steps, "extract_text")) return;
        if (advanceStep(run, doc, steps, "build_chunks")) return;
        if (advanceStep(run, doc, steps, "generate_embeddings")) return;
        if (advanceStep(run, doc, steps, "index_qdrant")) return;
        
        if (!hasStep(steps, "finalize")) {
            executeFinalize(run, doc);
        }
    }

    private boolean hasStep(List<IngestStep> steps, String stepName) {
        return steps.stream().anyMatch(s -> s.getStepName().equals(stepName));
    }

    private boolean isStepCompletedOrSkipped(List<IngestStep> steps, String stepName) {
        return steps.stream()
                .filter(s -> s.getStepName().equals(stepName))
                .findFirst()
                .map(s -> s.getStatus() == IngestStepStatus.COMPLETED || s.getStatus() == IngestStepStatus.SKIPPED)
                .orElse(false);
    }

    private IngestStep getOrCreateStep(IngestRun run, List<IngestStep> steps, String stepName) {
        Optional<IngestStep> existing = steps.stream().filter(s -> s.getStepName().equals(stepName)).findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        
        IngestStep newStep = new IngestStep(
                UUID.randomUUID(),
                "step_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20),
                run.getId(),
                stepName,
                run.getIdempotencyKey() + ":" + stepName
        );
        return stepRepository.save(newStep);
    }

    private boolean advanceStep(IngestRun run, Document doc, List<IngestStep> steps, String stepName) {
        if (isStepCompletedOrSkipped(steps, stepName)) {
            return false; 
        }

        IngestStep step = getOrCreateStep(run, steps, stepName);

        if (step.getStatus() == IngestStepStatus.RUNNING || step.getStatus() == IngestStepStatus.WAITING_FOR_REVIEW) {
            return true; 
        }

        if (step.getStatus() == IngestStepStatus.QUEUED) {
            step.markStarted();
            stepRepository.save(step);
            run.updateStatus(IngestRunStatus.RUNNING);
            runRepository.save(run);
            
            try {
                executeStepAction(run, doc, step);
            } catch (Exception e) {
                logger.error("Failed to execute step " + stepName, e);
                step.markFailed("execution_error", e.getMessage(), Map.of());
                stepRepository.save(step);
                run.updateStatus(IngestRunStatus.FAILED);
                runRepository.save(run);
            }
            return true; 
        }

        return true; 
    }

    private void executeStepAction(IngestRun run, Document doc, IngestStep step) {
        String callbackUrl = apiBaseUrl + "/api/v1/internal/callbacks/ingest-runs/" + run.getId() + "/pdf/";

        switch (step.getStepName()) {
            case "inspect_pdf":
                doc.updateStatus("analyzing");
                documentRepository.save(doc);
                workerClient.inspectPdf(PdfInspectRequest.create(
                        UUID.randomUUID().toString(),
                        run.getWorkspaceId(),
                        doc.getId(),
                        doc.getStorageUri(),
                        callbackUrl + "inspect"
                ));
                break;
            case "render_pages":
                PdfRenderRequest renderReq = new PdfRenderRequest();
                renderReq.setRequestId(UUID.randomUUID().toString());
                renderReq.setWorkspaceId(run.getWorkspaceId().toString());
                renderReq.setCallbackUrl(callbackUrl + "render-page");
                renderReq.setDocumentId(doc.getPublicId());
                renderReq.setStorageUri(doc.getStorageUri());
                renderReq.setPageNumber(1); 
                workerClient.requestRenderPage(renderReq);
                break;
            case "extract_text":
                PdfExtractTextRequest textReq = new PdfExtractTextRequest();
                textReq.setRequestId(UUID.randomUUID().toString());
                textReq.setWorkspaceId(run.getWorkspaceId().toString());
                textReq.setCallbackUrl(callbackUrl + "extract-text");
                textReq.setDocumentId(doc.getPublicId());
                textReq.setStorageUri(doc.getStorageUri());
                textReq.setPageNumber(1);
                workerClient.requestExtractText(textReq);
                break;
            case "build_chunks":
                step.markCompleted(Map.of());
                stepRepository.save(step);
                advancePipeline(run.getId());
                break;
            case "generate_embeddings":
                EmbeddingsTextRequest embReq = new EmbeddingsTextRequest();
                embReq.setRequestId(UUID.randomUUID().toString());
                embReq.setWorkspaceId(run.getWorkspaceId().toString());
                embReq.setCallbackUrl(apiBaseUrl + "/api/v1/internal/callbacks/ingest-runs/" + run.getId() + "/embeddings/text");
                embReq.setEmbeddingModel("mock-text-embedding-v1");
                embReq.setVectorSpace("default");
                workerClient.requestEmbeddings(embReq);
                break;
            case "index_qdrant":
                doc.updateStatus("indexing");
                documentRepository.save(doc);
                // Indexing to Qdrant is done asynchronously when receiving the EmbeddingsTextResponse payload.
                // We just mark this step as completed.
                step.markCompleted(Map.of());
                stepRepository.save(step);
                advancePipeline(run.getId());
                break;
            default:
                throw new IllegalStateException("Unknown step: " + step.getStepName());
        }
    }

    private void executeFinalize(IngestRun run, Document doc) {
        IngestStep step = new IngestStep(
                UUID.randomUUID(),
                "step_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20),
                run.getId(),
                "finalize",
                run.getIdempotencyKey() + ":finalize"
        );
        step.markStarted();
        stepRepository.save(step);
        
        doc.updateStatus("ready");
        documentRepository.save(doc);
        
        run.updateStatus(IngestRunStatus.COMPLETED);
        runRepository.save(run);
        
        step.markCompleted(Map.of());
        stepRepository.save(step);
    }
    
    @Transactional
    public void handleWorkerSuccess(UUID runId, String stepName, Map<String, Object> details) {
        List<IngestStep> steps = stepRepository.findByRunIdOrderByCreatedAtAsc(runId);
        steps.stream().filter(s -> s.getStepName().equals(stepName)).findFirst().ifPresent(step -> {
            step.markCompleted(details);
            stepRepository.save(step);
        });
        advancePipeline(runId);
    }

    @Transactional
    public void handleEmbeddingsSuccess(UUID runId, EmbeddingsTextResponse response) {
        IngestRun run = runRepository.findById(runId).orElseThrow();
        Document doc = documentRepository.findById(run.getDocumentId()).orElseThrow();
        
        List<ChunkEmbedding> savedEmbeddings = new java.util.ArrayList<>();
        List<Chunk> chunks = new java.util.ArrayList<>();
        List<List<Float>> vectors = new java.util.ArrayList<>();
        
        for (EmbeddingsTextResponse.Item item : response.getItems()) {
            Chunk chunk = chunkRepository.findByPublicId(item.getItemId()).orElse(null);
            if (chunk == null) continue;
            
            ChunkEmbedding embedding = new ChunkEmbedding(
                    UUID.randomUUID(),
                    "emb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20),
                    run.getWorkspaceId().toString(),
                    chunk.getId(),
                    UUID.randomUUID(), // fake vectorBindingId for MVP
                    response.getEmbeddingModel(),
                    response.getEmbeddingModelVersion(),
                    response.getDimension(),
                    response.getDistanceMetric(),
                    response.getVectorSpace(),
                    UUID.randomUUID().toString(), // qdrant point ID
                    response.getContractVersion(),
                    "active"
            );
            savedEmbeddings.add(embedding);
            chunks.add(chunk);
            vectors.add(item.getVector().stream().map(Double::floatValue).toList());
        }
        
        if (!savedEmbeddings.isEmpty()) {
            chunkEmbeddingRepository.saveAll(savedEmbeddings);
            
            // Upsert in batches of 100
            int batchSize = 100;
            for (int i = 0; i < savedEmbeddings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, savedEmbeddings.size());
                vectorStorePort.upsertBatch(
                        savedEmbeddings.subList(i, end),
                        chunks.subList(i, end),
                        doc,
                        null, // collection optional for MVP if we use default
                        null, // binding optional
                        vectors.subList(i, end)
                );
            }
        }

        handleWorkerSuccess(runId, "generate_embeddings", Map.of("indexedCount", savedEmbeddings.size()));
    }

    @Transactional
    public void handleWorkerError(UUID runId, String stepName, String errorCode, String errorMessage) {
        List<IngestStep> steps = stepRepository.findByRunIdOrderByCreatedAtAsc(runId);
        steps.stream().filter(s -> s.getStepName().equals(stepName)).findFirst().ifPresent(step -> {
            step.markFailed(errorCode, errorMessage, Map.of());
            stepRepository.save(step);
        });
        
        IngestRun run = runRepository.findById(runId).orElseThrow();
        run.updateStatus(IngestRunStatus.FAILED);
        runRepository.save(run);
        
        Document doc = documentRepository.findById(run.getDocumentId()).orElseThrow();
        doc.updateStatus("failed");
        documentRepository.save(doc);
    }
}
