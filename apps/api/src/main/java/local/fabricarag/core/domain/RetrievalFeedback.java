package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "retrieval_feedback")
public class RetrievalFeedback {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "knowledge_collection_id")
    private UUID knowledgeCollectionId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "chunk_id")
    private String chunkId;

    @Column(name = "query_event_id")
    private String queryEventId;

    @Column(name = "feedback_type")
    private String feedbackType;

    @Column(name = "note")
    private String note;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected RetrievalFeedback() {
    }

    public RetrievalFeedback(UUID id, String publicId, String workspaceId, UUID knowledgeCollectionId, String documentId, String chunkId, String queryEventId, String feedbackType, String note, UUID createdByUserId) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.knowledgeCollectionId = knowledgeCollectionId;
        this.documentId = documentId;
        this.chunkId = chunkId;
        this.queryEventId = queryEventId;
        this.feedbackType = feedbackType;
        this.note = note;
        this.createdByUserId = createdByUserId;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public UUID getKnowledgeCollectionId() {
        return knowledgeCollectionId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getChunkId() {
        return chunkId;
    }

    public String getQueryEventId() {
        return queryEventId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public String getNote() {
        return note;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
