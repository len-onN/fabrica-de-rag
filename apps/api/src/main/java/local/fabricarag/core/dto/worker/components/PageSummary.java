package local.fabricarag.core.dto.worker.components;

public class PageSummary {
    private Integer filePageNumber;
    private Integer tablesCount;
    private Integer figuresCount;
    private Integer imagesCount;
    private Integer captionsCount;
    private Integer unknownCount;
    private Integer warningsCount;

    public Integer getFilePageNumber() { return filePageNumber; }
    public void setFilePageNumber(Integer filePageNumber) { this.filePageNumber = filePageNumber; }

    public Integer getTablesCount() { return tablesCount; }
    public void setTablesCount(Integer tablesCount) { this.tablesCount = tablesCount; }

    public Integer getFiguresCount() { return figuresCount; }
    public void setFiguresCount(Integer figuresCount) { this.figuresCount = figuresCount; }

    public Integer getImagesCount() { return imagesCount; }
    public void setImagesCount(Integer imagesCount) { this.imagesCount = imagesCount; }

    public Integer getCaptionsCount() { return captionsCount; }
    public void setCaptionsCount(Integer captionsCount) { this.captionsCount = captionsCount; }

    public Integer getUnknownCount() { return unknownCount; }
    public void setUnknownCount(Integer unknownCount) { this.unknownCount = unknownCount; }

    public Integer getWarningsCount() { return warningsCount; }
    public void setWarningsCount(Integer warningsCount) { this.warningsCount = warningsCount; }
}
