CREATE TABLE chunks (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id VARCHAR(50) NOT NULL,
    document_id VARCHAR(50) NOT NULL,
    chunking_strategy VARCHAR(50) NOT NULL,
    chunking_version VARCHAR(50) NOT NULL,
    sequence_number INT NOT NULL,
    content_kind VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    heading_path JSONB,
    token_count INT NOT NULL,
    source_hash VARCHAR(128) NOT NULL,
    source_locator JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_chunks_doc ON chunks(document_id);
CREATE INDEX idx_chunks_workspace ON chunks(workspace_id);

CREATE TABLE chunk_relations (
    id UUID PRIMARY KEY,
    from_chunk_id UUID NOT NULL REFERENCES chunks(id) ON DELETE CASCADE,
    to_chunk_id UUID NOT NULL REFERENCES chunks(id) ON DELETE CASCADE,
    relation_type VARCHAR(50) NOT NULL,
    weight FLOAT DEFAULT 1.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_chunk_relations_from ON chunk_relations(from_chunk_id);
