package local.fabricarag.core.dto.worker;

import java.util.UUID;

public record PdfInspectResponse(
        String contractVersion,
        String requestId,
        UUID documentId,
        int pageCount,
        boolean encrypted,
        PdfInspectMetadata metadata
) {
    public record PdfInspectMetadata(
            String title,
            String author
    ) {}
}
