package local.fabricarag.core.client;

import local.fabricarag.core.dto.worker.PdfInspectRequest;
import local.fabricarag.core.dto.worker.PdfRenderRequest;
import local.fabricarag.core.dto.worker.PdfExtractTextRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WorkerClient {
    private static final Logger logger = LoggerFactory.getLogger(WorkerClient.class);
    
    private final RestClient restClient;

    public WorkerClient(
            RestClient.Builder restClientBuilder,
            @Value("${fabricarag.worker.url:http://localhost:8000}") String workerUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(workerUrl)
                .build();
    }

    public void inspectPdf(PdfInspectRequest request) {
        logger.info("Sending inspect PDF request for document {}", request.documentId());
        
        // This makes the POST request and we expect a 202 Accepted.
        // The worker will process in background and call the callbackUrl.
        restClient.post()
                .uri("/api/v1/pdf/inspect")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void requestRenderPage(PdfRenderRequest request) {
        restClient.post()
                .uri("/api/v1/pdf/render-page")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void requestExtractText(PdfExtractTextRequest request) {
        restClient.post()
                .uri("/api/v1/pdf/extract-text")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
