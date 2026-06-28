package local.fabricarag.core.service;

import local.fabricarag.core.domain.*;
import local.fabricarag.core.dto.*;
import local.fabricarag.core.exception.ResourceNotFoundException;
import local.fabricarag.core.repository.*;
import local.fabricarag.core.client.WorkerClient;
import local.fabricarag.core.dto.worker.PdfInspectRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IngestRunOperationService {
    private final IngestRunRepository runRepository;
    private final IngestStepRepository stepRepository;
    private final IngestRunLogRepository logRepository;
    private final DocumentRepository documentRepository;
    private final WorkerClient workerClient;

    public IngestRunOperationService(IngestRunRepository runRepository, IngestStepRepository stepRepository,
                                     IngestRunLogRepository logRepository, DocumentRepository documentRepository,
                                     WorkerClient workerClient) {
        this.runRepository = runRepository;
        this.stepRepository = stepRepository;
        this.logRepository = logRepository;
        this.documentRepository = documentRepository;
        this.workerClient = workerClient;
    }

    @Transactional(readOnly = true)
    public IngestRunDetailResponse getRunDetails(UUID workspaceId, String runPublicId) {
        IngestRun run = runRepository.findByWorkspaceIdAndPublicId(workspaceId, runPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found"));

        Document doc = documentRepository.findById(run.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        List<IngestStep> steps = stepRepository.findByRunIdOrderByCreatedAtAsc(run.getId());
        List<IngestStepResponse> stepResponses = steps.stream().map(s -> new IngestStepResponse(
                s.getPublicId(), s.getStepName(), s.getStatus(), s.getAttempt(),
                s.getStartedAt(), s.getFinishedAt(), s.getDurationMs(),
                s.getErrorCode(), s.getErrorMessage(), s.getSafeDetails()
        )).collect(Collectors.toList());
        
        String retryOfRunPublicId = null;
        if (run.getRetryOfRunId() != null) {
             retryOfRunPublicId = runRepository.findById(run.getRetryOfRunId()).map(IngestRun::getPublicId).orElse(null);
        }
        String reprocessOfRunPublicId = null;
        if (run.getReprocessOfRunId() != null) {
             reprocessOfRunPublicId = runRepository.findById(run.getReprocessOfRunId()).map(IngestRun::getPublicId).orElse(null);
        }

        return new IngestRunDetailResponse(
                run.getPublicId(),
                doc.getPublicId(),
                run.getStatus(),
                run.getCreatedAt(),
                run.getUpdatedAt(),
                retryOfRunPublicId,
                reprocessOfRunPublicId,
                stepResponses
        );
    }
    
    @Transactional
    public void cancelRun(UUID workspaceId, String runPublicId) {
        IngestRun run = runRepository.findByWorkspaceIdAndPublicId(workspaceId, runPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found"));
                
        if (run.getStatus() == IngestRunStatus.QUEUED || run.getStatus() == IngestRunStatus.RUNNING || run.getStatus() == IngestRunStatus.WAITING_FOR_REVIEW) {
            run.updateStatus(IngestRunStatus.CANCELLED);
            runRepository.save(run);
            
            List<IngestStep> steps = stepRepository.findByRunIdOrderByCreatedAtAsc(run.getId());
            for (IngestStep step : steps) {
                 if (step.getStatus() == IngestStepStatus.QUEUED || step.getStatus() == IngestStepStatus.WAITING_FOR_REVIEW) {
                     step.updateStatus(IngestStepStatus.CANCELLED);
                     stepRepository.save(step);
                 }
            }
        } else {
            throw new IllegalStateException("Run cannot be cancelled from status: " + run.getStatus());
        }
    }
    
    @Transactional
    public IngestRunDetailResponse retryRun(UUID workspaceId, String runPublicId, UUID userId) {
        IngestRun run = runRepository.findByWorkspaceIdAndPublicId(workspaceId, runPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found"));
                
        if (run.getStatus() != IngestRunStatus.FAILED) {
            throw new IllegalStateException("Only failed runs can be retried");
        }
        
        UUID newRunId = UUID.randomUUID();
        String newPublicId = "run_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        IngestRun newRun = new IngestRun(
                newRunId,
                newPublicId,
                workspaceId,
                run.getDocumentId(),
                run.getIdempotencyKey(),
                userId,
                run.getId(),
                null
        );
        runRepository.save(newRun);
        return getRunDetails(workspaceId, newPublicId);
    }
    
    @Transactional(readOnly = true)
    public Page<IngestRunLogResponse> getRunLogs(UUID workspaceId, String runPublicId, Pageable pageable) {
        IngestRun run = runRepository.findByWorkspaceIdAndPublicId(workspaceId, runPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found"));
                
        return logRepository.findByRunIdOrderByCreatedAtAsc(run.getId(), pageable).map(l -> {
             String stepPublicId = null;
             if (l.getStepId() != null) {
                 stepPublicId = stepRepository.findById(l.getStepId()).map(IngestStep::getPublicId).orElse(null);
             }
             return new IngestRunLogResponse(l.getLevel(), l.getMessage(), l.getDetails(), l.getCreatedAt(), stepPublicId);
        });
    }

    @Transactional
    public void startInspectStep(UUID runId, String storageUri, String apiBaseUrl) {
        IngestRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found"));
        
        // Em um fluxo real, a etapa IngestStep seria criada aqui e marcada como RUNNING.
        
        String callbackUrl = apiBaseUrl + "/api/v1/internal/callbacks/ingest-runs/" + runId + "/inspect";
        
        PdfInspectRequest request = PdfInspectRequest.create(
                UUID.randomUUID().toString(),
                run.getWorkspaceId(),
                run.getDocumentId(),
                storageUri,
                callbackUrl
        );
        
        workerClient.inspectPdf(request);
    }

    @Transactional
    public void handleInspectCallback(UUID runId, Map<String, Object> payload) {
        IngestRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run not found"));
        
        if (payload.containsKey("error")) {
            run.updateStatus(IngestRunStatus.FAILED);
            // Salva log de erro na IngestRunLog aqui
        } else {
            // Sucesso na inspeção
            // No MVP real, aqui pegaria pageCount, encrypted etc e salvaria no IngestStep
            // e avançaria para a próxima etapa (ex: render ou chunking)
            run.updateStatus(IngestRunStatus.WAITING_FOR_REVIEW); // ou continua
        }
        
        runRepository.save(run);
    }
}
