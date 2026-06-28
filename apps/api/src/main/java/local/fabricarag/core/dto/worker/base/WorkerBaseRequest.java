package local.fabricarag.core.dto.worker.base;

import jakarta.validation.constraints.NotBlank;

public class WorkerBaseRequest {

    @NotBlank(message = "RequestId cannot be blank")
    private String requestId;

    @NotBlank(message = "WorkspaceId cannot be blank")
    private String workspaceId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}
