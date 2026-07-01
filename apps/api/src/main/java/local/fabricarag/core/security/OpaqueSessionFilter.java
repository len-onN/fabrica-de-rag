package local.fabricarag.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import local.fabricarag.core.domain.AuthSession;
import local.fabricarag.core.repository.AuthSessionRepository;
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
public class OpaqueSessionFilter extends OncePerRequestFilter {

    private final AuthSessionRepository authSessionRepository;

    public OpaqueSessionFilter(AuthSessionRepository authSessionRepository) {
        this.authSessionRepository = authSessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String sessionSecret = getSessionCookie(request);

        if (sessionSecret != null && !sessionSecret.isBlank()) {
            String hash = hashSessionSecret(sessionSecret);
            Optional<AuthSession> authSessionOpt = authSessionRepository.findBySessionHash(hash);

            if (authSessionOpt.isPresent()) {
                AuthSession authSession = authSessionOpt.get();
                
                if (authSession.getExpiresAt().isAfter(OffsetDateTime.now())) {
                    // Valid session, setup security context
                    CurrentActor actor = CurrentActor.forUser(
                            authSession.getUser().getId(),
                            authSession.getUser().getPublicId(),
                            authSession.getUser().getEmail()
                    );
                    SecurityContextHolder.getContext().setAuthentication(actor);
                    
                    // Throttle last seen at updates to avoid DB hits on every request
                    if (authSession.getLastSeenAt().isBefore(OffsetDateTime.now().minusMinutes(5))) {
                        authSession.setLastSeenAt(OffsetDateTime.now());
                        authSessionRepository.save(authSession);
                    }
                } else {
                    // Session expired, could delete it here or leave it for cleanup job
                    authSessionRepository.delete(authSession);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getSessionCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("__Host-rag_session".equals(cookie.getName()) || "rag_session".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String hashSessionSecret(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
