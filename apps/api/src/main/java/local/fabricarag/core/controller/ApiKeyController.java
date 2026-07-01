package local.fabricarag.core.controller;

import local.fabricarag.core.domain.ApiKey;
import local.fabricarag.core.domain.User;
import local.fabricarag.core.domain.Workspace;
import local.fabricarag.core.domain.analytics.AnalyticsEvent;
import local.fabricarag.core.domain.analytics.AnalyticsEventService;
import local.fabricarag.core.domain.analytics.RetentionClass;
import local.fabricarag.core.domain.analytics.EventOrigin;
import local.fabricarag.core.domain.analytics.ActorType;
import local.fabricarag.core.dto.ApiKeyCreateRequest;
import local.fabricarag.core.dto.ApiKeyCreateResponse;
import local.fabricarag.core.dto.ApiKeyResponse;
import local.fabricarag.core.repository.ApiKeyRepository;
import local.fabricarag.core.repository.UserRepository;
import local.fabricarag.core.repository.WorkspaceRepository;
import local.fabricarag.core.security.ApiKeyFilter;
import local.fabricarag.core.security.AuthorizationPolicy;
import local.fabricarag.core.security.CurrentActor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/workspaces/{workspacePublicId}/api-keys")
public class ApiKeyController {

    private final ApiKeyRepository apiKeyRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final AnalyticsEventService analyticsService;
    private final AuthorizationPolicy authorizationPolicy;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyController(ApiKeyRepository apiKeyRepository, WorkspaceRepository workspaceRepository,
                            UserRepository userRepository, AnalyticsEventService analyticsService,
                            AuthorizationPolicy authorizationPolicy) {
        this.apiKeyRepository = apiKeyRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.analyticsService = analyticsService;
        this.authorizationPolicy = authorizationPolicy;
    }

    @GetMapping
    public List<ApiKeyResponse> list(@PathVariable String workspacePublicId, @AuthenticationPrincipal CurrentActor actor) {
        if (!authorizationPolicy.hasPermission(actor, workspacePublicId, "api_key.create")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to read API keys");
        }

        Workspace workspace = workspaceRepository.findByPublicId(workspacePublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));

        return apiKeyRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspace.getId()).stream()
                .map(key -> new ApiKeyResponse(
                        key.getId(),
                        key.getPublicId(),
                        key.getCapabilities(),
                        key.getExpiresAt(),
                        key.getRevokedAt(),
                        key.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ApiKeyCreateResponse create(@PathVariable String workspacePublicId,
                                       @RequestBody(required = false) ApiKeyCreateRequest request,
                                       @AuthenticationPrincipal CurrentActor actor) {
        if (!authorizationPolicy.hasPermission(actor, workspacePublicId, "api_key.create")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to create API keys");
        }

        Workspace workspace = workspaceRepository.findByPublicId(workspacePublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));

        User user = userRepository.findById(actor.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        String publicId = generateRandomString(16);
        String secret = generateRandomString(32);
        String secretHash = ApiKeyFilter.hashSecret(secret);

        List<String> capabilities = (request != null && request.capabilities() != null && !request.capabilities().isEmpty()) 
                ? request.capabilities() 
                : List.of("rag.search", "rag.context", "rag.ask", "chunk.read");
                
        OffsetDateTime expiresAt = (request != null && request.expiresAt() != null) ? request.expiresAt() : null;

        ApiKey key = new ApiKey(publicId, secretHash, workspace, capabilities, expiresAt, user);
        apiKeyRepository.save(key);

        AnalyticsEvent event = new AnalyticsEvent();
        event.setWorkspaceId(workspace.getPublicId());
        event.setEventName("security_api_key_created");
        event.setOrigin(EventOrigin.API);
        event.setRetentionClass(RetentionClass.AUDIT_MINIMUM);
        event.setActorType(ActorType.USER);
        event.setActorId(actor.getUserId() != null ? actor.getUserId().toString() : null);
        event.setCorrelationId(UUID.randomUUID().toString());
        event.setOccurredAt(Instant.now());
        event.setProperties(Map.of("keyPublicId", publicId, "capabilities", capabilities));
        analyticsService.publishEvent(event);

        return new ApiKeyCreateResponse(key.getId(), publicId, secret, capabilities, expiresAt);
    }

    @DeleteMapping("/{keyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable String workspacePublicId, @PathVariable UUID keyId, @AuthenticationPrincipal CurrentActor actor) {
        if (!authorizationPolicy.hasPermission(actor, workspacePublicId, "api_key.revoke")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to revoke API keys");
        }

        ApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "API Key not found"));

        if (!key.getWorkspace().getPublicId().equals(workspacePublicId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "API Key not found in workspace");
        }

        key.setRevokedAt(OffsetDateTime.now());
        apiKeyRepository.save(key);

        AnalyticsEvent event = new AnalyticsEvent();
        event.setWorkspaceId(workspacePublicId);
        event.setEventName("security_api_key_revoked");
        event.setOrigin(EventOrigin.API);
        event.setRetentionClass(RetentionClass.AUDIT_MINIMUM);
        event.setActorType(ActorType.USER);
        event.setActorId(actor.getUserId() != null ? actor.getUserId().toString() : null);
        event.setCorrelationId(UUID.randomUUID().toString());
        event.setOccurredAt(Instant.now());
        event.setProperties(Map.of("keyPublicId", key.getPublicId()));
        analyticsService.publishEvent(event);
    }

    private String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
    }
}
