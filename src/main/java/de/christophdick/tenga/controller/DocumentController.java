package de.christophdick.tenga.controller;

import de.christophdick.tenga.dto.CreateDocumentRequest;
import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.UpdateDocumentRequest;
import de.christophdick.tenga.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        DocumentDTO document = documentService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        DocumentDTO document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    public ResponseEntity<Page<DocumentDTO>> listDocuments(Pageable pageable) {
        Page<DocumentDTO> documents = documentService.listDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentRequest request) {
        DocumentDTO document = documentService.updateDocument(id, request);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.softDeleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<DocumentDTO> restoreDocument(@PathVariable Long id) {
        DocumentDTO document = documentService.restoreDocument(id);
        return ResponseEntity.ok(document);
    }
}
