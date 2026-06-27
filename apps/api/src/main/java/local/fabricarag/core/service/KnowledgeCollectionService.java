package local.fabricarag.core.service;

import local.fabricarag.core.domain.KnowledgeCollection;
import local.fabricarag.core.dto.CollectionCreateRequest;
import local.fabricarag.core.dto.CollectionResponse;
import local.fabricarag.core.exception.ResourceNotFoundException;
import local.fabricarag.core.repository.KnowledgeCollectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class KnowledgeCollectionService {

    private final KnowledgeCollectionRepository collectionRepository;

    public KnowledgeCollectionService(KnowledgeCollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Transactional
    public CollectionResponse createCollection(UUID workspaceId, UUID userId, CollectionCreateRequest request) {
        UUID id = UUID.randomUUID();
        String publicId = "col_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20); // Simulating base62 for MVP

        KnowledgeCollection collection = new KnowledgeCollection(
                id,
                publicId,
                workspaceId,
                request.name(),
                request.description(),
                request.purpose(),
                "empty", // Initial status
                request.defaultIngestionProfile(),
                request.defaultContextPolicy(),
                "mock-text-embedding", // Default for now
                userId
        );

        collectionRepository.save(collection);
        return mapToResponse(collection);
    }

    @Transactional(readOnly = true)
    public CollectionResponse getCollection(UUID workspaceId, String publicId) {
        KnowledgeCollection collection = collectionRepository.findByWorkspaceIdAndPublicId(workspaceId, publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        return mapToResponse(collection);
    }

    @Transactional(readOnly = true)
    public Page<CollectionResponse> listCollections(UUID workspaceId, Pageable pageable) {
        return collectionRepository.findByWorkspaceId(workspaceId, pageable)
                .map(this::mapToResponse);
    }

    private CollectionResponse mapToResponse(KnowledgeCollection collection) {
        return new CollectionResponse(
                collection.getPublicId(),
                collection.getWorkspaceId().toString(), // We can return String or UUID based on what we want. Actually, publicId of workspace would be better, but we only have UUID here.
                collection.getName(),
                collection.getDescription(),
                collection.getPurpose(),
                collection.getStatus(),
                collection.getDefaultIngestionProfile(),
                collection.getDefaultContextPolicy(),
                0L, // document count placeholder
                0L, // chunk count placeholder
                collection.getCreatedAt(),
                collection.getUpdatedAt()
        );
    }
}
