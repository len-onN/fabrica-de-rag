package local.fabricarag.core.service;

import local.fabricarag.core.domain.AuthSession;
import local.fabricarag.core.domain.User;
import local.fabricarag.core.domain.WorkspaceMembership;
import local.fabricarag.core.dto.AuthMeResponse;
import local.fabricarag.core.dto.LoginRequest;
import local.fabricarag.core.repository.AuthSessionRepository;
import local.fabricarag.core.repository.UserRepository;
import local.fabricarag.core.repository.WorkspaceMembershipRepository;
import local.fabricarag.core.security.CurrentActor;
import local.fabricarag.core.security.OpaqueSessionFilter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository,
                       AuthSessionRepository authSessionRepository,
                       WorkspaceMembershipRepository membershipRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authSessionRepository = authSessionRepository;
        this.membershipRepository = membershipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("invalid_credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("invalid_credentials");
        }

        // Create new session
        String sessionSecret = generateSessionSecret();
        String sessionHash = OpaqueSessionFilter.hashSessionSecret(sessionSecret);

        AuthSession session = new AuthSession();
        session.setSessionHash(sessionHash);
        session.setUser(user);
        session.setExpiresAt(OffsetDateTime.now().plusDays(7));
        session.setLastSeenAt(OffsetDateTime.now());
        authSessionRepository.save(session);

        // We could delete old sessions here, but we let them expire normally for MVP
        
        return sessionSecret;
    }

    @Transactional
    public void logout(String sessionSecret) {
        if (sessionSecret == null) return;
        String hash = OpaqueSessionFilter.hashSessionSecret(sessionSecret);
        authSessionRepository.findBySessionHash(hash)
                .ifPresent(authSessionRepository::delete);
    }

    @Transactional(readOnly = true)
    public AuthMeResponse me(CurrentActor actor) {
        User user = userRepository.findById(actor.getUserId())
                .orElseThrow(() -> new IllegalStateException("user_not_found"));
        
        List<WorkspaceMembership> memberships = membershipRepository.findByUserId(user.getId());
        
        List<AuthMeResponse.WorkspaceDto> workspaces = memberships.stream()
            .filter(m -> "active".equals(m.getStatus()))
            .map(m -> new AuthMeResponse.WorkspaceDto(
                m.getWorkspace().getPublicId(),
                m.getWorkspace().getName(),
                m.getRole(),
                getCapabilitiesForRole(m.getRole())
            ))
            .collect(Collectors.toList());
            
        String activeWorkspaceId = workspaces.isEmpty() ? null : workspaces.get(0).id();

        return new AuthMeResponse(
            "rest.auth.me.response.v1",
            new AuthMeResponse.UserDto(user.getPublicId(), user.getDisplayName(), user.getEmail()),
            workspaces,
            activeWorkspaceId
        );
    }

    private String generateSessionSecret() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private List<String> getCapabilitiesForRole(String role) {
        // Simplified mapping for UI capabilities
        return switch (role) {
            case "owner" -> List.of("workspace.read", "workspace.update", "workspace.delete", "collection.create", "document.upload", "rag.search", "rag.context", "rag.ask", "analytics.view", "api_key.create", "mcp.manage");
            case "admin" -> List.of("workspace.read", "workspace.update", "collection.create", "document.upload", "rag.search", "rag.context", "rag.ask", "analytics.view");
            case "curator" -> List.of("workspace.read", "collection.create", "document.upload", "rag.search", "rag.context", "rag.ask", "analytics.view");
            case "member", "viewer" -> List.of("workspace.read", "rag.search", "rag.context", "rag.ask");
            default -> List.of();
        };
    }
}
