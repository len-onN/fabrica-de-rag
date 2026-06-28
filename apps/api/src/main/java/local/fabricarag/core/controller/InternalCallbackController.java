package local.fabricarag.core.controller;

import local.fabricarag.core.dto.worker.PdfInspectResponse;
import local.fabricarag.core.dto.worker.PdfRenderResponse;
import local.fabricarag.core.dto.worker.PdfExtractTextResponse;
import local.fabricarag.core.dto.worker.PdfExtractElementsResponse;
import local.fabricarag.core.dto.worker.PdfInterpretVisualResponse;
import local.fabricarag.core.dto.worker.base.WorkerErrorResponse;
import local.fabricarag.core.service.IngestRunOperationService;
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

    public InternalCallbackController(IngestRunOperationService ingestRunOperationService) {
        this.ingestRunOperationService = ingestRunOperationService;
    }

    /**
     * Endpoint for the Python worker to post inspection results (or errors) asynchronously.
     */
    @PostMapping("/{runId}/inspect")
    public ResponseEntity<Void> onInspectCompleted(
            @PathVariable UUID runId,
            @RequestBody Map<String, Object> payload
    ) {
        ingestRunOperationService.handleInspectCallback(runId, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint for the Python worker to post inspection results (or errors) asynchronously.
     */
    @PostMapping("/pdf/inspect")
    public ResponseEntity<Void> onPdfInspectionSuccess(@Valid @RequestBody PdfInspectResponse response) {
        logger.info("Received PDF inspection success callback for request {}", response.requestId());
        // For MVP, we just log. Real implementation will update the IngestRun state.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pdf/render-page")
    public ResponseEntity<Void> onPdfRenderSuccess(@Valid @RequestBody PdfRenderResponse response) {
        logger.info("Received PDF render success callback for request {}", response.getRequestId());
        // For MVP, we just log.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pdf/extract-text")
    public ResponseEntity<Void> onPdfExtractSuccess(@Valid @RequestBody PdfExtractTextResponse response) {
        logger.info("Received PDF extract text success callback for request {}", response.getRequestId());
        // For MVP, we just log.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pdf/error")
    public ResponseEntity<Void> onPdfError(@Valid @RequestBody WorkerErrorResponse errorResponse) {
        logger.info("Received PDF error callback for request {}", errorResponse.getRequestId());
        // For MVP, we just log.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pdf/extract-elements")
    public ResponseEntity<Void> onPdfExtractElementsSuccess(@Valid @RequestBody PdfExtractElementsResponse response) {
        logger.info("Received PDF extract elements success callback for request {}", response.getRequestId());
        // For MVP, we just log.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pdf/interpret-visual")
    public ResponseEntity<Void> onPdfInterpretVisualSuccess(@Valid @RequestBody PdfInterpretVisualResponse response) {
        logger.info("Received PDF interpret visual success callback for request {}", response.getRequestId());
        // For MVP, we just log.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/embeddings/text")
    public ResponseEntity<Void> onEmbeddingsTextSuccess(@Valid @RequestBody local.fabricarag.core.dto.worker.EmbeddingsTextResponse response) {
        logger.info("Received embeddings text success callback for request {}", response.getRequestId());
        // For MVP, we just log for now as the logic is in EmbeddingsService
        return ResponseEntity.ok().build();
    }
}
