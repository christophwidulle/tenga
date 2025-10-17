package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.CreateDocumentRequest;
import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.UpdateDocumentRequest;
import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.Tag;
import de.christophdick.tenga.exception.DocumentSizeLimitException;
import de.christophdick.tenga.exception.ResourceNotFoundException;
import de.christophdick.tenga.repository.DocumentRepository;
import de.christophdick.tenga.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final int MAX_CONTENT_SIZE = 1048576; // 1MB

    private final DocumentRepository documentRepository;
    private final TagRepository tagRepository;
    private final VersionService versionService;

    public DocumentServiceImpl(DocumentRepository documentRepository,
                              TagRepository tagRepository,
                              VersionService versionService) {
        this.documentRepository = documentRepository;
        this.tagRepository = tagRepository;
        this.versionService = versionService;
    }

    @Override
    public DocumentDTO createDocument(CreateDocumentRequest request) {
        validateContentSize(request.getContent());

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());

        // Process tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = processTags(request.getTags());
            document.setTags(tags);
        }

        // Save document
        document = documentRepository.save(document);

        // Create initial version
        versionService.createVersion(document, "Initial version");

        return convertToDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDTO getDocumentById(Long id) {
        Document document = documentRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        return convertToDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDTO> listDocuments(Pageable pageable) {
        return documentRepository.findAllActive(pageable)
            .map(this::convertToDTO);
    }

    @Override
    public DocumentDTO updateDocument(Long id, UpdateDocumentRequest request) {
        Document document = documentRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        boolean contentChanged = false;

        // Update title if provided
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            document.setTitle(request.getTitle());
            contentChanged = true;
        }

        // Update content if provided
        if (request.getContent() != null && !request.getContent().isBlank()) {
            validateContentSize(request.getContent());
            document.setContent(request.getContent());
            contentChanged = true;
        }

        // Update tags if provided
        if (request.getTags() != null) {
            // Clear existing tags
            document.getTags().clear();
            // Add new tags
            Set<Tag> tags = processTags(request.getTags());
            document.setTags(tags);
            contentChanged = true;
        }

        // Save document
        document = documentRepository.save(document);

        // Create new version if content changed
        if (contentChanged) {
            document.setCurrentVersion(document.getCurrentVersion() + 1);
            document = documentRepository.save(document);
            versionService.createVersion(document, request.getChangeSummary());
        }

        return convertToDTO(document);
    }

    @Override
    public void softDeleteDocument(Long id) {
        Document document = documentRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        document.softDelete();
        documentRepository.save(document);
    }

    @Override
    public DocumentDTO restoreDocument(Long id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        if (!document.isDeleted()) {
            throw new IllegalStateException("Document is not deleted");
        }

        document.restore();
        document = documentRepository.save(document);

        return convertToDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentDTO> listDocumentsByTag(String tagName, Pageable pageable) {
        Tag tag = tagRepository.findByNameAndParentIsNull(tagName)
            .orElseThrow(() -> new ResourceNotFoundException("Tag", tagName));

        return documentRepository.findByTag(tag, pageable)
            .map(this::convertToDTO);
    }

    // Helper methods

    private void validateContentSize(String content) {
        if (content != null && content.length() > MAX_CONTENT_SIZE) {
            throw new DocumentSizeLimitException(content.length());
        }
    }

    private Set<Tag> processTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();

        for (String tagName : tagNames) {
            if (tagName == null || tagName.isBlank()) {
                continue;
            }

            // Try to find existing tag
            Tag tag = tagRepository.findByNameAndParentIsNull(tagName.trim())
                .orElseGet(() -> {
                    // Create new tag if it doesn't exist
                    Tag newTag = new Tag(tagName.trim());
                    return tagRepository.save(newTag);
                });

            tags.add(tag);
        }

        return tags;
    }

    private DocumentDTO convertToDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setContent(document.getContent());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setDeletedAt(document.getDeletedAt());
        dto.setCurrentVersion(document.getCurrentVersion());

        // Convert tags
        if (document.getTags() != null && !document.getTags().isEmpty()) {
            Set<String> tagNames = document.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
            dto.setTags(tagNames);
        }

        return dto;
    }
}
