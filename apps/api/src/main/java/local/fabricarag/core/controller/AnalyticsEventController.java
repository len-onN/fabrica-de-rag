package local.fabricarag.core.controller;

import jakarta.validation.Valid;
import local.fabricarag.core.domain.analytics.ActorType;
import local.fabricarag.core.domain.analytics.AnalyticsEvent;
import local.fabricarag.core.domain.analytics.AnalyticsEventRepository;
import local.fabricarag.core.domain.analytics.AnalyticsEventService;
import local.fabricarag.core.domain.analytics.RetentionClass;
import local.fabricarag.core.dto.analytics.AnalyticsEventRequest;
import local.fabricarag.core.security.AuthorizationPolicy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/analytics")
public class AnalyticsEventController {

    private final AnalyticsEventService service;
    private final AnalyticsEventRepository repository;
    private final AuthorizationPolicy authorizationPolicy;

    public AnalyticsEventController(AnalyticsEventService service, AnalyticsEventRepository repository, AuthorizationPolicy authorizationPolicy) {
        this.service = service;
        this.repository = repository;
        this.authorizationPolicy = authorizationPolicy;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'workspace.read')")
    public void recordEvent(@PathVariable String workspaceId, @Valid @RequestBody AnalyticsEventRequest request) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventVersion(request.eventVersion());
        event.setEventName(request.eventName());
        event.setOrigin(request.origin());
        event.setRetentionClass(request.retentionClass());
        event.setWorkspaceId(workspaceId);
        
        if (request.actor() != null) {
            event.setActorType(request.actor().type());
            event.setActorId(request.actor().id());
        } else {
            event.setActorType(ActorType.SYSTEM);
        }

        event.setCorrelationId(request.correlationId());
        event.setOccurredAt(request.occurredAt());
        
        if (request.resource() != null) {
            event.setResourceType(request.resource().type());
            event.setResourceId(request.resource().id());
        }
        
        event.setProperties(request.properties());

        // Publish asynchronously
        service.publishEvent(event);
    }

    @DeleteMapping("/events")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'analytics.delete')")
    public void deleteAnalytics(@PathVariable String workspaceId) {
        // Enforce max volume limit to 0 for Analytics and History classes
        repository.enforceMaxVolumeLimit(workspaceId, RetentionClass.ANALYTICS.name(), 0);
        repository.enforceMaxVolumeLimit(workspaceId, RetentionClass.HISTORY.name(), 0);
        // Leave RUN_LOG and AUDIT_MINIMUM alone for delete requests unless specifically configured
    }

    @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@authorizationPolicy.hasPermission(#workspaceId, 'analytics.export')")
    public ResponseEntity<List<AnalyticsEvent>> exportAnalytics(@PathVariable String workspaceId) {
        // Warning: Loading all events into memory for export.
        // For MVP, this is a placeholder. Real implementation should stream JSONL using Jackson.
        List<AnalyticsEvent> events = repository.findAll().stream()
                .filter(e -> workspaceId.equals(e.getWorkspaceId()))
                .toList();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"analytics_export_" + workspaceId + ".json\"")
                .body(events);
    }
}
