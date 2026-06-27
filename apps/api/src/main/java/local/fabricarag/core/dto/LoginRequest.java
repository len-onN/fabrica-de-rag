package local.fabricarag.core.dto;

public record LoginRequest(
    String email,
    String password
) {}
