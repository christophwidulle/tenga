# Design Document: Knowledge Base MVP

## Context
Tenga is a greenfield project implementing a personal knowledge base with conversational interaction through chat agents. The system must support document storage, hierarchical tagging, versioning, search, and dual API interfaces (REST + MCP).

**Constraints:**
- Single-user MVP (API-key auth only, no user accounts)
- PostgreSQL database already configured
- Spring Boot 3.5.6 with Java 23
- No file uploads (text-only)
- Performance target: < 500ms for < 10,000 documents

**Stakeholders:**
- End user: Personal knowledge management through chat agent
- Developer: Extensibility for future multi-user and advanced AI features

## Goals / Non-Goals

**Goals:**
- Simple, minimal implementation suitable for MVP
- Clean separation of concerns (entity, repository, service, controller layers)
- Database-agnostic business logic (use JPA abstractions)
- Extensible design for future multi-user support
- Reliable version control that never loses data

**Non-Goals:**
- Multi-tenancy in MVP (single API-key)
- Real-time collaboration
- Advanced AI/ML features beyond basic semantic search
- Mobile-specific optimizations
- Complex authentication flows

## Decisions

### Decision 1: Database Schema Design

**What:** Use relational schema with separate tables for documents, versions, tags, and document-tag associations.

**Why:**
- Hierarchical tags naturally fit parent_id foreign key pattern
- Version history benefits from separate table to avoid bloating main document table
- Many-to-many relationship for document-tags is standard pattern
- PostgreSQL provides good full-text search capabilities (tsvector)

**Schema outline:**
```sql
documents (id, title, content, content_vector, created_at, updated_at, deleted_at, current_version)
document_versions (id, document_id, version_number, title, content, tags_snapshot, created_at, change_summary)
tags (id, name, parent_id, created_at)
document_tags (document_id, tag_id)
api_keys (id, key_hash, created_at, last_used_at) -- for future expansion
```

**Alternatives considered:**
- NoSQL (MongoDB): Rejected because hierarchical queries and version diffing are easier in SQL
- Document-only storage with embedded versions: Rejected because it would bloat document table and complicate queries

### Decision 2: Version Storage Strategy

**What:** Store complete document snapshots for each version (not diffs).

**Why:**
- Simpler implementation and retrieval (no reconstruction needed)
- Guaranteed ability to restore any version
- Easier to implement version comparison (diff on-demand)
- Storage is cheap for text content

**Trade-off:** Higher storage usage, but acceptable for text-only content with 10-version limit.

**Alternatives considered:**
- Delta storage: More complex, harder to guarantee data integrity
- Event sourcing: Overkill for MVP, adds significant complexity

### Decision 3: Tag Auto-Extraction

**What:** Implement hybrid tag extraction with KeyBERT + YAKE and taxonomy linking.

**Why:**
- Hybrid approach provides better accuracy than single method
- Taxonomy linking ensures consistency with existing tag hierarchy
- Can start simple (TF-IDF in MVP) and enhance iteratively
- Users can manually specify tags and system learns from patterns

**Implementation phases:**
1. **Phase 1 (MVP):** Simple TF-IDF keyword extraction
2. **Phase 1.5:** Add KeyBERT-style semantic keyword extraction
3. **Phase 2:** Integrate YAKE for statistical validation
4. **Phase 2:** Implement taxonomy linking to existing tags

**Migration path:** See "Decisions (Resolved)" section below for detailed algorithm specifications.

### Decision 4: Semantic Search Implementation

**What:** Defer full semantic search to Phase 2; implement full-text search in MVP. Use OpenAI embeddings for Phase 2.

**Why:**
- PostgreSQL full-text search (tsvector/tsquery) is built-in and performant
- Embedding generation and vector search require external dependencies
- Full-text search satisfies most MVP use cases
- Can add pgvector extension later without major refactoring

**Migration path (Phase 2):**
- Add `content_vector` column (already in schema)
- Integrate OpenAI text-embedding-3-small via Spring AI
- Add pgvector extension for similarity search
- Background job for generating embeddings on document changes

**Implementation:** See "Decisions (Resolved)" section below for detailed OpenAI integration.

### Decision 5: MCP Server Implementation

**What:** Use Spring AI MCP Server (`spring-ai-starter-mcp-server-webmvc`) with WebMvc transport.

**Why:**
- Keeps everything in single deployment
- Leverages existing Spring security and service layer
- Official Spring integration with annotation-based tool definition
- SSE-based transport works well for chat agents
- No custom protocol implementation needed

**Implementation:** See "Decisions (Resolved)" section below for detailed configuration.

**Alternatives considered:**
- Separate MCP server: Adds deployment complexity
- Custom MCP implementation: Unnecessary with Spring AI available
- WebFlux transport: Not needed for MVP, synchronous mode sufficient

### Decision 6: Frontend Framework

**What:** Use Vue.js 3 with Composition API, Vite, and TypeScript.

**Why:**
- Not blocking backend development
- Simpler learning curve and less boilerplate than React
- Excellent Markdown rendering libraries (vue-markdown-it)
- Component-based architecture fits document-centric UI
- Strong TypeScript support with Pinia for state management

**Implementation:** See "Decisions (Resolved)" section below for detailed stack configuration.

### Decision 7: API-Key Authentication

**What:** Simple Bearer token authentication with API-key stored as hash in database.

**Why:**
- No complex OAuth/JWT infrastructure needed for MVP
- Single user = single API-key
- Easy to implement and test
- Can migrate to proper auth later

**Implementation:**
- Store bcrypt hash of API-key
- Accept key in `Authorization: Bearer <key>` header
- Validate on every request using Spring Security filter

**Security notes:**
- Use HTTPS in production
- Rate limiting per API-key
- Consider API-key rotation in future

### Decision 8: Version Pruning Strategy

**What:** Keep last 10 versions by version_number; delete oldest when limit exceeded.

**Why:**
- Simple FIFO queue behavior
- Deterministic and predictable
- Easy to implement with `DELETE WHERE version_number NOT IN (SELECT ... ORDER BY version_number DESC LIMIT 10)`

**Trade-offs:**
- Users cannot configure retention
- Acceptable for MVP; can add configuration later

## Risks / Trade-offs

### Risk 1: MCP Protocol Maturity
**Risk:** MCP specification may evolve, requiring rework.

**Mitigation:**
- Abstract MCP integration behind service layer
- Version the MCP endpoints
- Design for protocol versioning from start

### Risk 2: Performance with Large Documents
**Risk:** Storing full document snapshots for 10 versions could impact performance with very large documents.

**Mitigation:**
- Set content size limits (e.g., 1MB per document)
- Monitor database size and query performance
- Can optimize with compression if needed

### Risk 3: Semantic Search Expectations
**Risk:** Users may expect sophisticated AI search in MVP.

**Mitigation:**
- Document limitation clearly in UI
- Implement full-text search well to minimize gap
- Prioritize semantic search in Phase 2

### Risk 4: Tag Hierarchy Complexity
**Risk:** Circular references and deep hierarchies could cause issues.

**Mitigation:**
- Validate parent references on tag creation/update
- Limit hierarchy depth (e.g., max 5 levels)
- Use recursive CTEs carefully with depth limits

### Risk 5: Single API-Key Limitation
**Risk:** Users may want multiple devices or share access.

**Mitigation:**
- Design database schema to support multiple keys per user
- Consider "device" or "session" concept in future
- For MVP, users can use same key across devices

## Migration Plan

### Phase 1 (MVP - Current Proposal):
1. Database schema with Flyway migrations
2. JPA entities and repositories
3. Service layer for all business logic
4. REST API with OpenAPI documentation
5. MCP server integration
6. Basic web UI

### Phase 2 (Semantic Search):
1. Add pgvector extension
2. Integrate embedding API
3. Add background job for generating embeddings
4. Implement vector similarity search
5. Update search endpoints

### Phase 3 (Multi-User):
1. Add users table
2. Add user_id foreign keys to documents, tags
3. Implement proper authentication (OAuth2/JWT)
4. Multi-key support per user
5. Sharing and permissions

### Rollback Plan:
- Flyway supports migration rollback
- All changes are additive (no breaking schema changes in MVP)
- API versioning allows graceful deprecation

## Decisions (Resolved)

### 1. MCP Library: Spring AI MCP Server

**Decision:** Use `spring-ai-starter-mcp-server-webmvc` from Spring AI project.

**Implementation:**
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
</dependency>
```

**Configuration:**
```yaml
spring:
  ai:
    mcp:
      server:
        enabled: true
        name: "tenga-knowledge-base"
        version: "0.1.0"
        type: SYNC
        transport: WEBMVC
        sse-message-endpoint: /mcp/message
```

**Rationale:**
- Official Spring integration with mature ecosystem
- Leverages existing Spring Boot infrastructure
- Supports annotation-based tool definition with `@McpTool`
- Built-in SSE transport for web-based communication
- Well-documented with examples from Spring AI project

**Integration approach:**
- Annotate service methods with `@McpTool` for automatic exposure
- Use `SyncMcpToolCallbackProvider` for integration with ChatModel
- Implement methods: `createNote`, `updateNote`, `getNotes`, `searchNotes`

### 2. Tag Extraction Algorithm: Hybrid (KeyBERT + YAKE) with Taxonomie-Linking

**Decision:** Implement hybrid keyword extraction combining KeyBERT-style and YAKE algorithms with taxonomy linking.

**Algorithm components:**
- **KeyBERT-style:** Use embeddings to extract keywords that are semantically relevant to the document
- **YAKE:** Yet Another Keyword Extractor for statistical keyword extraction
- **Taxonomie-Linking:** Map extracted keywords to existing tag hierarchy

**Implementation plan:**
1. Start with simple TF-IDF for MVP Phase 1
2. Add KeyBERT integration in Phase 1.5 (uses sentence transformers)
3. Combine with YAKE for statistical validation
4. Link to existing taxonomy to suggest parent-child relationships

**Dependencies:**
- Apache Lucene for TF-IDF
- KeyBERT Java wrapper or Python microservice
- YAKE library (via Python or Java port)

**Rationale:**
- Hybrid approach provides better accuracy than single method
- Taxonomy linking ensures consistency with existing tags
- Gradual implementation allows MVP to proceed without blocking

### 3. Markdown Parser: flexmark-java

**Decision:** Use flexmark-java for Markdown parsing and YAML front-matter.

**Implementation:**
```xml
<dependency>
    <groupId>com.vladsch.flexmark</groupId>
    <artifactId>flexmark-all</artifactId>
    <version>0.64.8</version>
</dependency>
```

**Rationale:**
- More comprehensive than commonmark-java
- Built-in YAML front-matter extension
- Better table support and extended syntax
- Active maintenance and good documentation
- Supports custom extensions for future needs

### 4. Frontend Framework: Vue.js

**Decision:** Use Vue.js 3 with Composition API and TypeScript.

**Stack:**
- Vue.js 3 with Composition API
- Vite for build tooling
- Pinia for state management
- Vue Router for routing
- TypeScript for type safety

**Rationale:**
- Simpler learning curve than React
- Excellent documentation and ecosystem
- Good Markdown rendering libraries (vue-markdown-it)
- Less boilerplate than React
- Strong TypeScript support

### 5. Embedding Service: OpenAI

**Decision:** Use OpenAI's text-embedding-3-small model for Phase 2 semantic search.

**Implementation (Phase 2):**
```java
// Using Spring AI
@Service
public class EmbeddingService {
    private final OpenAiEmbeddingModel embeddingModel;

    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(text);
    }
}
```

**Configuration:**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      embedding:
        options:
          model: text-embedding-3-small
```

**Rationale:**
- Spring AI provides native OpenAI integration
- text-embedding-3-small is cost-effective (512 dimensions)
- High quality embeddings with good performance
- Easy to integrate with existing Spring infrastructure
- Can switch to local models (sentence-transformers) later if needed

### 6. Rate Limiting Strategy: 100 requests/minute per API-key

**Decision:** Implement rate limiting at 100 requests per minute per API-key, with burst allowance.

**Implementation:**
```java
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.of("api-rate-limiter",
            RateLimiterConfig.custom()
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofMillis(500))
                .build());
    }
}
```

**Limits:**
- **Global:** 100 requests/minute per API-key
- **Burst:** Allow up to 20 requests in 5 seconds
- **Search endpoints:** 20 requests/minute (more expensive)
- **MCP methods:** Share same quota as REST API

**Headers returned:**
- `X-RateLimit-Limit: 100`
- `X-RateLimit-Remaining: 75`
- `X-RateLimit-Reset: 1234567890`
- `Retry-After: 60` (when exceeded)

**Rationale:**
- 100 req/min sufficient for personal use and chat agents
- Prevents abuse and resource exhaustion
- Per-key limiting allows future multi-user expansion
- Burst allowance handles legitimate usage spikes

### 7. Document Size Limit: 1MB per document

**Decision:** Enforce maximum document size of 1MB (1,048,576 bytes) for content.

**Implementation:**
```java
@Entity
public class Document {
    @Column(columnDefinition = "TEXT")
    @Size(max = 1048576, message = "Document content exceeds 1MB limit")
    private String content;
}
```

**Validation:**
- Validate on document creation and update
- Return 400 Bad Request with clear error message
- Include size information in error response

**Rationale:**
- 1MB sufficient for extensive text documents (~500,000 words)
- Prevents database bloat from accidental large uploads
- Reasonable limit for version storage (10MB max per document with 10 versions)
- Can be increased in future if needed

### 8. Soft Delete Behavior: Keep indefinitely

**Decision:** Keep soft-deleted documents indefinitely (no automatic purging).

**Implementation:**
```java
@Entity
public class Document {
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
```

**Behavior:**
- Soft delete sets `deleted_at` timestamp
- Excluded from normal queries via `WHERE deleted_at IS NULL`
- Version history preserved for deleted documents
- Can be restored by setting `deleted_at = NULL`
- Admin can manually purge if needed (future feature)

**Rationale:**
- Prevents accidental data loss
- Allows recovery from mistakes
- Storage cost minimal for text-only content
- Audit trail maintained
- Future feature can add scheduled purging if desired

## Implementation Notes

### Testing Strategy:
- Unit tests for service layer (Mockito for dependencies)
- Integration tests for repositories (Spring Boot Test with Testcontainers PostgreSQL)
- API tests for REST endpoints (MockMvc or RestAssured)
- MCP integration tests (custom test client)

### Error Handling:
- Global exception handler with `@ControllerAdvice`
- Consistent error response format
- Validation with Bean Validation (JSR-303)
- Transaction management for version creation

### Performance Considerations:
- Index on `documents.content` with `GIN(to_tsvector(content))`
- Index on `tags.parent_id` for hierarchy queries
- Pagination for all list endpoints
- Lazy loading for document content in list views

### Security:
- SQL injection prevention through JPA/Hibernate
- XSS prevention in API responses (Spring escaping)
- CSRF protection for web UI
- Rate limiting with Spring annotations or Bucket4j
