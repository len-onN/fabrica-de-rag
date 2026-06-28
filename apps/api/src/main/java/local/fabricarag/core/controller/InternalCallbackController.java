package local.fabricarag.core.controller;

import local.fabricarag.core.service.IngestRunOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/callbacks/ingest-runs")
public class InternalCallbackController {

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
}
