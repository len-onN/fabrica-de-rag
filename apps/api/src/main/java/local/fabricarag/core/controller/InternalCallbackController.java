package local.fabricarag.core.controller;

import local.fabricarag.core.dto.worker.PdfInspectResponse;
import local.fabricarag.core.dto.worker.PdfRenderResponse;
import local.fabricarag.core.dto.worker.PdfExtractTextResponse;
import local.fabricarag.core.dto.worker.PdfExtractElementsResponse;
import local.fabricarag.core.dto.worker.PdfInterpretVisualResponse;
import local.fabricarag.core.dto.worker.*;
import local.fabricarag.core.dto.worker.base.WorkerErrorResponse;
import local.fabricarag.core.service.IngestRunOperationService;
import local.fabricarag.core.service.IngestRunPipelineService;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/callbacks/ingest-runs")
public class InternalCallbackController {

    private static final Logger logger = LoggerFactory.getLogger(InternalCallbackController.class);

    private final IngestRunOperationService ingestRunOperationService;
    private final IngestRunPipelineService pipelineService;

    public InternalCallbackController(IngestRunOperationService ingestRunOperationService, IngestRunPipelineService pipelineService) {
        this.ingestRunOperationService = ingestRunOperationService;
        this.pipelineService = pipelineService;
    }

    /**
     * Endpoint for the Python worker to post inspection results (or errors) asynchronously.
     */
    @PostMapping("/{runId}/pdf/inspect")
    public ResponseEntity<Void> onPdfInspectionSuccess(
            @PathVariable UUID runId,
            @Valid @RequestBody PdfInspectResponse response
    ) {
        logger.info("Received PDF inspection success callback for run {} and request {}", runId, response.requestId());
        // For record types, we need to pass a Map or handle it manually. We'll pass empty map for MVP success.
        pipelineService.handleWorkerSuccess(runId, "inspect_pdf", Map.of("pageCount", response.pageCount()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runId}/pdf/render-page")
    public ResponseEntity<Void> onPdfRenderSuccess(
            @PathVariable UUID runId,
            @Valid @RequestBody PdfRenderResponse response
    ) {
        logger.info("Received PDF render success callback for run {} and request {}", runId, response.getRequestId());
        pipelineService.handleWorkerSuccess(runId, "render_pages", Map.of("imageUri", response.getImageUri()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runId}/pdf/extract-text")
    public ResponseEntity<Void> onPdfExtractSuccess(
            @PathVariable UUID runId,
            @Valid @RequestBody PdfExtractTextResponse response
    ) {
        logger.info("Received PDF extract text success callback for run {} and request {}", runId, response.getRequestId());
        pipelineService.handleWorkerSuccess(runId, "extract_text", Map.of("pageNumber", response.getPageNumber()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runId}/pdf/error")
    public ResponseEntity<Void> onPdfError(
            @PathVariable UUID runId,
            @Valid @RequestBody WorkerErrorResponse errorResponse
    ) {
        logger.info("Received PDF error callback for run {} and request {}", runId, errorResponse.getRequestId());
        String errStr = errorResponse.getError() != null ? errorResponse.getError().toString() : "unknown";
        pipelineService.handleWorkerError(runId, "current_step_unknown", "WORKER_ERROR", errStr);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runId}/pdf/extract-elements")
    public ResponseEntity<Void> onPdfExtractElementsSuccess(
            @PathVariable UUID runId,
            @Valid @RequestBody PdfExtractElementsResponse response
    ) {
        logger.info("Received PDF extract elements success callback for run {} and request {}", runId, response.getRequestId());
        pipelineService.handleWorkerSuccess(runId, "extract_elements", Map.of());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runId}/pdf/interpret-visual")
    public ResponseEntity<Void> onPdfInterpretVisualSuccess(
            @PathVariable UUID runId,
            @Valid @RequestBody PdfInterpretVisualResponse response
    ) {
        logger.info("Received PDF interpret visual success callback for run {} and request {}", runId, response.getRequestId());
        pipelineService.handleWorkerSuccess(runId, "interpret_visual", Map.of());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{runId}/embeddings/text")
    public ResponseEntity<Void> onEmbeddingsTextSuccess(
            @PathVariable UUID runId,
            @Valid @RequestBody local.fabricarag.core.dto.worker.EmbeddingsTextResponse response
    ) {
        logger.info("Received embeddings text success callback for run {} and request {}", runId, response.getRequestId());
        pipelineService.handleEmbeddingsSuccess(runId, response);
        return ResponseEntity.ok().build();
    }
}
