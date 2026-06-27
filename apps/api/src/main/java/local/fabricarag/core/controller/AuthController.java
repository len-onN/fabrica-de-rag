package local.fabricarag.core.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import local.fabricarag.core.dto.AuthMeResponse;
import local.fabricarag.core.dto.LoginRequest;
import local.fabricarag.core.security.CookieHelper;
import local.fabricarag.core.security.CurrentActor;
import local.fabricarag.core.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String sessionSecret = authService.login(request);
        CookieHelper.setSessionCookie(response, sessionSecret);
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String sessionSecret = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("__Host-rag_session".equals(cookie.getName()) || "rag_session".equals(cookie.getName())) {
                    sessionSecret = cookie.getValue();
                }
            }
        }
        
        authService.logout(sessionSecret);
        CookieHelper.clearSessionCookie(response);
    }

    @GetMapping("/me")
    public AuthMeResponse me(@AuthenticationPrincipal CurrentActor actor) {
        return authService.me(actor);
    }
    
    @GetMapping("/auth/csrf")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void csrf() {
        // Just an endpoint to allow SPA to fetch the CSRF token cookie via interceptor.
        // Spring Security sets the XSRF-TOKEN cookie automatically on any request if configured,
        // but SPA sometimes needs an explicit endpoint to guarantee it.
    }
}
