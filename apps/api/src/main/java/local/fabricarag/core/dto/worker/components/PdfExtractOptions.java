package local.fabricarag.core.dto.worker.components;

public class PdfExtractOptions {
    private Boolean detectTables = true;
    private Boolean detectImages = true;
    private Boolean detectCaptions = true;
    private Boolean includeCellBBoxes = true;
    private Boolean renderAssetReferences = true;
    private Integer maxElements = 1000;
    private Integer maxTableCells = 2000;

    public Boolean getDetectTables() { return detectTables; }
    public void setDetectTables(Boolean detectTables) { this.detectTables = detectTables; }

    public Boolean getDetectImages() { return detectImages; }
    public void setDetectImages(Boolean detectImages) { this.detectImages = detectImages; }

    public Boolean getDetectCaptions() { return detectCaptions; }
    public void setDetectCaptions(Boolean detectCaptions) { this.detectCaptions = detectCaptions; }

    public Boolean getIncludeCellBBoxes() { return includeCellBBoxes; }
    public void setIncludeCellBBoxes(Boolean includeCellBBoxes) { this.includeCellBBoxes = includeCellBBoxes; }

    public Boolean getRenderAssetReferences() { return renderAssetReferences; }
    public void setRenderAssetReferences(Boolean renderAssetReferences) { this.renderAssetReferences = renderAssetReferences; }

    public Integer getMaxElements() { return maxElements; }
    public void setMaxElements(Integer maxElements) { this.maxElements = maxElements; }

    public Integer getMaxTableCells() { return maxTableCells; }
    public void setMaxTableCells(Integer maxTableCells) { this.maxTableCells = maxTableCells; }
}
