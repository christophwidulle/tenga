-- Create api_keys table for authentication
CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    key_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    CONSTRAINT uq_api_key_hash UNIQUE (key_hash)
);

-- Index for authentication lookups
CREATE INDEX idx_api_keys_key_hash ON api_keys(key_hash);

-- Index for last_used_at for analytics
CREATE INDEX idx_api_keys_last_used_at ON api_keys(last_used_at DESC);
