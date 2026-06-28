CREATE TABLE retrieval_feedback (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id VARCHAR(50) NOT NULL,
    knowledge_collection_id UUID NOT NULL,
    document_id VARCHAR(50),
    chunk_id VARCHAR(50),
    query_event_id VARCHAR(50),
    feedback_type VARCHAR(50) NOT NULL,
    note TEXT,
    created_by_user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_feedback_collection FOREIGN KEY (knowledge_collection_id) REFERENCES knowledge_collections(id) ON DELETE CASCADE
);

CREATE INDEX idx_retrieval_feedback_workspace ON retrieval_feedback(workspace_id);
