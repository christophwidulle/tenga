-- Create documents table for knowledge base MVP
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    current_version INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT chk_title_not_empty CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_content_size CHECK (LENGTH(content) <= 1048576)
);

-- Index for soft delete queries
CREATE INDEX idx_documents_deleted_at ON documents(deleted_at);

-- Index for updated_at for sorting
CREATE INDEX idx_documents_updated_at ON documents(updated_at DESC);

-- Index for created_at for sorting
CREATE INDEX idx_documents_created_at ON documents(created_at DESC);
