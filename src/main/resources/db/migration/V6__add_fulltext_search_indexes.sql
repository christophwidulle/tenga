-- Add full-text search support using PostgreSQL tsvector
-- Note: This requires PostgreSQL (not H2)

-- Add tsvector column for full-text search
ALTER TABLE documents ADD COLUMN content_search_vector tsvector;

-- Create function to update search vector
CREATE OR REPLACE FUNCTION update_document_search_vector() RETURNS TRIGGER AS $$
BEGIN
    NEW.content_search_vector :=
        setweight(to_tsvector('english', COALESCE(NEW.title, '')), 'A') ||
        setweight(to_tsvector('english', COALESCE(NEW.content, '')), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically update search vector
CREATE TRIGGER trg_update_document_search_vector
    BEFORE INSERT OR UPDATE OF title, content
    ON documents
    FOR EACH ROW
    EXECUTE FUNCTION update_document_search_vector();

-- Create GIN index for full-text search
CREATE INDEX idx_documents_search_vector ON documents USING GIN(content_search_vector);

-- Update existing rows (if any)
UPDATE documents SET content_search_vector =
    setweight(to_tsvector('english', COALESCE(title, '')), 'A') ||
    setweight(to_tsvector('english', COALESCE(content, '')), 'B');
