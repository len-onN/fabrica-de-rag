package local.fabricarag.core.dto;

import java.util.List;

public record AuthMeResponse(
    String schemaVersion,
    UserDto user,
    List<WorkspaceDto> workspaces,
    String activeWorkspaceId
) {
    public record UserDto(String id, String displayName, String email) {}
    public record WorkspaceDto(String id, String name, String role, List<String> capabilities) {}
}
