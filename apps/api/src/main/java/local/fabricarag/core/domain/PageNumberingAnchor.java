package local.fabricarag.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "page_numbering_anchors")
public class PageNumberingAnchor {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "document_page_id")
    private UUID documentPageId;

    @Column(name = "file_page_number")
    private Integer filePageNumber;

    @Column(name = "printed_label")
    private String printedLabel;

    @Column(name = "numbering_style")
    private String numberingStyle;

    @Column(name = "apply_direction")
    private String applyDirection;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected PageNumberingAnchor() {
    }

    public PageNumberingAnchor(UUID id, String publicId, UUID workspaceId, UUID documentId, UUID documentPageId,
                               Integer filePageNumber, String printedLabel, String numberingStyle,
                               String applyDirection, UUID createdByUserId) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.documentId = documentId;
        this.documentPageId = documentPageId;
        this.filePageNumber = filePageNumber;
        this.printedLabel = printedLabel;
        this.numberingStyle = numberingStyle;
        this.applyDirection = applyDirection;
        this.createdByUserId = createdByUserId;
        this.createdAt = OffsetDateTime.now();
    }

    public void update(String printedLabel, String numberingStyle, String applyDirection) {
        this.printedLabel = printedLabel;
        this.numberingStyle = numberingStyle;
        this.applyDirection = applyDirection;
    }

    public UUID getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public UUID getDocumentPageId() {
        return documentPageId;
    }

    public Integer getFilePageNumber() {
        return filePageNumber;
    }

    public String getPrintedLabel() {
        return printedLabel;
    }

    public String getNumberingStyle() {
        return numberingStyle;
    }

    public String getApplyDirection() {
        return applyDirection;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
