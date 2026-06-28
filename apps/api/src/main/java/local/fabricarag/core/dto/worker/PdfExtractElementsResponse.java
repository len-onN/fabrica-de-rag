package local.fabricarag.core.dto.worker;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import local.fabricarag.core.dto.worker.components.Asset;
import local.fabricarag.core.dto.worker.components.DocumentElement;
import local.fabricarag.core.dto.worker.components.PageSummary;

public class PdfExtractElementsResponse {

    private String contractVersion = "worker.pdf.extract_elements.response.v1";

    @NotBlank(message = "RequestId cannot be blank")
    private String requestId;

    private String workspaceId;

    @NotBlank(message = "DocumentId cannot be blank")
    private String documentId;

    private String runId;

    private List<DocumentElement> elements;
    private List<Asset> assets;
    private List<PageSummary> pageSummaries;
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

    public List<DocumentElement> getElements() { return elements; }
    public void setElements(List<DocumentElement> elements) { this.elements = elements; }

    public List<Asset> getAssets() { return assets; }
    public void setAssets(List<Asset> assets) { this.assets = assets; }

    public List<PageSummary> getPageSummaries() { return pageSummaries; }
    public void setPageSummaries(List<PageSummary> pageSummaries) { this.pageSummaries = pageSummaries; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}
