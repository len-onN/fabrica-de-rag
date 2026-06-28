package local.fabricarag.core.dto.worker.components;

public class VisualUsage {
    private Integer inputTokensEstimate;
    private Integer outputTokensEstimate;
    private Boolean cacheHit;

    public Integer getInputTokensEstimate() { return inputTokensEstimate; }
    public void setInputTokensEstimate(Integer inputTokensEstimate) { this.inputTokensEstimate = inputTokensEstimate; }

    public Integer getOutputTokensEstimate() { return outputTokensEstimate; }
    public void setOutputTokensEstimate(Integer outputTokensEstimate) { this.outputTokensEstimate = outputTokensEstimate; }

    public Boolean getCacheHit() { return cacheHit; }
    public void setCacheHit(Boolean cacheHit) { this.cacheHit = cacheHit; }
}
