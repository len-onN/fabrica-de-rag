package local.fabricarag.core.security;

import local.fabricarag.core.domain.WorkspaceMembership;
import local.fabricarag.core.repository.WorkspaceMembershipRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("authorizationPolicy")
public class AuthorizationPolicy {

    private final WorkspaceMembershipRepository membershipRepository;

    public AuthorizationPolicy(WorkspaceMembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public boolean hasPermission(Authentication authentication, String workspacePublicId, String permission) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication instanceof CurrentActor)) {
            return false;
        }

        CurrentActor actor = (CurrentActor) authentication;
        
        if ("agent".equals(actor.getType())) {
            return workspacePublicId.equals(actor.getWorkspacePublicId()) && actor.getCapabilities().contains(permission);
        }

        List<WorkspaceMembership> memberships = membershipRepository.findByUserId(actor.getUserId());
        
        return memberships.stream()
            .filter(m -> m.getWorkspace().getPublicId().equals(workspacePublicId))
            .filter(m -> "active".equals(m.getStatus()))
            .anyMatch(m -> checkPermission(m.getRole(), permission));
    }

    private boolean checkPermission(String role, String permission) {
        // Simplified matrix based on docs/seguranca-permissoes-mvp.md
        // In a real app, this would be a map or a dedicated service, but for MVP:
        return switch (role) {
            case "owner" -> true; // Owner has all permissions (except maybe some agent specifics, but true for MVP)
            case "admin" -> switch (permission) {
                case "workspace.delete", "api_key.create", "api_key.revoke", "vector_connection.create", "vector_connection.manage", "mcp.use" -> false;
                default -> true;
            };
            case "curator" -> switch (permission) {
                case "workspace.update", "workspace.delete", "members.read", "members.manage", 
                     "settings.update", "collection.delete", "analytics.export", "analytics.delete", 
                     "api_key.create", "api_key.revoke", "vector_connection.create", "vector_connection.manage",
                     "mcp.use", "mcp.manage" -> false;
                default -> true;
            };
            case "member", "viewer" -> switch (permission) {
                case "workspace.read", "settings.read", "collection.read", "document.read", "document.file.read",
                     "ingest_run.read", "page_map.read", "chunk.read", "rag.search", "rag.context", "rag.ask",
                     "feedback.create" -> true;
                default -> false;
            };
            case "agent" -> switch (permission) {
                // Agent requires explicit capabilities, simplified here to false by default without capability check
                default -> false; 
            };
            default -> false;
        };
    }
}
