# MCP Integration Specification

## ADDED Requirements

### Requirement: MCP Server Implementation
The system SHALL implement a Model Context Protocol (MCP) server to enable chat agent integration.

#### Scenario: MCP server initialization
- **WHEN** the application starts
- **THEN** the system initializes the MCP server on a configured port
- **AND** the system publishes the MCP manifest describing available methods

#### Scenario: MCP connection
- **WHEN** a chat agent connects to the MCP server
- **THEN** the system establishes the connection
- **AND** the system authenticates the client using API-key

### Requirement: Create Note Method
The system SHALL provide an MCP method `createNote` for creating new knowledge documents.

#### Scenario: Create note via MCP
- **WHEN** a chat agent calls createNote with content and optional tags
- **THEN** the system creates a new document
- **AND** the system extracts title from content if not provided
- **AND** the system suggests tags based on content analysis
- **AND** the system returns the created document ID and metadata

#### Scenario: Create note with explicit tags
- **WHEN** a chat agent calls createNote with specified tags
- **THEN** the system uses the provided tags
- **AND** the system validates tag hierarchy (see tag-management spec)

### Requirement: Update Note Method
The system SHALL provide an MCP method `updateNote` for updating existing documents.

#### Scenario: Update note via MCP
- **WHEN** a chat agent calls updateNote with document ID and new content
- **THEN** the system updates the document
- **AND** the system creates a new version (see version-control spec)
- **AND** the system returns the updated document

#### Scenario: Append to note
- **WHEN** a chat agent calls updateNote with append flag
- **THEN** the system appends new content to existing content
- **AND** the system creates a new version

### Requirement: Get Notes Method
The system SHALL provide an MCP method `getNotes` for retrieving documents.

#### Scenario: Get all notes via MCP
- **WHEN** a chat agent calls getNotes without filters
- **THEN** the system returns all documents with pagination
- **AND** the system includes metadata but may truncate long content

#### Scenario: Get notes by tag via MCP
- **WHEN** a chat agent calls getNotes with tag filter
- **THEN** the system returns documents matching the tag hierarchy
- **AND** the system includes child tags in the results

#### Scenario: Get single note via MCP
- **WHEN** a chat agent calls getNotes with a specific document ID
- **THEN** the system returns the complete document with full content

### Requirement: Search Notes Method
The system SHALL provide an MCP method `searchNotes` for searching documents.

#### Scenario: Search via MCP
- **WHEN** a chat agent calls searchNotes with a query
- **THEN** the system performs full-text search
- **AND** the system returns matching documents with snippets

#### Scenario: Semantic search via MCP
- **WHEN** a chat agent calls searchNotes with semantic flag
- **THEN** the system performs semantic search
- **AND** the system returns conceptually similar documents with similarity scores

### Requirement: MCP Conversational Context
The system SHALL provide conversational context to help chat agents understand user intent.

#### Scenario: Context-aware note creation
- **WHEN** a chat agent is in a conversation about a specific topic
- **THEN** the system suggests relevant tags based on conversation context
- **AND** the system can link new notes to previously discussed notes

#### Scenario: Similarity detection during creation
- **WHEN** a chat agent creates a note similar to existing content
- **THEN** the system detects similarity and suggests updating the existing note instead
- **AND** the system provides the existing note for comparison

### Requirement: MCP Error Handling
The system SHALL provide clear error responses through MCP protocol.

#### Scenario: MCP validation error
- **WHEN** an MCP method receives invalid parameters
- **THEN** the system returns a structured error with details
- **AND** the system maintains the MCP connection

#### Scenario: MCP server error
- **WHEN** an internal error occurs during MCP method execution
- **THEN** the system returns an error response without disconnecting
- **AND** the system logs the error for debugging

### Requirement: MCP Manifest
The system SHALL publish an MCP manifest describing all available methods and their parameters.

#### Scenario: MCP manifest retrieval
- **WHEN** a chat agent requests the MCP manifest
- **THEN** the system returns complete method signatures
- **AND** the system includes parameter descriptions and examples

### Requirement: MCP Streaming Support
The system SHALL support streaming responses for long content or multiple results.

#### Scenario: Stream search results
- **WHEN** a search returns many results
- **THEN** the system streams results incrementally
- **AND** the system allows the client to cancel mid-stream

### Requirement: MCP Rate Limiting
The system SHALL implement rate limiting per API-key for MCP connections.

#### Scenario: MCP within rate limit
- **WHEN** an MCP client makes requests within the limit
- **THEN** the system processes requests normally

#### Scenario: MCP exceeds rate limit
- **WHEN** an MCP client exceeds the rate limit
- **THEN** the system returns a rate limit error
- **AND** the system maintains the connection for future requests

### Requirement: MCP Authentication
The system SHALL authenticate MCP connections using the same API-key mechanism as REST API.

#### Scenario: MCP authentication on connect
- **WHEN** a chat agent connects with a valid API-key
- **THEN** the system establishes an authenticated session
- **AND** all subsequent method calls use this authentication context

#### Scenario: MCP authentication failure
- **WHEN** a chat agent attempts to connect without valid credentials
- **THEN** the system rejects the connection
- **AND** the system returns an authentication error

### Requirement: MCP Batch Operations
The system SHALL support batch operations through MCP for efficiency.

#### Scenario: Batch create notes
- **WHEN** a chat agent submits multiple notes in one request
- **THEN** the system creates all notes in a single transaction
- **AND** the system returns all created document IDs
- **OR** the system rolls back all changes if any creation fails
