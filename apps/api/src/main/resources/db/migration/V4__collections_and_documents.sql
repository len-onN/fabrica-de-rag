CREATE TABLE knowledge_collections (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    purpose VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    default_ingestion_profile VARCHAR(50) NOT NULL,
    default_context_policy VARCHAR(50) NOT NULL,
    default_embedding_model VARCHAR(255) NOT NULL,
    default_vector_binding_id UUID NULL,
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE NULL,

    CONSTRAINT fk_knowledge_collections_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_knowledge_collections_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_knowledge_collections_workspace_status ON knowledge_collections(workspace_id, status);
CREATE INDEX idx_knowledge_collections_workspace_updated_at ON knowledge_collections(workspace_id, updated_at);
CREATE UNIQUE INDEX uk_knowledge_collections_workspace_name_active ON knowledge_collections(workspace_id, name) WHERE deleted_at IS NULL;

CREATE TABLE documents (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id UUID NOT NULL,
    knowledge_collection_id UUID NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_uri VARCHAR(1024) NOT NULL,
    source_hash VARCHAR(255) NOT NULL,
    source_locator JSONB,
    original_filename VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    page_count INTEGER NOT NULL,
    storage_uri VARCHAR(1024) NOT NULL,
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE NULL,

    CONSTRAINT fk_documents_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id),
    CONSTRAINT fk_documents_collection FOREIGN KEY (knowledge_collection_id) REFERENCES knowledge_collections(id),
    CONSTRAINT fk_documents_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_documents_workspace_collection_status ON documents(workspace_id, knowledge_collection_id, status);
CREATE INDEX idx_documents_workspace_hash ON documents(workspace_id, source_hash);
