package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.SearchResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SearchService {

    /**
     * Full-text search across documents
     */
    Page<SearchResultDTO> fullTextSearch(String query, Pageable pageable);

    /**
     * Search documents by tags
     */
    Page<DocumentDTO> searchByTags(Set<String> tags, Pageable pageable);

    /**
     * Search documents by date range
     */
    Page<DocumentDTO> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Combined search (text + tags + date)
     */
    Page<SearchResultDTO> combinedSearch(String query, Set<String> tags,
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Semantic search (placeholder for Phase 2)
     */
    Page<SearchResultDTO> semanticSearch(String query, Pageable pageable);
}
