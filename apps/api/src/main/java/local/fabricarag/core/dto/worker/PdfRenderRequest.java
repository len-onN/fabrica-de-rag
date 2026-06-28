package local.fabricarag.core.dto.worker;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import local.fabricarag.core.dto.worker.base.WorkerBaseRequest;

public class PdfRenderRequest extends WorkerBaseRequest {

    @NotBlank(message = "DocumentId cannot be blank")
    private String documentId;

    @NotBlank(message = "StorageUri cannot be blank")
    private String storageUri;

    @NotNull(message = "PageNumber is required")
    @Min(value = 1, message = "PageNumber must be at least 1")
    private Integer pageNumber;

    private Integer dpi = 150;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStorageUri() {
        return storageUri;
    }

    public void setStorageUri(String storageUri) {
        this.storageUri = storageUri;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getDpi() {
        return dpi;
    }

    public void setDpi(Integer dpi) {
        this.dpi = dpi;
    }
}
