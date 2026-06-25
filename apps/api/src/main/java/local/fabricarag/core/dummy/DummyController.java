package local.fabricarag.core.dummy;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dummy")
public class DummyController {

    public record DummyRequest(@NotBlank(message = "Name is required.") String name) {}

    @PostMapping
    public String createDummy(@Valid @RequestBody DummyRequest request) {
        return "Success: " + request.name();
    }
}
