package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chunks")
public class Chunk {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "chunking_strategy")
    private String chunkingStrategy;

    @Column(name = "chunking_version")
    private String chunkingVersion;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "content_kind")
    private String contentKind;

    @Column(name = "content")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "heading_path")
    private String headingPath;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "source_hash")
    private String sourceHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_locator")
    private String sourceLocator;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected Chunk() {
    }

    public Chunk(UUID id, String publicId, String workspaceId, String documentId,
                 String chunkingStrategy, String chunkingVersion, Integer sequenceNumber,
                 String contentKind, String content, String headingPath,
                 Integer tokenCount, String sourceHash, String sourceLocator) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.documentId = documentId;
        this.chunkingStrategy = chunkingStrategy;
        this.chunkingVersion = chunkingVersion;
        this.sequenceNumber = sequenceNumber;
        this.contentKind = contentKind;
        this.content = content;
        this.headingPath = headingPath;
        this.tokenCount = tokenCount;
        this.sourceHash = sourceHash;
        this.sourceLocator = sourceLocator;
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

    public String getDocumentId() {
        return documentId;
    }

    public String getChunkingStrategy() {
        return chunkingStrategy;
    }

    public String getChunkingVersion() {
        return chunkingVersion;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getContentKind() {
        return contentKind;
    }

    public String getContent() {
        return content;
    }

    public String getHeadingPath() {
        return headingPath;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public String getSourceHash() {
        return sourceHash;
    }

    public String getSourceLocator() {
        return sourceLocator;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
