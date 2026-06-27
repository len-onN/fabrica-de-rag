package local.fabricarag.core.dto;

public record BootstrapRequest(
    String email,
    String password,
    String displayName,
    String workspaceName,
    String workspacePurpose,
    Analytics analytics
) {
    public record Analytics(
        boolean enabled,
        int retentionDays
    ) {}
}
