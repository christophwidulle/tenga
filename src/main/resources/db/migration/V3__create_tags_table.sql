-- Create tags table for hierarchical tag system
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tags_parent FOREIGN KEY (parent_id)
        REFERENCES tags(id) ON DELETE SET NULL,
    CONSTRAINT uq_tag_name_parent UNIQUE (name, parent_id),
    CONSTRAINT chk_tag_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT chk_not_self_parent CHECK (id != parent_id)
);

-- Index for parent-child queries
CREATE INDEX idx_tags_parent_id ON tags(parent_id);

-- Index for name lookups
CREATE INDEX idx_tags_name ON tags(name);
