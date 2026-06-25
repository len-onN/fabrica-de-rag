package local.fabricarag.core.dummy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DummyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProblemDetailsOnValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/dummy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}")) // Empty name triggers validation error
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://fabricarag.local/problems/validation-error"))
                .andExpect(jsonPath("$.title").value("Validation error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Request contains invalid fields."))
                .andExpect(jsonPath("$.code").value("validation_error"))
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("$.fieldErrors[0].code").value("notblank"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Name is required."));
    }
}
