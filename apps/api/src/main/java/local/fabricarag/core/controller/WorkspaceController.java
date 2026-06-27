package local.fabricarag.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.domain.Workspace;
import local.fabricarag.core.dto.DashboardSummaryResponse;
import local.fabricarag.core.dto.WorkspaceSettingsResponse;
import local.fabricarag.core.dto.WorkspaceSettingsUpdateRequest;
import local.fabricarag.core.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final ObjectMapper objectMapper;

    public WorkspaceController(WorkspaceRepository workspaceRepository, ObjectMapper objectMapper) {
        this.workspaceRepository = workspaceRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{workspaceId}/dashboard")
    @org.springframework.security.access.prepost.PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.read')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @PathVariable String workspaceId) {

        DashboardSummaryResponse response = new DashboardSummaryResponse(
                "1.0",
                workspaceId,
                0, 
                0, 
                0  
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workspaceId}/settings")
    @org.springframework.security.access.prepost.PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.read')")
    public ResponseEntity<WorkspaceSettingsResponse> getWorkspaceSettings(
            @PathVariable String workspaceId) {
        
        Workspace workspace = workspaceRepository.findByPublicId(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
                
        JsonNode settingsNode;
        try {
            settingsNode = objectMapper.readTree(workspace.getSettings());
        } catch (JsonProcessingException e) {
            settingsNode = objectMapper.createObjectNode();
        }

        WorkspaceSettingsResponse response = new WorkspaceSettingsResponse(
                workspace.getPublicId(),
                workspace.getName(),
                workspace.getPurpose(),
                workspace.getSlug(),
                settingsNode,
                workspace.getCreatedAt(),
                workspace.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{workspaceId}/settings")
    @org.springframework.security.access.prepost.PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.update')")
    public ResponseEntity<WorkspaceSettingsResponse> updateWorkspaceSettings(
            @PathVariable String workspaceId,
            @RequestBody WorkspaceSettingsUpdateRequest request) {
            
        Workspace workspace = workspaceRepository.findByPublicId(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
                
        if (request.name() != null) {
            workspace.setName(request.name());
        }
        if (request.purpose() != null) {
            workspace.setPurpose(request.purpose());
        }
        if (request.slug() != null) {
            workspace.setSlug(request.slug());
        }
        if (request.settings() != null) {
            try {
                workspace.setSettings(objectMapper.writeValueAsString(request.settings()));
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid settings format");
            }
        }
        
        workspaceRepository.save(workspace);
        
        JsonNode settingsNode;
        try {
            settingsNode = objectMapper.readTree(workspace.getSettings());
        } catch (JsonProcessingException e) {
            settingsNode = objectMapper.createObjectNode();
        }

        WorkspaceSettingsResponse response = new WorkspaceSettingsResponse(
                workspace.getPublicId(),
                workspace.getName(),
                workspace.getPurpose(),
                workspace.getSlug(),
                settingsNode,
                workspace.getCreatedAt(),
                workspace.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }
}
