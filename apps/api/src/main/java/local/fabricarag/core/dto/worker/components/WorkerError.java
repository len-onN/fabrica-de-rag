package local.fabricarag.core.dto.worker.components;

public class WorkerError {
    private String code;
    private String message;
    private Boolean retryable;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getRetryable() { return retryable; }
    public void setRetryable(Boolean retryable) { this.retryable = retryable; }
}
