package local.fabricarag.core.dto.worker.components;

import java.util.Map;

public class VisualInterpretation {
    private String contractVersion = "pdf.visual_interpretation.v1";
    private String interpretationId;
    private String elementId;
    private String assetId;
    private String status;
    private String interpretationText;
    private Map<String, Object> structuredText;
    private String provider;
    private String model;
    private String promptVersion;
    private String inputHash;
    private Double confidence;
    private VisualUsage usage;
    private WorkerError error;
    private SourceLocator sourceLocator;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getInterpretationId() { return interpretationId; }
    public void setInterpretationId(String interpretationId) { this.interpretationId = interpretationId; }

    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInterpretationText() { return interpretationText; }
    public void setInterpretationText(String interpretationText) { this.interpretationText = interpretationText; }

    public Map<String, Object> getStructuredText() { return structuredText; }
    public void setStructuredText(Map<String, Object> structuredText) { this.structuredText = structuredText; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPromptVersion() { return promptVersion; }
    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public String getInputHash() { return inputHash; }
    public void setInputHash(String inputHash) { this.inputHash = inputHash; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public VisualUsage getUsage() { return usage; }
    public void setUsage(VisualUsage usage) { this.usage = usage; }

    public WorkerError getError() { return error; }
    public void setError(WorkerError error) { this.error = error; }

    public SourceLocator getSourceLocator() { return sourceLocator; }
    public void setSourceLocator(SourceLocator sourceLocator) { this.sourceLocator = sourceLocator; }
}
