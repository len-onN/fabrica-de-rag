package local.fabricarag.core.dto.worker.components;

public class VisualBudgetResult {
    private Integer itemsRequested;
    private Integer itemsProcessed;
    private Integer itemsSkipped;
    private Boolean limitHit;

    public Integer getItemsRequested() { return itemsRequested; }
    public void setItemsRequested(Integer itemsRequested) { this.itemsRequested = itemsRequested; }

    public Integer getItemsProcessed() { return itemsProcessed; }
    public void setItemsProcessed(Integer itemsProcessed) { this.itemsProcessed = itemsProcessed; }

    public Integer getItemsSkipped() { return itemsSkipped; }
    public void setItemsSkipped(Integer itemsSkipped) { this.itemsSkipped = itemsSkipped; }

    public Boolean getLimitHit() { return limitHit; }
    public void setLimitHit(Boolean limitHit) { this.limitHit = limitHit; }
}
