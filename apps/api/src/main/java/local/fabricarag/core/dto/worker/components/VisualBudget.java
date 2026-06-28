package local.fabricarag.core.dto.worker.components;

public class VisualBudget {
    private Integer maxItems = 20;
    private Integer maxImagePixelsLongestSide = 2048;
    private Integer timeoutPerItemMs = 30000;
    private Integer maxOutputTokensPerItem = 800;

    public Integer getMaxItems() { return maxItems; }
    public void setMaxItems(Integer maxItems) { this.maxItems = maxItems; }

    public Integer getMaxImagePixelsLongestSide() { return maxImagePixelsLongestSide; }
    public void setMaxImagePixelsLongestSide(Integer maxImagePixelsLongestSide) { this.maxImagePixelsLongestSide = maxImagePixelsLongestSide; }

    public Integer getTimeoutPerItemMs() { return timeoutPerItemMs; }
    public void setTimeoutPerItemMs(Integer timeoutPerItemMs) { this.timeoutPerItemMs = timeoutPerItemMs; }

    public Integer getMaxOutputTokensPerItem() { return maxOutputTokensPerItem; }
    public void setMaxOutputTokensPerItem(Integer maxOutputTokensPerItem) { this.maxOutputTokensPerItem = maxOutputTokensPerItem; }
}
