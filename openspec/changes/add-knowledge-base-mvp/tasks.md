# Implementation Tasks: Knowledge Base MVP

## 1. Database Schema & Migrations
- [ ] 1.1 Create Flyway migration for documents table (id, title, content, created_at, updated_at, deleted_at, current_version)
- [ ] 1.2 Create Flyway migration for document_versions table (id, document_id, version_number, title, content, tags_snapshot, created_at, change_summary)
- [ ] 1.3 Create Flyway migration for tags table (id, name, parent_id, created_at)
- [ ] 1.4 Create Flyway migration for document_tags join table (document_id, tag_id)
- [ ] 1.5 Create Flyway migration for api_keys table (id, key_hash, created_at, last_used_at)
- [ ] 1.6 Add indexes for full-text search (GIN index on documents.content with to_tsvector)
- [ ] 1.7 Add indexes for tag hierarchy (parent_id) and common queries
- [ ] 1.8 Add foreign key constraints and cascade rules
- [ ] 1.9 Test migrations with local PostgreSQL database
- [ ] 1.10 Test migration rollback scenarios

## 2. JPA Entities & Repositories
- [ ] 2.1 Create Document entity with JPA annotations
- [ ] 2.2 Create DocumentVersion entity with JPA annotations
- [ ] 2.3 Create Tag entity with parent-child self-reference
- [ ] 2.4 Create ApiKey entity
- [ ] 2.5 Create DocumentRepository extending JpaRepository with custom queries
- [ ] 2.6 Create DocumentVersionRepository with versioning queries
- [ ] 2.7 Create TagRepository with hierarchy queries (using @Query with recursive CTEs)
- [ ] 2.8 Create ApiKeyRepository
- [ ] 2.9 Add repository unit tests with Testcontainers PostgreSQL
- [ ] 2.10 Test repository performance with sample data (< 500ms for 10k documents)

## 3. Service Layer - Knowledge Capture
- [ ] 3.1 Create DocumentService interface
- [ ] 3.2 Implement createDocument method with validation
- [ ] 3.3 Implement getDocumentById method
- [ ] 3.4 Implement listDocuments with pagination
- [ ] 3.5 Implement updateDocument with version creation
- [ ] 3.6 Implement softDeleteDocument method
- [ ] 3.7 Implement restoreDocument method
- [ ] 3.8 Add transaction management for document operations
- [ ] 3.9 Add validation for document size limits
- [ ] 3.10 Write unit tests for DocumentService with Mockito

## 4. Service Layer - Tag Management
- [ ] 4.1 Create TagService interface
- [ ] 4.2 Implement createTag with parent validation
- [ ] 4.3 Implement getTagById and listTags methods
- [ ] 4.4 Implement updateTag (rename) with document association updates
- [ ] 4.5 Implement deleteTag with conflict checking
- [ ] 4.6 Implement tag hierarchy validation (prevent circular references)
- [ ] 4.7 Implement getTagHierarchy method (tree structure)
- [ ] 4.8 Implement associateTagWithDocument and removeTagFromDocument
- [ ] 4.9 Implement basic tag extraction algorithm (keyword frequency or TF-IDF)
- [ ] 4.10 Write unit tests for TagService

## 5. Service Layer - Version Control
- [ ] 5.1 Create VersionService interface
- [ ] 5.2 Implement createVersion method (called by DocumentService on updates)
- [ ] 5.3 Implement getVersionHistory method (max 10 versions)
- [ ] 5.4 Implement getSpecificVersion method
- [ ] 5.5 Implement pruneOldVersions method (keep latest 10)
- [ ] 5.6 Implement restoreVersion method (create new version from old)
- [ ] 5.7 Implement compareVersions method (generate diff)
- [ ] 5.8 Add transaction rollback on version creation failure
- [ ] 5.9 Add audit trail metadata to versions
- [ ] 5.10 Write unit tests for VersionService

## 6. Service Layer - Search
- [ ] 6.1 Create SearchService interface
- [ ] 6.2 Implement full-text search using PostgreSQL tsvector/tsquery
- [ ] 6.3 Implement search result ranking and snippet generation
- [ ] 6.4 Implement tag-based filtering with hierarchy support
- [ ] 6.5 Implement combined text + tag search
- [ ] 6.6 Implement date range filtering
- [ ] 6.7 Implement search query validation and sanitization
- [ ] 6.8 Implement search result pagination
- [ ] 6.9 Add search query logging for analytics
- [ ] 6.10 Write unit tests for SearchService
- [ ] 6.11 Add stub for future semantic search (interface only)

## 7. REST API - Controllers
- [ ] 7.1 Create DocumentController with CRUD endpoints
- [ ] 7.2 Add POST /api/v1/documents (create)
- [ ] 7.3 Add GET /api/v1/documents/{id} (retrieve)
- [ ] 7.4 Add GET /api/v1/documents (list with pagination)
- [ ] 7.5 Add PUT /api/v1/documents/{id} (update)
- [ ] 7.6 Add DELETE /api/v1/documents/{id} (soft delete)
- [ ] 7.7 Create TagController with CRUD endpoints
- [ ] 7.8 Add tag hierarchy endpoints (GET /api/v1/tags with tree structure)
- [ ] 7.9 Create VersionController with version history endpoints
- [ ] 7.10 Add GET /api/v1/documents/{id}/versions
- [ ] 7.11 Add GET /api/v1/documents/{id}/versions/{versionNumber}
- [ ] 7.12 Add POST /api/v1/documents/{id}/versions/{versionNumber}/restore
- [ ] 7.13 Add GET /api/v1/documents/{id}/versions/compare
- [ ] 7.14 Create SearchController with search endpoints
- [ ] 7.15 Add GET /api/v1/search (full-text)
- [ ] 7.16 Add POST /api/v1/search/semantic (stub for future)

## 8. REST API - Security & Error Handling
- [ ] 8.1 Implement API-key authentication filter with Spring Security
- [ ] 8.2 Add API-key validation against hashed keys in database
- [ ] 8.3 Implement global exception handler with @ControllerAdvice
- [ ] 8.4 Create consistent error response DTOs
- [ ] 8.5 Add validation annotations to DTOs (JSR-303)
- [ ] 8.6 Implement rate limiting (Spring annotations or Bucket4j)
- [ ] 8.7 Add CORS configuration
- [ ] 8.8 Add request/response logging
- [ ] 8.9 Write integration tests for authentication
- [ ] 8.10 Write integration tests for error scenarios

## 9. REST API - Documentation & Testing
- [ ] 9.1 Add SpringDoc OpenAPI dependency
- [ ] 9.2 Configure OpenAPI documentation
- [ ] 9.3 Add API documentation annotations to controllers
- [ ] 9.4 Generate OpenAPI spec file
- [ ] 9.5 Write integration tests for all document endpoints (MockMvc or RestAssured)
- [ ] 9.6 Write integration tests for tag endpoints
- [ ] 9.7 Write integration tests for version endpoints
- [ ] 9.8 Write integration tests for search endpoints
- [ ] 9.9 Test pagination and filtering scenarios
- [ ] 9.10 Test performance benchmarks (< 500ms for < 10k documents)

## 10. MCP Integration
- [ ] 10.1 Research and select Java MCP library or implement custom protocol
- [ ] 10.2 Add MCP dependencies to pom.xml
- [ ] 10.3 Create MCP server configuration
- [ ] 10.4 Implement createNote MCP method (delegates to DocumentService)
- [ ] 10.5 Implement updateNote MCP method with append support
- [ ] 10.6 Implement getNotes MCP method with filtering
- [ ] 10.7 Implement searchNotes MCP method
- [ ] 10.8 Add MCP authentication using same API-key mechanism
- [ ] 10.9 Implement MCP manifest/discovery endpoint
- [ ] 10.10 Add error handling for MCP methods
- [ ] 10.11 Implement batch operations support
- [ ] 10.12 Add MCP rate limiting
- [ ] 10.13 Write MCP integration tests with test client
- [ ] 10.14 Document MCP API usage

## 11. Web UI - Setup & Infrastructure
- [ ] 11.1 Choose frontend framework (React/Vue.js/Svelte)
- [ ] 11.2 Initialize frontend project with Vite
- [ ] 11.3 Configure TypeScript
- [ ] 11.4 Add Markdown rendering library (react-markdown or marked)
- [ ] 11.5 Add diff library for version comparison
- [ ] 11.6 Add HTTP client (axios or fetch wrapper)
- [ ] 11.7 Configure API base URL and environments
- [ ] 11.8 Set up routing (React Router or Vue Router)
- [ ] 11.9 Set up state management (Context API, Zustand, or Pinia)
- [ ] 11.10 Configure build and deployment

## 12. Web UI - Authentication & Layout
- [ ] 12.1 Create API-key input component
- [ ] 12.2 Implement API-key validation
- [ ] 12.3 Store API-key in LocalStorage securely
- [ ] 12.4 Create authenticated routing guard
- [ ] 12.5 Create main layout with navigation
- [ ] 12.6 Create responsive sidebar for tags
- [ ] 12.7 Add logout functionality
- [ ] 12.8 Test authentication flow

## 13. Web UI - Document Views
- [ ] 13.1 Create document list component
- [ ] 13.2 Implement list view with title, tags, dates
- [ ] 13.3 Implement card view toggle
- [ ] 13.4 Add pagination controls
- [ ] 13.5 Create document detail component
- [ ] 13.6 Implement Markdown rendering for document content
- [ ] 13.7 Add navigation between documents
- [ ] 13.8 Create loading states for async operations
- [ ] 13.9 Add error handling and retry logic
- [ ] 13.10 Test document views on desktop and mobile

## 14. Web UI - Document Editing
- [ ] 14.1 Create document editor component
- [ ] 14.2 Add Markdown syntax highlighting
- [ ] 14.3 Add live preview pane
- [ ] 14.4 Implement save document functionality
- [ ] 14.5 Add tag selector component
- [ ] 14.6 Implement auto-save or draft functionality
- [ ] 14.7 Create new document form
- [ ] 14.8 Add validation feedback
- [ ] 14.9 Implement cancel/discard changes
- [ ] 14.10 Test editor functionality

## 15. Web UI - Tags & Search
- [ ] 15.1 Create tag tree component
- [ ] 15.2 Implement expand/collapse tag hierarchy
- [ ] 15.3 Add tag filtering with multi-select
- [ ] 15.4 Show document count per tag
- [ ] 15.5 Create search input component
- [ ] 15.6 Implement search result display with snippets
- [ ] 15.7 Add search filters (tags, dates)
- [ ] 15.8 Highlight search terms in results
- [ ] 15.9 Add semantic search toggle (UI only, disabled in MVP)
- [ ] 15.10 Test search and filtering combinations

## 16. Web UI - Version History
- [ ] 16.1 Create version history component
- [ ] 16.2 Display version list with timestamps
- [ ] 16.3 Implement version detail view
- [ ] 16.4 Create version comparison component with diff display
- [ ] 16.5 Implement restore version with confirmation
- [ ] 16.6 Add version navigation
- [ ] 16.7 Test version history workflows

## 17. Web UI - Polish & Accessibility
- [ ] 17.1 Add loading spinners and skeletons
- [ ] 17.2 Implement error boundaries
- [ ] 17.3 Add toast notifications for actions
- [ ] 17.4 Ensure keyboard navigation
- [ ] 17.5 Add ARIA labels for accessibility
- [ ] 17.6 Test with screen reader
- [ ] 17.7 Add dark mode support (optional)
- [ ] 17.8 Optimize bundle size
- [ ] 17.9 Add basic analytics or usage tracking
- [ ] 17.10 Test on multiple browsers

## 18. Deployment & Documentation
- [ ] 18.1 Create Docker Compose for local development
- [ ] 18.2 Create Dockerfile for Spring Boot application
- [ ] 18.3 Create Dockerfile for frontend (if separate)
- [ ] 18.4 Configure production database connection
- [ ] 18.5 Add environment variable configuration
- [ ] 18.6 Set up logging configuration
- [ ] 18.7 Write README with setup instructions
- [ ] 18.8 Document API-key generation process
- [ ] 18.9 Create user guide for web interface
- [ ] 18.10 Document MCP integration for chat agents

## 19. Testing & Validation
- [ ] 19.1 Run all unit tests and achieve > 80% coverage
- [ ] 19.2 Run all integration tests
- [ ] 19.3 Perform end-to-end testing of complete workflows
- [ ] 19.4 Load test with 10,000 documents (verify < 500ms response)
- [ ] 19.5 Test version pruning with > 10 updates
- [ ] 19.6 Test tag hierarchy with deep nesting
- [ ] 19.7 Verify soft delete and restore functionality
- [ ] 19.8 Test error scenarios and rollback
- [ ] 19.9 Security audit (SQL injection, XSS, CSRF)
- [ ] 19.10 Validate OpenAPI spec completeness

## 20. Final Cleanup & Release
- [ ] 20.1 Remove debug logging and console statements
- [ ] 20.2 Update version numbers to 0.1.0
- [ ] 20.3 Create CHANGELOG documenting features
- [ ] 20.4 Tag release in git
- [ ] 20.5 Deploy to production environment
- [ ] 20.6 Verify production deployment
- [ ] 20.7 Create backup and restore procedures
- [ ] 20.8 Document known limitations
- [ ] 20.9 Create issue templates for future enhancements
- [ ] 20.10 Celebrate MVP completion!

---

## Dependencies Between Tasks

**Critical Path:**
1 → 2 → 3 → 7 (Database → Entities → Services → API)

**Parallel Tracks:**
- Services (3-6) can be developed in parallel after entities (2)
- REST API (7-9) depends on services (3-6)
- MCP (10) depends on services (3-6) but independent of REST API (7-9)
- Web UI (11-17) depends on REST API (7-9) being complete

**Recommended Order:**
1. Complete database and entities (1-2)
2. Implement core services in parallel (3-6)
3. Build REST API (7-9)
4. Implement MCP integration (10)
5. Build Web UI (11-17)
6. Deploy and test (18-20)
