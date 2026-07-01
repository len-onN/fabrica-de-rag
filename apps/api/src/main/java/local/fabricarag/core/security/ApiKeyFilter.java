package local.fabricarag.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import local.fabricarag.core.domain.ApiKey;
import local.fabricarag.core.repository.ApiKeyRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer rag_key_")) {
            String token = authHeader.substring(7); // remove "Bearer "
            String keyPayload = token.substring(8); // remove "rag_key_"
            
            int dotIndex = keyPayload.indexOf('.');
            if (dotIndex > 0 && dotIndex < keyPayload.length() - 1) {
                String publicId = keyPayload.substring(0, dotIndex);
                String secret = keyPayload.substring(dotIndex + 1);

                String hash = hashSecret(secret);
                Optional<ApiKey> keyOpt = apiKeyRepository.findByPublicIdAndSecretHash(publicId, hash);

                if (keyOpt.isPresent()) {
                    ApiKey key = keyOpt.get();
                    OffsetDateTime now = OffsetDateTime.now();

                    if (key.getRevokedAt() == null && (key.getExpiresAt() == null || key.getExpiresAt().isAfter(now))) {
                        CurrentActor actor = CurrentActor.forAgent(
                                key.getId(),
                                key.getPublicId(),
                                key.getWorkspace().getPublicId(),
                                key.getCapabilities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(actor);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public static String hashSecret(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
