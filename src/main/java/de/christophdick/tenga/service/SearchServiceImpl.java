package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.SearchResultDTO;
import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.Tag;
import de.christophdick.tenga.exception.ResourceNotFoundException;
import de.christophdick.tenga.repository.DocumentRepository;
import de.christophdick.tenga.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final DocumentRepository documentRepository;
    private final TagRepository tagRepository;
    private final EntityManager entityManager;

    public SearchServiceImpl(DocumentRepository documentRepository,
                            TagRepository tagRepository,
                            EntityManager entityManager) {
        this.documentRepository = documentRepository;
        this.tagRepository = tagRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Page<SearchResultDTO> fullTextSearch(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return Page.empty(pageable);
        }

        // Use PostgreSQL full-text search
        String sql = """
            SELECT d.id, d.title, d.content,
                   ts_rank(d.content_search_vector, to_tsquery('english', :query)) as rank
            FROM documents d
            WHERE d.deleted_at IS NULL
              AND d.content_search_vector @@ to_tsquery('english', :query)
            ORDER BY rank DESC
            LIMIT :limit OFFSET :offset
            """;

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter("query", sanitizeSearchQuery(query));
        nativeQuery.setParameter("limit", pageable.getPageSize());
        nativeQuery.setParameter("offset", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Object[]> results = nativeQuery.getResultList();

        List<SearchResultDTO> searchResults = results.stream()
            .map(row -> {
                Long id = ((Number) row[0]).longValue();
                String title = (String) row[1];
                String content = (String) row[2];
                Float rank = ((Number) row[3]).floatValue();

                String snippet = createSnippet(content, query);

                return new SearchResultDTO(id, title, snippet, rank);
            })
            .collect(Collectors.toList());

        // Count total results
        String countSql = """
            SELECT COUNT(*)
            FROM documents d
            WHERE d.deleted_at IS NULL
              AND d.content_search_vector @@ to_tsquery('english', :query)
            """;

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("query", sanitizeSearchQuery(query));
        long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(searchResults, pageable, total);
    }

    @Override
    public Page<DocumentDTO> searchByTags(Set<String> tagNames, Pageable pageable) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Page.empty(pageable);
        }

        // Find tags
        List<Tag> tags = tagNames.stream()
            .map(name -> tagRepository.findByNameAndParentIsNull(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", name)))
            .collect(Collectors.toList());

        // Search documents with all specified tags
        return documentRepository.findByAllTags(tags, (long) tags.size(), pageable)
            .map(this::convertToDocumentDTO);
    }

    @Override
    public Page<DocumentDTO> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must be provided");
        }

        return documentRepository.findByUpdatedAtBetween(startDate, endDate, pageable)
            .map(this::convertToDocumentDTO);
    }

    @Override
    public Page<SearchResultDTO> combinedSearch(String query, Set<String> tags,
                                                LocalDateTime startDate, LocalDateTime endDate,
                                                Pageable pageable) {
        // For MVP, we'll do a simple implementation
        // Start with full-text search if query is provided
        if (query != null && !query.isBlank()) {
            return fullTextSearch(query, pageable);
        }

        // Otherwise search by tags
        if (tags != null && !tags.isEmpty()) {
            Page<DocumentDTO> documents = searchByTags(tags, pageable);
            return documents.map(doc -> new SearchResultDTO(doc.getId(), doc.getTitle(),
                createSnippet(doc.getContent(), ""), 1.0f));
        }

        // Otherwise search by date range
        if (startDate != null && endDate != null) {
            Page<DocumentDTO> documents = searchByDateRange(startDate, endDate, pageable);
            return documents.map(doc -> new SearchResultDTO(doc.getId(), doc.getTitle(),
                createSnippet(doc.getContent(), ""), 1.0f));
        }

        return Page.empty(pageable);
    }

    @Override
    public Page<SearchResultDTO> semanticSearch(String query, Pageable pageable) {
        // Placeholder for Phase 2 - semantic search with embeddings
        throw new UnsupportedOperationException("Semantic search is not yet implemented (planned for Phase 2)");
    }

    // Helper methods

    private String sanitizeSearchQuery(String query) {
        // Replace spaces with AND operator for tsquery
        return query.trim().replaceAll("\\s+", " & ");
    }

    private String createSnippet(String content, String query) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        int snippetLength = 200;

        // Try to find query term in content
        if (query != null && !query.isEmpty()) {
            String lowerContent = content.toLowerCase();
            String lowerQuery = query.toLowerCase().split("\\s+")[0];

            int position = lowerContent.indexOf(lowerQuery);
            if (position != -1) {
                int start = Math.max(0, position - 100);
                int end = Math.min(content.length(), position + 100);

                String snippet = content.substring(start, end);
                if (start > 0) snippet = "..." + snippet;
                if (end < content.length()) snippet = snippet + "...";

                return snippet;
            }
        }

        // Default: return first N characters
        if (content.length() <= snippetLength) {
            return content;
        }

        return content.substring(0, snippetLength) + "...";
    }

    private DocumentDTO convertToDocumentDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setContent(document.getContent());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setDeletedAt(document.getDeletedAt());
        dto.setCurrentVersion(document.getCurrentVersion());

        if (document.getTags() != null && !document.getTags().isEmpty()) {
            Set<String> tagNames = document.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
            dto.setTags(tagNames);
        }

        return dto;
    }
}
