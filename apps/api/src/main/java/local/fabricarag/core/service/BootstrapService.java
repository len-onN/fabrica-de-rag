package local.fabricarag.core.service;

import local.fabricarag.core.domain.AuthSession;
import local.fabricarag.core.domain.User;
import local.fabricarag.core.domain.Workspace;
import local.fabricarag.core.domain.WorkspaceMembership;
import local.fabricarag.core.dto.BootstrapRequest;
import local.fabricarag.core.dto.BootstrapResponse;
import local.fabricarag.core.repository.AuthSessionRepository;
import local.fabricarag.core.repository.UserRepository;
import local.fabricarag.core.repository.WorkspaceMembershipRepository;
import local.fabricarag.core.repository.WorkspaceRepository;
import local.fabricarag.core.security.OpaqueSessionFilter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class BootstrapService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final AuthSessionRepository authSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public BootstrapService(
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMembershipRepository membershipRepository,
            AuthSessionRepository authSessionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
        this.authSessionRepository = authSessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record BootstrapResult(BootstrapResponse response, String sessionSecret) {}

    @Transactional
    public BootstrapResult bootstrap(BootstrapRequest request) {
        if (userRepository.count() > 0) {
            throw new IllegalStateException("bootstrap_already_completed");
        }

        // Create User
        User user = new User();
        user.setPublicId("usr_" + generateIdSuffix());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user = userRepository.save(user);

        // Create Workspace
        Workspace workspace = new Workspace();
        workspace.setPublicId("wsp_" + generateIdSuffix());
        workspace.setName(request.workspaceName());
        workspace.setPurpose(request.workspacePurpose());
        workspace = workspaceRepository.save(workspace);

        // Create Membership
        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setPublicId("mem_" + generateIdSuffix());
        membership.setUser(user);
        membership.setWorkspace(workspace);
        membership.setRole("owner");
        membership.setStatus("active");
        membership = membershipRepository.save(membership);

        // Create Session
        String sessionSecret = generateSessionSecret();
        String sessionHash = OpaqueSessionFilter.hashSessionSecret(sessionSecret);
        
        AuthSession session = new AuthSession();
        session.setSessionHash(sessionHash);
        session.setUser(user);
        session.setExpiresAt(OffsetDateTime.now().plusDays(7));
        session.setLastSeenAt(OffsetDateTime.now());
        authSessionRepository.save(session);

        // Map to Response
        BootstrapResponse response = new BootstrapResponse(
            "rest.bootstrap.response.v1",
            new BootstrapResponse.UserDto(user.getPublicId(), user.getDisplayName(), user.getEmail()),
            new BootstrapResponse.WorkspaceDto(workspace.getPublicId(), workspace.getName(), workspace.getPurpose()),
            new BootstrapResponse.MembershipDto(membership.getPublicId(), membership.getRole(), membership.getStatus()),
            new BootstrapResponse.SessionDto(true, "X-XSRF-TOKEN")
        );
        return new BootstrapResult(response, sessionSecret);
    }

    public String generateSessionSecret() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String generateIdSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
