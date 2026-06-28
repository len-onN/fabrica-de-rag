CREATE TABLE vector_connections (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id VARCHAR(50),
    owner_user_id VARCHAR(50),
    owner_scope VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    mode VARCHAR(50) NOT NULL,
    endpoint VARCHAR(255),
    auth_secret_ref VARCHAR(255),
    capabilities JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE vector_index_bindings (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id VARCHAR(50) NOT NULL,
    knowledge_collection_id VARCHAR(50) NOT NULL,
    vector_connection_id UUID NOT NULL REFERENCES vector_connections(id) ON DELETE CASCADE,
    remote_collection_name VARCHAR(255) NOT NULL,
    remote_namespace VARCHAR(255),
    vector_name VARCHAR(100) NOT NULL,
    embedding_model VARCHAR(100) NOT NULL,
    embedding_model_version VARCHAR(100) NOT NULL,
    dimension INT NOT NULL,
    distance_metric VARCHAR(50) NOT NULL,
    payload_contract_version VARCHAR(50) NOT NULL,
    sync_status VARCHAR(50) NOT NULL,
    last_sync_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE chunk_embeddings (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    workspace_id VARCHAR(50) NOT NULL,
    chunk_id UUID NOT NULL REFERENCES chunks(id) ON DELETE CASCADE,
    vector_binding_id UUID NOT NULL REFERENCES vector_index_bindings(id) ON DELETE CASCADE,
    embedding_model VARCHAR(100) NOT NULL,
    embedding_model_version VARCHAR(100) NOT NULL,
    dimension INT NOT NULL,
    distance_metric VARCHAR(50) NOT NULL,
    vector_space VARCHAR(100) NOT NULL,
    qdrant_point_id VARCHAR(128) NOT NULL,
    payload_contract_version VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE UNIQUE INDEX uk_chunk_embeddings_binding_chunk_model ON chunk_embeddings(vector_binding_id, chunk_id, embedding_model);
CREATE INDEX idx_chunk_embeddings_workspace_status ON chunk_embeddings(workspace_id, status);
