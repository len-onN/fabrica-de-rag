package local.fabricarag.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import local.fabricarag.core.dto.DashboardSummaryResponse;
import local.fabricarag.core.security.CurrentActor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    @GetMapping("/{workspaceId}/dashboard")
    @org.springframework.security.access.prepost.PreAuthorize("@authorizationPolicy.hasPermission(authentication, #workspaceId, 'workspace.read')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @PathVariable String workspaceId) {

        // Returning fixed 0 as per MVP agreement until collections/documents exist
        DashboardSummaryResponse response = new DashboardSummaryResponse(
                "1.0",
                workspaceId,
                0, // collectionCount
                0, // documentCount
                0  // activeRunsCount
        );

        return ResponseEntity.ok(response);
    }
}
