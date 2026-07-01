package local.fabricarag.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CurrentActor implements Authentication {
    private final UUID userId;
    private final String publicId;
    private final String email;
    private final String type;
    private final String workspacePublicId;
    private final List<String> capabilities;
    private boolean authenticated = true;

    private CurrentActor(UUID userId, String publicId, String email, String type, String workspacePublicId, List<String> capabilities) {
        this.userId = userId;
        this.publicId = publicId;
        this.email = email;
        this.type = type;
        this.workspacePublicId = workspacePublicId;
        this.capabilities = capabilities == null ? List.of() : capabilities;
    }

    public static CurrentActor forUser(UUID userId, String publicId, String email) {
        return new CurrentActor(userId, publicId, email, "user", null, null);
    }

    public static CurrentActor forAgent(UUID keyId, String publicId, String workspacePublicId, List<String> capabilities) {
        return new CurrentActor(keyId, publicId, "agent@" + workspacePublicId, "agent", workspacePublicId, capabilities);
    }

    public UUID getUserId() { return userId; }
    public String getPublicId() { return publicId; }
    public String getEmail() { return email; }
    public String getType() { return type; }
    public String getWorkspacePublicId() { return workspacePublicId; }
    public List<String> getCapabilities() { return capabilities; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Authorization is handled by AuthorizationPolicy based on Workspace scope
    }

    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getDetails() { return null; }

    @Override
    public Object getPrincipal() { return email; }

    @Override
    public boolean isAuthenticated() { return authenticated; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() { return email; }
}
