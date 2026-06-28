CREATE TABLE document_pages (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id UUID NOT NULL,
    document_id UUID NOT NULL,
    file_page_number INTEGER NOT NULL,
    detected_printed_label VARCHAR(50),
    effective_printed_label VARCHAR(50),
    numbering_style VARCHAR(20),
    page_role VARCHAR(20) NOT NULL,
    include_in_search BOOLEAN NOT NULL,
    include_in_numbering BOOLEAN NOT NULL,
    ocr_status VARCHAR(20) NOT NULL,
    text_quality JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_locator JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_document_pages_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_document_pages_document FOREIGN KEY (document_id) REFERENCES documents(id)
);

CREATE UNIQUE INDEX uk_document_pages_document_file_page ON document_pages(document_id, file_page_number);
CREATE INDEX idx_document_pages_workspace_document ON document_pages(workspace_id, document_id);

CREATE TABLE page_numbering_anchors (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id UUID NOT NULL,
    document_id UUID NOT NULL,
    document_page_id UUID NOT NULL,
    file_page_number INTEGER NOT NULL,
    printed_label VARCHAR(50) NOT NULL,
    numbering_style VARCHAR(20) NOT NULL,
    apply_direction VARCHAR(20) NOT NULL,
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_page_numbering_anchors_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_page_numbering_anchors_document FOREIGN KEY (document_id) REFERENCES documents(id),
    CONSTRAINT fk_page_numbering_anchors_page FOREIGN KEY (document_page_id) REFERENCES document_pages(id),
    CONSTRAINT fk_page_numbering_anchors_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_page_numbering_anchors_workspace_doc ON page_numbering_anchors(workspace_id, document_id);
CREATE UNIQUE INDEX uk_page_numbering_anchors_page ON page_numbering_anchors(document_page_id);
