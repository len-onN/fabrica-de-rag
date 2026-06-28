package local.fabricarag.core.dto.worker;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import local.fabricarag.core.dto.worker.components.VisualBudgetResult;
import local.fabricarag.core.dto.worker.components.VisualInterpretation;

public class PdfInterpretVisualResponse {

    private String contractVersion = "worker.pdf.interpret_visual.response.v1";

    @NotBlank(message = "RequestId cannot be blank")
    private String requestId;

    private String workspaceId;

    @NotBlank(message = "DocumentId cannot be blank")
    private String documentId;

    private String runId;

    private List<VisualInterpretation> interpretations;
    private VisualBudgetResult budget;
    private List<String> warnings;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public List<VisualInterpretation> getInterpretations() { return interpretations; }
    public void setInterpretations(List<VisualInterpretation> interpretations) { this.interpretations = interpretations; }

    public VisualBudgetResult getBudget() { return budget; }
    public void setBudget(VisualBudgetResult budget) { this.budget = budget; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}
