package local.fabricarag.core.dto.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import local.fabricarag.core.dto.worker.base.WorkerBaseRequest;
import local.fabricarag.core.dto.worker.components.PrivacyPolicy;
import local.fabricarag.core.dto.worker.components.VisualBudget;
import local.fabricarag.core.dto.worker.components.VisualInterpretationItem;

public class PdfInterpretVisualRequest extends WorkerBaseRequest {

    private String contractVersion = "worker.pdf.interpret_visual.request.v1";

    @NotBlank(message = "DocumentId cannot be blank")
    private String documentId;

    @NotBlank(message = "RunId cannot be blank")
    private String runId;

    private String language;
    private String provider;
    private String model;
    private String promptVersion;

    private PrivacyPolicy privacyPolicy;
    private VisualBudget budget;

    @NotNull(message = "Items list is required")
    private List<VisualInterpretationItem> items;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPromptVersion() { return promptVersion; }
    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public PrivacyPolicy getPrivacyPolicy() { return privacyPolicy; }
    public void setPrivacyPolicy(PrivacyPolicy privacyPolicy) { this.privacyPolicy = privacyPolicy; }

    public VisualBudget getBudget() { return budget; }
    public void setBudget(VisualBudget budget) { this.budget = budget; }

    public List<VisualInterpretationItem> getItems() { return items; }
    public void setItems(List<VisualInterpretationItem> items) { this.items = items; }
}
