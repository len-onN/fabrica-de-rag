package local.fabricarag.core.dto.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import local.fabricarag.core.dto.worker.base.WorkerBaseRequest;
import local.fabricarag.core.dto.worker.components.PageExtractContext;
import local.fabricarag.core.dto.worker.components.PdfExtractOptions;

public class PdfExtractElementsRequest extends WorkerBaseRequest {

    private String contractVersion = "worker.pdf.extract_elements.request.v1";

    @NotBlank(message = "DocumentId cannot be blank")
    private String documentId;

    @NotBlank(message = "RunId cannot be blank")
    private String runId;

    @NotBlank(message = "StorageUri cannot be blank")
    private String storageUri;

    @NotNull(message = "Pages list is required")
    private List<PageExtractContext> pages;

    private PdfExtractOptions options;

    public String getContractVersion() { return contractVersion; }
    public void setContractVersion(String contractVersion) { this.contractVersion = contractVersion; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getStorageUri() { return storageUri; }
    public void setStorageUri(String storageUri) { this.storageUri = storageUri; }

    public List<PageExtractContext> getPages() { return pages; }
    public void setPages(List<PageExtractContext> pages) { this.pages = pages; }

    public PdfExtractOptions getOptions() { return options; }
    public void setOptions(PdfExtractOptions options) { this.options = options; }
}
