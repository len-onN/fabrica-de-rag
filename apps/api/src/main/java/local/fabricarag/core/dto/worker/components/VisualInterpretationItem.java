package local.fabricarag.core.dto.worker.components;

import java.util.List;

public class VisualInterpretationItem {
    private String elementId;
    private String assetId;
    private String assetStorageUri;
    private String elementType;
    private String caption;
    private List<String> detectedText;
    private SourceLocator sourceLocator;

    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }

    public String getAssetStorageUri() { return assetStorageUri; }
    public void setAssetStorageUri(String assetStorageUri) { this.assetStorageUri = assetStorageUri; }

    public String getElementType() { return elementType; }
    public void setElementType(String elementType) { this.elementType = elementType; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public List<String> getDetectedText() { return detectedText; }
    public void setDetectedText(List<String> detectedText) { this.detectedText = detectedText; }

    public SourceLocator getSourceLocator() { return sourceLocator; }
    public void setSourceLocator(SourceLocator sourceLocator) { this.sourceLocator = sourceLocator; }
}
