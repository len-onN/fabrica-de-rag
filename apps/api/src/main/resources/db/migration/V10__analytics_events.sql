-- V10__analytics_events.sql

CREATE TABLE analytics_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_version VARCHAR(50) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    origin VARCHAR(50) NOT NULL,
    retention_class VARCHAR(50) NOT NULL,
    workspace_id VARCHAR(50),
    actor_type VARCHAR(50) NOT NULL,
    actor_id VARCHAR(50),
    correlation_id VARCHAR(100) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    resource_type VARCHAR(50),
    resource_id VARCHAR(100),
    properties JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for performance and retention jobs
CREATE INDEX idx_analytics_events_workspace_id ON analytics_events(workspace_id);
CREATE INDEX idx_analytics_events_occurred_at ON analytics_events(occurred_at);
CREATE INDEX idx_analytics_events_retention_class ON analytics_events(retention_class);
CREATE INDEX idx_analytics_events_event_name ON analytics_events(event_name);
