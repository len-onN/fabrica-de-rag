package local.fabricarag.core.dto.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class PdfRenderResponse {

    private String contractVersion = "worker.pdf.render.response.v1";

    @NotBlank(message = "RequestId cannot be blank")
    private String requestId;

    @NotBlank(message = "DocumentId cannot be blank")
    private String documentId;

    @NotNull(message = "PageNumber is required")
    @Min(value = 1, message = "PageNumber must be at least 1")
    private Integer pageNumber;

    @NotBlank(message = "ImageUri cannot be blank")
    private String imageUri;

    public String getContractVersion() {
        return contractVersion;
    }

    public void setContractVersion(String contractVersion) {
        this.contractVersion = contractVersion;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
