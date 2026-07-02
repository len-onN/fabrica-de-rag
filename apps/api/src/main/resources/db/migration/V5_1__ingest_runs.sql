CREATE TABLE ingest_runs (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id UUID NOT NULL,
    document_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE NULL,

    CONSTRAINT fk_ingest_runs_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_ingest_runs_document FOREIGN KEY (document_id) REFERENCES documents(id),
    CONSTRAINT fk_ingest_runs_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_ingest_runs_workspace_document ON ingest_runs(workspace_id, document_id);
CREATE INDEX idx_ingest_runs_status ON ingest_runs(workspace_id, status);
