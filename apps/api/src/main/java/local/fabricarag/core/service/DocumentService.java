package local.fabricarag.core.service;

import local.fabricarag.core.domain.Document;
import local.fabricarag.core.domain.IngestRun;
import local.fabricarag.core.domain.KnowledgeCollection;
import local.fabricarag.core.dto.DocumentResponse;
import local.fabricarag.core.exception.ResourceNotFoundException;
import local.fabricarag.core.repository.DocumentRepository;
import local.fabricarag.core.repository.IngestRunRepository;
import local.fabricarag.core.repository.KnowledgeCollectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class DocumentService {

    private static final long MAX_FILE_SIZE = 90L * 1024 * 1024; // 90MB

    private final DocumentRepository documentRepository;
    private final KnowledgeCollectionRepository collectionRepository;
    private final IngestRunRepository ingestRunRepository;
    private final StorageService storageService;

    public DocumentService(DocumentRepository documentRepository,
                           KnowledgeCollectionRepository collectionRepository,
                           IngestRunRepository ingestRunRepository,
                           StorageService storageService) {
        this.documentRepository = documentRepository;
        this.collectionRepository = collectionRepository;
        this.ingestRunRepository = ingestRunRepository;
        this.storageService = storageService;
    }

    @Transactional
    public DocumentResponse uploadDocument(UUID workspaceId, String collectionPublicId, MultipartFile file, UUID userId) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Tamanho do arquivo excede o limite de 90MB.");
        }
        
        if (file.getContentType() == null || !file.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("Apenas arquivos PDF são aceitos.");
        }

        KnowledgeCollection collection = collectionRepository.findByWorkspaceIdAndPublicId(workspaceId, collectionPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        UUID documentId = UUID.randomUUID();
        String publicId = "doc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
        
        String sourceHash;
        try {
            byte[] fileBytes = file.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);
            sourceHash = HexFormat.of().formatHex(hashBytes);
            
            // Gravar em disco
            storageService.saveFile(workspaceId, documentId, "original.pdf", file.getInputStream());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao processar o arquivo", e);
        }

        String storageUri = storageService.generateStorageUri(workspaceId, documentId, "original.pdf");

        Document document = new Document(
                documentId,
                publicId,
                workspaceId,
                collection.getId(),
                "file_upload",
                storageUri, // source_uri
                sourceHash,
                originalFilename,
                "application/pdf",
                file.getSize(),
                "uploaded", // initial status
                0, // page count calculated later
                storageUri,
                userId
        );

        documentRepository.save(document);

        // Criar run inicial
        UUID runId = UUID.randomUUID();
        String runPublicId = "run_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String idempotencyKey = "hash_" + sourceHash; // Simples para MVP
        
        IngestRun run = new IngestRun(
                runId,
                runPublicId,
                workspaceId,
                documentId,
                idempotencyKey,
                userId
        );
        
        ingestRunRepository.save(run);

        return mapToResponse(document);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> listDocuments(UUID workspaceId, String collectionPublicId, Pageable pageable) {
        KnowledgeCollection collection = collectionRepository.findByWorkspaceIdAndPublicId(workspaceId, collectionPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        return documentRepository.findByWorkspaceIdAndKnowledgeCollectionId(workspaceId, collection.getId(), pageable)
                .map(this::mapToResponse);
    }

    private DocumentResponse mapToResponse(Document document) {
        return new DocumentResponse(
                document.getPublicId(),
                document.getOriginalFilename(),
                document.getMimeType(),
                document.getFileSizeBytes(),
                document.getStatus(),
                document.getPageCount(),
                document.getCreatedAt()
        );
    }
}
