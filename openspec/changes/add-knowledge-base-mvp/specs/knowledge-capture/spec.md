# Knowledge Capture Specification

## ADDED Requirements

### Requirement: Document Storage
The system SHALL store knowledge entries as structured Markdown documents with metadata including title, content, tags, creation timestamp, and last modified timestamp.

#### Scenario: Create new document
- **WHEN** a user creates a new knowledge entry
- **THEN** the system persists the document with a unique ID, title, content, tags array, creation timestamp, and last modified timestamp
- **AND** the system returns the created document with its assigned ID

#### Scenario: Document with YAML front-matter
- **WHEN** a user creates a document with YAML front-matter
- **THEN** the system parses and stores the front-matter metadata separately
- **AND** the system preserves the original Markdown content

### Requirement: UTF-8 Encoding
The system SHALL use UTF-8 encoding for all text content to support international characters.

#### Scenario: Store non-ASCII characters
- **WHEN** a user creates a document with non-ASCII characters (German umlauts, emojis, etc.)
- **THEN** the system correctly stores and retrieves the content without corruption

### Requirement: Document Retrieval
The system SHALL provide retrieval of individual documents by unique identifier.

#### Scenario: Retrieve existing document
- **WHEN** a user requests a document by its ID
- **THEN** the system returns the document with all metadata and content
- **AND** the response includes the current version number

#### Scenario: Retrieve non-existent document
- **WHEN** a user requests a document that does not exist
- **THEN** the system returns a 404 Not Found error

### Requirement: Document Listing
The system SHALL provide listing of all documents with pagination support.

#### Scenario: List all documents
- **WHEN** a user requests a list of documents
- **THEN** the system returns documents ordered by last modified timestamp (newest first)
- **AND** the system includes basic metadata (ID, title, tags, timestamps) but excludes full content

#### Scenario: Paginated listing
- **WHEN** a user requests a page of documents with size and offset
- **THEN** the system returns the requested page with total count information

### Requirement: Document Update
The system SHALL allow updating existing documents while maintaining version history.

#### Scenario: Update document content
- **WHEN** a user updates a document's title or content
- **THEN** the system creates a new version (see version-control spec)
- **AND** the system updates the last modified timestamp
- **AND** the system returns the updated document

#### Scenario: Update document tags
- **WHEN** a user updates a document's tags
- **THEN** the system validates tag relationships (see tag-management spec)
- **AND** the system updates the tag associations
- **AND** the system creates a new version

### Requirement: Document Deletion
The system SHALL support soft deletion of documents to prevent data loss.

#### Scenario: Delete document
- **WHEN** a user deletes a document
- **THEN** the system marks the document as deleted without removing it from storage
- **AND** the system excludes deleted documents from listings and searches
- **AND** the system retains version history for audit purposes

#### Scenario: Restore deleted document
- **WHEN** a user restores a deleted document
- **THEN** the system marks the document as active
- **AND** the system includes it in listings and searches again

### Requirement: No File Uploads
The system SHALL NOT support file uploads or binary attachments in MVP.

#### Scenario: Attempt file upload
- **WHEN** a user attempts to upload a file
- **THEN** the system returns a 400 Bad Request error with a clear message

### Requirement: Performance Target
The system SHALL respond to document operations in under 500ms for databases with fewer than 10,000 documents.

#### Scenario: Fast retrieval
- **WHEN** a user retrieves a document from a database with < 10,000 documents
- **THEN** the system responds in less than 500ms
