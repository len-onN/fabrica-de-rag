package local.fabricarag.core.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieHelper {
    public static void setSessionCookie(HttpServletResponse response, String secret) {
        Cookie cookie = new Cookie("__Host-rag_session", secret);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        
        Cookie devCookie = new Cookie("rag_session", secret);
        devCookie.setHttpOnly(true);
        devCookie.setSecure(false);
        devCookie.setPath("/");
        devCookie.setAttribute("SameSite", "Lax");
        devCookie.setMaxAge(7 * 24 * 60 * 60);
        
        response.addCookie(cookie);
        response.addCookie(devCookie);
    }
    
    public static void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("__Host-rag_session", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        
        Cookie devCookie = new Cookie("rag_session", "");
        devCookie.setHttpOnly(true);
        devCookie.setSecure(false);
        devCookie.setPath("/");
        devCookie.setMaxAge(0);
        
        response.addCookie(cookie);
        response.addCookie(devCookie);
    }
}
