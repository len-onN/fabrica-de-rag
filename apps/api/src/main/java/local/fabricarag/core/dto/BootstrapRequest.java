package local.fabricarag.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BootstrapRequest(
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$", message = "A senha deve conter letras maiúsculas, minúsculas e números")
    String password,

    @NotBlank(message = "O nome de exibição é obrigatório")
    String displayName,

    @NotBlank(message = "O nome do workspace é obrigatório")
    String workspaceName,

    @NotBlank(message = "O propósito do workspace é obrigatório")
    String workspacePurpose,

    Analytics analytics
) {
    public record Analytics(
        boolean enabled,
        int retentionDays
    ) {}
}
