# Project Context

## Purpose
Tenga is a personal AI-based knowledge base that captures, organizes, and retrieves thoughts, ideas, and information as structured Markdown documents. Users interact through a chat agent (ChatGPT) and an MCP backend service to store knowledge conversationally with automatic tagging, versioning, and semantic search.

## Tech Stack
- **Backend**: Java 23, Spring Boot 3.5.6
- **Database**: PostgreSQL 17.4 (production), H2 (development)
- **ORM**: Spring Data JPA, Hibernate
- **Migration**: Flyway
- **API**: REST + MCP (Model Context Protocol)
- **Frontend**: TBD (Web UI for browsing/managing)
- **Build**: Maven

## Project Conventions

### Code Style
- Java package structure: `de.christophdick.tenga`
- Follow Spring Boot best practices
- Use UTF-8 encoding throughout
- Markdown documents with YAML front-matter

### Architecture Patterns
- RESTful API design
- Repository pattern for data access
- Service layer for business logic
- API-key based authentication (no user accounts in v0.1)
- Single-user focus initially, multi-user extensibility planned

### Testing Strategy
- Unit tests for service layer
- Integration tests for API endpoints
- Repository tests for database operations
- Test coverage for version control and tag hierarchies

### Git Workflow
- Main branch: `main`
- Feature branches for new capabilities
- OpenSpec-driven development for significant changes

## Domain Context
- **Documents**: Markdown-formatted knowledge entries with metadata
- **Tags**: Hierarchical organization (parent-child relationships)
- **Versions**: Up to 10 historical versions per document (latest 10 only)
- **Search**: Both full-text and semantic (AI-powered) search
- **MCP**: Chat agent integration protocol for conversational interaction

## Important Constraints
- No file uploads (text-only storage)
- Maximum 10 versions per document
- Old versions are not searchable
- Response time target: < 500ms for < 10,000 documents
- Single API-key per user (no complex auth flows)
- Errors must never overwrite versions

## External Dependencies
- PostgreSQL database server
- MCP server implementation
- Chat agent (ChatGPT or compatible)
- Future: Embedding service for semantic search
