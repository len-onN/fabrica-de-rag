package local.fabricarag.core.dto;

public record BootstrapResponse(
    String schemaVersion,
    UserDto user,
    WorkspaceDto workspace,
    MembershipDto membership,
    SessionDto session
) {
    public record UserDto(String id, String displayName, String email) {}
    public record WorkspaceDto(String id, String name, String purpose) {}
    public record MembershipDto(String id, String role, String status) {}
    public record SessionDto(boolean authenticated, String csrfHeaderName) {}
}
