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
    private boolean authenticated = true;

    public CurrentActor(UUID userId, String publicId, String email) {
        this.userId = userId;
        this.publicId = publicId;
        this.email = email;
    }

    public UUID getUserId() { return userId; }
    public String getPublicId() { return publicId; }
    public String getEmail() { return email; }

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
