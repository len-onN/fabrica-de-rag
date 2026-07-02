package local.fabricarag.core.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import local.fabricarag.core.dto.BootstrapRequest;
import local.fabricarag.core.dto.BootstrapResponse;
import local.fabricarag.core.security.CookieHelper;
import local.fabricarag.core.service.BootstrapService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BootstrapController {

    private final BootstrapService bootstrapService;

    public BootstrapController(BootstrapService bootstrapService) {
        this.bootstrapService = bootstrapService;
    }

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.CREATED)
    public BootstrapResponse bootstrap(@Valid @RequestBody BootstrapRequest request, HttpServletResponse response) {
        BootstrapService.BootstrapResult result = bootstrapService.bootstrap(request);
        CookieHelper.setSessionCookie(response, result.sessionSecret());
        return result.response();
    }
}
