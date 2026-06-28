package local.fabricarag.core.dto.worker.base;

import jakarta.validation.constraints.NotBlank;

public class WorkerErrorResponse {

    @NotBlank(message = "RequestId cannot be blank")
    private String requestId;

    private Object error;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
