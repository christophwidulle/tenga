package de.christophdick.tenga.mcp.service;

import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.SearchResultDTO;
import de.christophdick.tenga.mcp.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface McpService {

    /**
     * Create a new note through MCP interface.
     * Extracts title from content if not provided.
     * Suggests tags if requested.
     */
    DocumentDTO createNote(CreateNoteParams params);

    /**
     * Update an existing note through MCP interface.
     * Supports append mode and version creation.
     */
    DocumentDTO updateNote(UpdateNoteParams params);

    /**
     * Get notes with optional filtering by ID or tags.
     * Truncates content if includeFullContent is false.
     */
    Page<DocumentDTO> getNotes(GetNotesParams params);

    /**
     * Search notes using full-text search.
     * Semantic search is a stub for future implementation.
     */
    Page<SearchResultDTO> searchNotes(SearchNotesParams params);

    /**
     * Batch create multiple notes in a single transaction.
     */
    List<DocumentDTO> batchCreateNotes(BatchCreateNotesParams params);

    /**
     * Extract title from content (first line or first N characters).
     */
    String extractTitle(String content);

    /**
     * Suggest tags based on content analysis (basic keyword extraction).
     */
    List<String> suggestTags(String content);

    /**
     * Truncate content for list view (preserve first N characters).
     */
    String truncateContent(String content, int maxLength);
}
