-- Create document_versions table for version control
CREATE TABLE document_versions (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    version_number INTEGER NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    tags_snapshot TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_summary VARCHAR(1000),
    CONSTRAINT fk_document_versions_document FOREIGN KEY (document_id)
        REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT uq_document_version UNIQUE (document_id, version_number),
    CONSTRAINT chk_version_number_positive CHECK (version_number > 0)
);

-- Index for retrieving version history
CREATE INDEX idx_document_versions_document_id ON document_versions(document_id, version_number DESC);

-- Index for created_at
CREATE INDEX idx_document_versions_created_at ON document_versions(created_at DESC);
