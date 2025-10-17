package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.CreateDocumentRequest;
import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.UpdateDocumentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {

    /**
     * Create a new document
     */
    DocumentDTO createDocument(CreateDocumentRequest request);

    /**
     * Get document by ID (active documents only)
     */
    DocumentDTO getDocumentById(Long id);

    /**
     * List all active documents with pagination
     */
    Page<DocumentDTO> listDocuments(Pageable pageable);

    /**
     * Update an existing document (creates a new version)
     */
    DocumentDTO updateDocument(Long id, UpdateDocumentRequest request);

    /**
     * Soft delete a document
     */
    void softDeleteDocument(Long id);

    /**
     * Restore a soft-deleted document
     */
    DocumentDTO restoreDocument(Long id);

    /**
     * List documents by tag
     */
    Page<DocumentDTO> listDocumentsByTag(String tagName, Pageable pageable);
}
