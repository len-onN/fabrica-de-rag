ALTER TABLE ingest_runs 
ADD COLUMN retry_of_run_id UUID NULL,
ADD COLUMN reprocess_of_run_id UUID NULL;

ALTER TABLE ingest_runs ADD CONSTRAINT fk_ingest_runs_retry FOREIGN KEY (retry_of_run_id) REFERENCES ingest_runs(id);
ALTER TABLE ingest_runs ADD CONSTRAINT fk_ingest_runs_reprocess FOREIGN KEY (reprocess_of_run_id) REFERENCES ingest_runs(id);

CREATE TABLE ingest_steps (
    id UUID PRIMARY KEY,
    public_id VARCHAR(50) NOT NULL UNIQUE,
    run_id UUID NOT NULL,
    step_name VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    attempt INT NOT NULL DEFAULT 1,
    idempotency_key VARCHAR(255) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NULL,
    finished_at TIMESTAMP WITH TIME ZONE NULL,
    duration_ms BIGINT NULL,
    error_code VARCHAR(100) NULL,
    error_message TEXT NULL,
    safe_details JSONB NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_ingest_steps_run FOREIGN KEY (run_id) REFERENCES ingest_runs(id)
);

CREATE INDEX idx_ingest_steps_run_id ON ingest_steps(run_id);
CREATE INDEX idx_ingest_steps_run_status ON ingest_steps(run_id, status);

CREATE TABLE ingest_run_logs (
    id UUID PRIMARY KEY,
    run_id UUID NOT NULL,
    step_id UUID NULL,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    details JSONB NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_ingest_run_logs_run FOREIGN KEY (run_id) REFERENCES ingest_runs(id),
    CONSTRAINT fk_ingest_run_logs_step FOREIGN KEY (step_id) REFERENCES ingest_steps(id)
);

CREATE INDEX idx_ingest_run_logs_run_id ON ingest_run_logs(run_id);
