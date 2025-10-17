# REST API Specification

## ADDED Requirements

### Requirement: API-Key Authentication
The system SHALL use API-key based authentication for all API requests.

#### Scenario: Valid API key
- **WHEN** a user makes a request with a valid API key in the Authorization header
- **THEN** the system authenticates the request
- **AND** the system grants access to the user's documents

#### Scenario: Missing API key
- **WHEN** a user makes a request without an API key
- **THEN** the system returns a 401 Unauthorized error

#### Scenario: Invalid API key
- **WHEN** a user makes a request with an invalid API key
- **THEN** the system returns a 401 Unauthorized error with a clear message

### Requirement: Document Endpoints
The system SHALL provide RESTful endpoints for document CRUD operations.

#### Scenario: Create document - POST /api/documents
- **WHEN** a user POSTs a JSON document with title, content, and optional tags
- **THEN** the system creates the document and returns 201 Created
- **AND** the response includes the created document with its ID and metadata

#### Scenario: Get document - GET /api/documents/{id}
- **WHEN** a user GETs a document by ID
- **THEN** the system returns 200 OK with the document
- **OR** the system returns 404 Not Found if the document doesn't exist

#### Scenario: Update document - PUT /api/documents/{id}
- **WHEN** a user PUTs updated document data
- **THEN** the system updates the document, creates a new version, and returns 200 OK
- **AND** the response includes the updated document

#### Scenario: Delete document - DELETE /api/documents/{id}
- **WHEN** a user DELETEs a document
- **THEN** the system soft-deletes the document and returns 204 No Content

#### Scenario: List documents - GET /api/documents
- **WHEN** a user GETs the documents collection
- **THEN** the system returns 200 OK with a paginated list of documents
- **AND** the system supports query parameters for pagination (page, size, sort)

### Requirement: Tag Endpoints
The system SHALL provide RESTful endpoints for tag management.

#### Scenario: Create tag - POST /api/tags
- **WHEN** a user POSTs a tag with name and optional parent ID
- **THEN** the system creates the tag and returns 201 Created

#### Scenario: Get tag - GET /api/tags/{id}
- **WHEN** a user GETs a tag by ID
- **THEN** the system returns 200 OK with tag details including parent and children

#### Scenario: Update tag - PUT /api/tags/{id}
- **WHEN** a user PUTs updated tag data
- **THEN** the system updates the tag and returns 200 OK

#### Scenario: Delete tag - DELETE /api/tags/{id}
- **WHEN** a user DELETEs an unused tag
- **THEN** the system deletes the tag and returns 204 No Content
- **OR** the system returns 409 Conflict if the tag has associated documents

#### Scenario: List tags - GET /api/tags
- **WHEN** a user GETs the tags collection
- **THEN** the system returns 200 OK with the hierarchical tag structure

### Requirement: Search Endpoints
The system SHALL provide RESTful endpoints for searching documents.

#### Scenario: Full-text search - GET /api/search?q={query}
- **WHEN** a user performs a search with query parameter
- **THEN** the system returns 200 OK with matching documents
- **AND** the system supports additional filters (tags, dates)

#### Scenario: Semantic search - POST /api/search/semantic
- **WHEN** a user POSTs a semantic search query
- **THEN** the system returns 200 OK with semantically similar documents
- **AND** the system includes similarity scores

### Requirement: Version Endpoints
The system SHALL provide RESTful endpoints for document version history.

#### Scenario: List versions - GET /api/documents/{id}/versions
- **WHEN** a user GETs the versions collection for a document
- **THEN** the system returns 200 OK with up to 10 historical versions

#### Scenario: Get specific version - GET /api/documents/{id}/versions/{versionNumber}
- **WHEN** a user GETs a specific version
- **THEN** the system returns 200 OK with the complete version data

#### Scenario: Restore version - POST /api/documents/{id}/versions/{versionNumber}/restore
- **WHEN** a user POSTs to restore a version
- **THEN** the system creates a new current version from the historical version
- **AND** the system returns 200 OK with the restored document

#### Scenario: Compare versions - GET /api/documents/{id}/versions/compare?from={v1}&to={v2}
- **WHEN** a user requests comparison between two versions
- **THEN** the system returns 200 OK with differences highlighted

### Requirement: Error Responses
The system SHALL provide consistent error responses following REST conventions.

#### Scenario: Validation error
- **WHEN** a request has invalid data
- **THEN** the system returns 400 Bad Request with detailed validation errors

#### Scenario: Not found error
- **WHEN** a requested resource doesn't exist
- **THEN** the system returns 404 Not Found with a clear message

#### Scenario: Server error
- **WHEN** an internal error occurs
- **THEN** the system returns 500 Internal Server Error
- **AND** the system logs the error for debugging without exposing internal details

### Requirement: Response Format
The system SHALL return JSON responses with consistent structure.

#### Scenario: Success response structure
- **WHEN** an API request succeeds
- **THEN** the response includes the requested data in JSON format
- **AND** the response follows a consistent structure with appropriate HTTP status code

#### Scenario: Error response structure
- **WHEN** an API request fails
- **THEN** the response includes an error object with code, message, and details
- **AND** the response uses appropriate HTTP status codes

### Requirement: Content Negotiation
The system SHALL support JSON content type for all API requests and responses.

#### Scenario: JSON content type
- **WHEN** a user makes an API request
- **THEN** the system accepts Content-Type: application/json for requests
- **AND** the system returns responses with Content-Type: application/json

#### Scenario: Unsupported content type
- **WHEN** a user sends a request with an unsupported content type
- **THEN** the system returns 415 Unsupported Media Type

### Requirement: CORS Support
The system SHALL support Cross-Origin Resource Sharing (CORS) for web clients.

#### Scenario: CORS preflight
- **WHEN** a browser sends an OPTIONS request for CORS preflight
- **THEN** the system responds with appropriate CORS headers
- **AND** the system allows requests from configured origins

### Requirement: Rate Limiting
The system SHALL implement rate limiting to prevent abuse.

#### Scenario: Within rate limit
- **WHEN** a user makes requests within the rate limit
- **THEN** the system processes the requests normally

#### Scenario: Exceed rate limit
- **WHEN** a user exceeds the rate limit
- **THEN** the system returns 429 Too Many Requests
- **AND** the system includes Retry-After header

### Requirement: API Versioning
The system SHALL support API versioning to enable future changes without breaking existing clients.

#### Scenario: API version in URL
- **WHEN** a user makes a request to /api/v1/documents
- **THEN** the system routes to the appropriate API version
- **AND** the system supports multiple versions concurrently during transitions
