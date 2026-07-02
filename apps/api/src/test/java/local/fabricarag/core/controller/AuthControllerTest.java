package local.fabricarag.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import local.fabricarag.core.dto.LoginRequest;
import local.fabricarag.core.security.ApiKeyFilter;
import local.fabricarag.core.security.OpaqueSessionFilter;
import local.fabricarag.core.security.SecurityConfig;
import local.fabricarag.core.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.doAnswer;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private OpaqueSessionFilter opaqueSessionFilter;

    @MockBean
    private ApiKeyFilter apiKeyFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(opaqueSessionFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(apiKeyFilter).doFilter(any(), any(), any());
    }

    @Test
    void login_WithValidCredentials_ReturnsOkAndSetsCookies() throws Exception {
        LoginRequest req = new LoginRequest("test@test.com", "password");
        when(authService.login(any(LoginRequest.class))).thenReturn("fake-session-secret");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("__Host-rag_session"))
                .andExpect(cookie().exists("rag_session"))
                .andExpect(cookie().value("rag_session", "fake-session-secret"));
    }
}
