package de.christophdick.tenga.mcp.service;

import de.christophdick.tenga.dto.*;
import de.christophdick.tenga.mcp.dto.*;
import de.christophdick.tenga.service.DocumentService;
import de.christophdick.tenga.service.SearchService;
import de.christophdick.tenga.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;

@Service
@Transactional
public class McpServiceImpl implements McpService {

    private static final Logger logger = LoggerFactory.getLogger(McpServiceImpl.class);
    private static final int DEFAULT_TITLE_LENGTH = 50;
    private static final int DEFAULT_CONTENT_PREVIEW_LENGTH = 200;

    private final DocumentService documentService;
    private final TagService tagService;
    private final SearchService searchService;

    public McpServiceImpl(DocumentService documentService, TagService tagService, SearchService searchService) {
        this.documentService = documentService;
        this.tagService = tagService;
        this.searchService = searchService;
    }

    @Override
    public DocumentDTO createNote(CreateNoteParams params) {
        logger.info("MCP: Creating note with content length: {}", params.getContent().length());

        // Extract title if not provided
        String title = params.getTitle();
        if (title == null || title.isBlank()) {
            title = extractTitle(params.getContent());
        }

        // Suggest tags if requested and none provided
        List<String> tags = params.getTags();
        if ((tags == null || tags.isEmpty()) && params.isSuggestTags()) {
            tags = suggestTags(params.getContent());
            logger.info("MCP: Suggested tags: {}", tags);
        }

        CreateDocumentRequest request = new CreateDocumentRequest();
        request.setTitle(title);
        request.setContent(params.getContent());
        if (tags != null && !tags.isEmpty()) {
            request.setTags(new HashSet<>(tags));
        }

        return documentService.createDocument(request);
    }

    @Override
    public DocumentDTO updateNote(UpdateNoteParams params) {
        logger.info("MCP: Updating note {} (append: {})", params.getDocumentId(), params.isAppend());

        UpdateDocumentRequest request = new UpdateDocumentRequest();

        if (params.isAppend() && params.getContent() != null) {
            // Append mode: fetch current content and append new content
            DocumentDTO current = documentService.getDocumentById(params.getDocumentId());
            String appendedContent = current.getContent() + "\n\n" + params.getContent();
            request.setContent(appendedContent);
        } else {
            request.setContent(params.getContent());
        }

        request.setTitle(params.getTitle());
        if (params.getTags() != null && !params.getTags().isEmpty()) {
            request.setTags(new HashSet<>(params.getTags()));
        }
        request.setChangeSummary(params.getChangeSummary());

        return documentService.updateDocument(params.getDocumentId(), request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDTO> getNotes(GetNotesParams params) {
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());

        Page<DocumentDTO> documents;

        if (params.getDocumentId() != null) {
            // Single document by ID
            DocumentDTO doc = documentService.getDocumentById(params.getDocumentId());
            List<DocumentDTO> singleDocList = List.of(doc);
            documents = new org.springframework.data.domain.PageImpl<>(singleDocList, pageable, 1);
        } else if (params.getTags() != null && !params.getTags().isEmpty()) {
            // Filter by tags using search service
            Set<String> tagSet = new HashSet<>(params.getTags());
            documents = searchService.searchByTags(tagSet, pageable);
        } else {
            // All documents
            documents = documentService.listDocuments(pageable);
        }

        // Truncate content if full content not requested
        if (!params.isIncludeFullContent()) {
            documents = documents.map(doc -> {
                DocumentDTO truncated = new DocumentDTO();
                truncated.setId(doc.getId());
                truncated.setTitle(doc.getTitle());
                truncated.setContent(truncateContent(doc.getContent(), DEFAULT_CONTENT_PREVIEW_LENGTH));
                truncated.setTags(doc.getTags());
                truncated.setCreatedAt(doc.getCreatedAt());
                truncated.setUpdatedAt(doc.getUpdatedAt());
                truncated.setCurrentVersion(doc.getCurrentVersion());
                return truncated;
            });
        }

        return documents;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SearchResultDTO> searchNotes(SearchNotesParams params) {
        if (params.isSemantic()) {
            logger.warn("MCP: Semantic search requested but not implemented. Falling back to full-text search.");
            // TODO: Implement semantic search in future version
        }

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());

        if (params.getTags() != null && !params.getTags().isEmpty()) {
            Set<String> tagSet = new HashSet<>(params.getTags());
            return searchService.combinedSearch(params.getQuery(), tagSet, null, null, pageable);
        } else {
            return searchService.fullTextSearch(params.getQuery(), pageable);
        }
    }

    @Override
    public List<DocumentDTO> batchCreateNotes(BatchCreateNotesParams params) {
        logger.info("MCP: Batch creating {} notes", params.getNotes().size());

        List<DocumentDTO> createdDocuments = new ArrayList<>();

        for (CreateNoteParams noteParams : params.getNotes()) {
            try {
                DocumentDTO created = createNote(noteParams);
                createdDocuments.add(created);
            } catch (Exception e) {
                logger.error("MCP: Failed to create note in batch. Rolling back transaction.", e);
                throw new RuntimeException("Batch creation failed: " + e.getMessage(), e);
            }
        }

        return createdDocuments;
    }

    @Override
    public String extractTitle(String content) {
        if (content == null || content.isBlank()) {
            return "Untitled Note";
        }

        // Try to extract first line (Markdown header or first sentence)
        String[] lines = content.split("\n", 2);
        String firstLine = lines[0].trim();

        // Remove Markdown header markers
        firstLine = firstLine.replaceFirst("^#{1,6}\\s*", "");

        // Truncate if too long
        if (firstLine.length() > DEFAULT_TITLE_LENGTH) {
            firstLine = firstLine.substring(0, DEFAULT_TITLE_LENGTH) + "...";
        }

        return firstLine.isEmpty() ? "Untitled Note" : firstLine;
    }

    @Override
    public List<String> suggestTags(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }

        // Basic keyword extraction: find common meaningful words
        // This is a simple implementation; in production, consider TF-IDF or NLP
        String normalized = content.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", " ")
            .replaceAll("\\s+", " ");

        String[] words = normalized.split(" ");

        // Common stop words to exclude
        Set<String> stopWords = Set.of(
            "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
            "of", "with", "by", "from", "as", "is", "was", "are", "were", "be",
            "been", "being", "have", "has", "had", "do", "does", "did", "will",
            "would", "should", "could", "may", "might", "can", "this", "that",
            "these", "those", "i", "you", "he", "she", "it", "we", "they"
        );

        // Count word frequency
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String word : words) {
            if (word.length() > 3 && !stopWords.contains(word)) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }

        // Return top 3-5 most frequent words as suggested tags
        return wordFrequency.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }

        // Truncate at word boundary
        String truncated = content.substring(0, maxLength);
        int lastSpace = truncated.lastIndexOf(' ');

        if (lastSpace > maxLength / 2) {
            truncated = truncated.substring(0, lastSpace);
        }

        return truncated + "...";
    }
}
