# Tenga Knowledge Base MVP

Personal knowledge base system with conversational interaction through chat agents.

## Features

- **Document Management**: Create, update, delete, and restore Markdown documents
- **Hierarchical Tagging**: Organize documents with parent-child tag relationships
- **Version Control**: Automatic versioning with 10-version history
- **Full-Text Search**: PostgreSQL-powered search with ranking
- **REST API**: Complete RESTful API with OpenAPI documentation
- **MCP Integration**: JSON-RPC 2.0 interface for chat agent integration
- **API Key Authentication**: Secure bearer token authentication
- **Rate Limiting**: 60 requests per minute per API key

## Tech Stack

- **Backend**: Spring Boot 3.5.6, Java 21
- **Database**: PostgreSQL 17 with full-text search
- **Frontend**: Vue.js 3, TypeScript, Vite (integrated in Spring Boot)
- **Documentation**: OpenAPI 3.0 (SpringDoc)
- **Security**: Spring Security with API-key authentication
- **Build**: Maven (builds both backend and frontend via frontend-maven-plugin)

## Quick Start

### Prerequisites

- Docker and Docker Compose
- OR Java 21+ and PostgreSQL 17+

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd tenga

# Start services (PostgreSQL + Application with integrated frontend)
docker-compose up -d

# View logs
docker-compose logs -f tenga-app

# Access the application (everything on port 8080)
# Web UI: http://localhost:8080
# API: http://localhost:8080/api/v1
# Swagger UI: http://localhost:8080/swagger-ui.html
# MCP Manifest: http://localhost:8080/mcp/manifest
```

### Option 2: Local Development with Maven

```bash
# Start PostgreSQL (or use Docker)
docker-compose up -d postgres

# Build and run (includes frontend build)
mvn spring-boot:run

# Access at http://localhost:8080
```

### Option 3: Frontend Development Mode

If you want hot-reload for frontend development:

```bash
# Terminal 1: Start backend only
mvn spring-boot:run

# Terminal 2: Start frontend dev server
cd frontend
npm install
npm run dev

# Access at http://localhost:3000 (proxied to backend at 8080)
```

## API Documentation

Once running, visit:
- **Web UI**: http://localhost:8080 (integrated in Spring Boot)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **MCP Manifest**: http://localhost:8080/mcp/manifest

**Note**: Frontend and backend are served from the same port (8080) in production/Docker mode. The Vue.js frontend is built during Maven build and served as static resources by Spring Boot.

## API Authentication

All API endpoints require authentication using an API key in the `Authorization` header:

```
Authorization: Bearer <your-api-key>
```

### Creating an API Key

Currently, API keys must be created directly in the database. Run this to create a test key:

```java
// Use the ApiKeyService.createApiKey() method
// This will return a UUID that you can use for authentication
```

Or use the API endpoint (if exposed):
```bash
curl -X POST http://localhost:8080/api/v1/admin/api-keys
```

## API Endpoints

### Documents
- `POST /api/v1/documents` - Create document
- `GET /api/v1/documents/{id}` - Get document
- `GET /api/v1/documents` - List documents (paginated)
- `PUT /api/v1/documents/{id}` - Update document
- `DELETE /api/v1/documents/{id}` - Soft delete document
- `POST /api/v1/documents/{id}/restore` - Restore deleted document

### Tags
- `POST /api/v1/tags` - Create tag
- `GET /api/v1/tags/{id}` - Get tag
- `GET /api/v1/tags` - List all tags
- `GET /api/v1/tags/hierarchy` - Get tag hierarchy
- `PUT /api/v1/tags/{id}` - Update tag name
- `DELETE /api/v1/tags/{id}` - Delete tag
- `GET /api/v1/tags/search?q=term` - Search tags

### Versions
- `GET /api/v1/documents/{id}/versions` - Get version history
- `GET /api/v1/documents/{id}/versions/{number}` - Get specific version
- `POST /api/v1/documents/{id}/versions/{number}/restore` - Restore version
- `GET /api/v1/documents/{id}/versions/compare?v1=1&v2=2` - Compare versions

### Search
- `GET /api/v1/search?q=query&tags=tag1,tag2` - Full-text search

## MCP Integration (Chat Agents)

Tenga provides a Model Context Protocol (MCP) server for integration with chat agents like Claude.

### MCP Endpoint

- **URL**: `POST /mcp`
- **Protocol**: JSON-RPC 2.0
- **Authentication**: Bearer token (same as REST API)
- **Manifest**: `GET /mcp/manifest` (public, no auth required)

### MCP Methods

#### createNote
Create a new document with optional title and tag extraction.

```json
{
  "jsonrpc": "2.0",
  "method": "createNote",
  "params": {
    "title": "Optional title",
    "content": "Document content in Markdown",
    "tags": ["optional", "tags"],
    "suggestTags": true
  },
  "id": 1
}
```

If `title` is omitted, it will be extracted from the first line of content. If `suggestTags` is true, tags will be auto-generated from content analysis.

#### updateNote
Update an existing document with append support.

```json
{
  "jsonrpc": "2.0",
  "method": "updateNote",
  "params": {
    "documentId": 123,
    "content": "New or additional content",
    "append": false,
    "changeSummary": "Optional change description"
  },
  "id": 2
}
```

Set `append: true` to append content instead of replacing it.

#### getNotes
Retrieve documents with optional filtering.

```json
{
  "jsonrpc": "2.0",
  "method": "getNotes",
  "params": {
    "documentId": 123,
    "tags": ["tag1", "tag2"],
    "page": 0,
    "size": 20,
    "includeFullContent": false
  },
  "id": 3
}
```

When `includeFullContent` is false, content is truncated to 200 characters for performance.

#### searchNotes
Full-text search across documents.

```json
{
  "jsonrpc": "2.0",
  "method": "searchNotes",
  "params": {
    "query": "search terms",
    "tags": ["optional", "filter"],
    "page": 0,
    "size": 20,
    "semantic": false
  },
  "id": 4
}
```

Note: Semantic search (`semantic: true`) is not yet implemented and will fall back to full-text search.

#### batchCreateNotes
Create multiple documents in a single transaction.

```json
{
  "jsonrpc": "2.0",
  "method": "batchCreateNotes",
  "params": {
    "notes": [
      {
        "content": "First document",
        "tags": ["batch"]
      },
      {
        "content": "Second document",
        "tags": ["batch"]
      }
    ]
  },
  "id": 5
}
```

All notes are created in a single transaction. If any note fails, all changes are rolled back.

### Rate Limiting

MCP endpoints are rate-limited to **60 requests per minute** per API key. Rate limit headers are included in responses:

```
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 2025-10-17T13:45:00Z
```

When rate limit is exceeded, a `429 Too Many Requests` response is returned with a JSON-RPC error:

```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32004,
    "message": "Rate limit exceeded. Maximum 60 requests per minute."
  },
  "id": null
}
```

### Example MCP Request

```bash
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-api-key-here" \
  -d '{
    "jsonrpc": "2.0",
    "method": "createNote",
    "params": {
      "content": "# My First Note\n\nThis is a test note created via MCP.",
      "suggestTags": true
    },
    "id": 1
  }'
```

## Development

### Project Structure

```
tenga/
├── src/main/
│   ├── java/de/christophdick/tenga/      # Backend
│   │   ├── config/                       # Configuration (includes SpaWebConfiguration)
│   │   ├── controller/                   # REST controllers
│   │   ├── dto/                          # Data Transfer Objects
│   │   ├── entity/                       # JPA entities
│   │   ├── exception/                    # Custom exceptions
│   │   ├── mcp/                          # MCP integration
│   │   ├── repository/                   # Spring Data repositories
│   │   ├── security/                     # Security filters & rate limiting
│   │   └── service/                      # Business logic
│   └── resources/
│       ├── static/                       # Frontend build output (generated)
│       └── db/migration/                 # Flyway migrations
└── frontend/                             # Frontend source
    ├── src/
    │   ├── components/                   # Vue components
    │   ├── views/                        # Page views
    │   ├── stores/                       # Pinia stores
    │   ├── services/                     # API client
    │   ├── router/                       # Vue Router
    │   └── types/                        # TypeScript types
    └── vite.config.ts                    # Builds to ../src/main/resources/static
```

**Build Process**: When you run `mvn package`, the frontend-maven-plugin automatically:
1. Installs Node.js and npm (if not present)
2. Runs `npm install` in the frontend directory
3. Runs `npm run build` which outputs to `src/main/resources/static`
4. Spring Boot packages the frontend with the JAR file

### Database Migrations

Flyway migrations are in `src/main/resources/db/migration/`:
- V1: Documents table
- V2: Document versions table
- V3: Tags table
- V4: Document-tags join table
- V5: API keys table
- V6: Full-text search indexes
- V7: Future semantic search preparation

## Configuration

Key configuration options in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tenga
    username: tenga
    password: changeme

  jpa:
    hibernate:
      ddl-auto: validate  # Flyway handles schema

  flyway:
    enabled: true
    baseline-on-migrate: true
```

## Roadmap

### MVP (Current)
- ✅ Document management with versioning
- ✅ Hierarchical tagging
- ✅ Full-text search
- ✅ REST API with OpenAPI docs
- ✅ MCP integration for chat agents
- ✅ API key authentication
- ✅ Rate limiting
- ✅ Web UI (Vue.js 3 with TypeScript)

### Phase 2 (Planned)
- Semantic search with OpenAI embeddings
- Enhanced tag auto-extraction (KeyBERT + YAKE)
- Comprehensive test suite
- Enhanced Web UI (drag-and-drop tag management, advanced diff viewer)

### Phase 3 (Future)
- Multi-user support
- OAuth2 authentication
- File attachments
- Real-time collaboration

## License

MIT License

## Support

For issues and questions, please create an issue in the GitHub repository.
