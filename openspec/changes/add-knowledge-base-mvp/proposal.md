# Proposal: Knowledge Base MVP

## Why
Implement the core Tenga personal knowledge base system as defined in PRD v1.1. The system enables users to capture, organize, and retrieve knowledge through conversational interactions with a chat agent, storing structured Markdown documents with automatic tagging, versioning, and semantic search capabilities.

## What Changes
This proposal adds seven new capabilities to deliver the complete MVP:

- **knowledge-capture**: Markdown document storage with metadata and YAML front-matter
- **tag-management**: Hierarchical tag system with parent-child relationships
- **version-control**: Document versioning system (max 10 versions per document)
- **search**: Full-text and semantic search across documents
- **rest-api**: RESTful API endpoints with API-key authentication
- **mcp-integration**: MCP server implementation for chat agent integration
- **web-ui**: Web interface for browsing, filtering, and managing documents

## Impact
**Affected specs:**
- NEW: `specs/knowledge-capture/spec.md` - Document storage and retrieval
- NEW: `specs/tag-management/spec.md` - Hierarchical tagging system
- NEW: `specs/version-control/spec.md` - Document version history
- NEW: `specs/search/spec.md` - Full-text and semantic search
- NEW: `specs/rest-api/spec.md` - REST API endpoints
- NEW: `specs/mcp-integration/spec.md` - MCP server integration
- NEW: `specs/web-ui/spec.md` - Web interface

**Affected code:**
- Database schema (Flyway migrations)
- JPA entities and repositories
- Service layer implementation
- REST controllers
- MCP server setup
- Web UI components (future phase)

**Dependencies:**
- PostgreSQL database (already configured)
- Spring Boot framework (already configured)
- MCP server library (to be added)
- Frontend framework (to be determined)

**Non-goals for MVP:**
- Multi-user support (single API-key only)
- File uploads (text-only)
- Advanced AI features beyond basic semantic search
- Mobile applications
- Real-time collaboration
