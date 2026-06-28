package local.fabricarag.core.dto.worker;

import java.util.UUID;

public record PdfInspectRequest(
        String contractVersion,
        String requestId,
        UUID workspaceId,
        UUID documentId,
        String storageUri,
        String callbackUrl
) {
    public static PdfInspectRequest create(String requestId, UUID workspaceId, UUID documentId, String storageUri, String callbackUrl) {
        return new PdfInspectRequest(
                "worker.pdf.inspect.request.v1",
                requestId,
                workspaceId,
                documentId,
                storageUri,
                callbackUrl
        );
    }
}
