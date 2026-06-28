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
@Table(name = "document_pages")
public class DocumentPage {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "file_page_number")
    private Integer filePageNumber;

    @Column(name = "detected_printed_label")
    private String detectedPrintedLabel;

    @Column(name = "effective_printed_label")
    private String effectivePrintedLabel;

    @Column(name = "numbering_style")
    private String numberingStyle;

    @Column(name = "page_role")
    private String pageRole;

    @Column(name = "include_in_search")
    private Boolean includeInSearch;

    @Column(name = "include_in_numbering")
    private Boolean includeInNumbering;

    @Column(name = "ocr_status")
    private String ocrStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "text_quality")
    private String textQuality;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_locator")
    private String sourceLocator;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    protected DocumentPage() {
    }

    public DocumentPage(UUID id, String publicId, UUID workspaceId, UUID documentId, Integer filePageNumber,
                        String pageRole, Boolean includeInSearch, Boolean includeInNumbering,
                        String ocrStatus, String textQuality, String sourceLocator) {
        this.id = id;
        this.publicId = publicId;
        this.workspaceId = workspaceId;
        this.documentId = documentId;
        this.filePageNumber = filePageNumber;
        this.pageRole = pageRole;
        this.includeInSearch = includeInSearch;
        this.includeInNumbering = includeInNumbering;
        this.ocrStatus = ocrStatus;
        this.textQuality = textQuality;
        this.sourceLocator = sourceLocator;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void updateLabels(String detectedPrintedLabel, String effectivePrintedLabel, String numberingStyle) {
        this.detectedPrintedLabel = detectedPrintedLabel;
        this.effectivePrintedLabel = effectivePrintedLabel;
        this.numberingStyle = numberingStyle;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateFlags(String pageRole, Boolean includeInSearch, Boolean includeInNumbering) {
        this.pageRole = pageRole;
        this.includeInSearch = includeInSearch;
        this.includeInNumbering = includeInNumbering;
        this.updatedAt = OffsetDateTime.now();
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

    public Integer getFilePageNumber() {
        return filePageNumber;
    }

    public String getDetectedPrintedLabel() {
        return detectedPrintedLabel;
    }

    public String getEffectivePrintedLabel() {
        return effectivePrintedLabel;
    }

    public String getNumberingStyle() {
        return numberingStyle;
    }

    public String getPageRole() {
        return pageRole;
    }

    public Boolean getIncludeInSearch() {
        return includeInSearch;
    }

    public Boolean getIncludeInNumbering() {
        return includeInNumbering;
    }

    public String getOcrStatus() {
        return ocrStatus;
    }

    public String getTextQuality() {
        return textQuality;
    }

    public String getSourceLocator() {
        return sourceLocator;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
