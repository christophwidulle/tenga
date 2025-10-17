-- Add content_vector column for future semantic search (Phase 2)
-- This prepares the schema but won't be used in MVP

-- Note: This requires pgvector extension which should be installed separately:
-- CREATE EXTENSION IF NOT EXISTS vector;
-- For MVP, we'll defer this to Phase 2 when semantic search is implemented

-- Add placeholder comment for future extension
COMMENT ON TABLE documents IS 'Document storage table. content_vector column will be added in Phase 2 for semantic search using pgvector extension.';

-- The actual column will be added in Phase 2 with:
-- ALTER TABLE documents ADD COLUMN content_vector vector(1536);
-- CREATE INDEX ON documents USING hnsw (content_vector vector_cosine_ops);
