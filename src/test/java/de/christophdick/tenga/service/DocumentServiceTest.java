package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.CreateDocumentRequest;
import de.christophdick.tenga.dto.DocumentDTO;
import de.christophdick.tenga.dto.UpdateDocumentRequest;
import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.Tag;
import de.christophdick.tenga.exception.ResourceNotFoundException;
import de.christophdick.tenga.repository.DocumentRepository;
import de.christophdick.tenga.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private VersionService versionService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Document testDocument;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("test");

        testDocument = new Document();
        testDocument.setId(1L);
        testDocument.setTitle("Test Doc");
        testDocument.setContent("Test content");
        testDocument.setTags(new HashSet<>(Set.of(testTag)));
        testDocument.setCurrentVersion(1);
    }

    @Test
    void testCreateDocument_Success() {
        CreateDocumentRequest request = new CreateDocumentRequest();
        request.setTitle("New Document");
        request.setContent("New content");
        request.setTags(Set.of("test"));

        when(tagRepository.findByNameAndParentIsNull("test")).thenReturn(Optional.of(testTag));
        when(documentRepository.save(any(Document.class))).thenAnswer(i -> {
            Document doc = i.getArgument(0);
            doc.setId(1L);
            return doc;
        });
        doNothing().when(versionService).createVersion(any(Document.class), anyString());

        DocumentDTO result = documentService.createDocument(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Document");
        assertThat(result.getContent()).isEqualTo("New content");
        verify(documentRepository).save(any(Document.class));
        verify(versionService).createVersion(any(Document.class), eq("Initial version"));
    }

    @Test
    void testGetDocumentById_Success() {
        when(documentRepository.findActiveById(1L)).thenReturn(Optional.of(testDocument));

        DocumentDTO result = documentService.getDocumentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Doc");
    }

    @Test
    void testGetDocumentById_NotFound() {
        when(documentRepository.findActiveById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getDocumentById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Document not found with id: 999");
    }

    @Test
    void testUpdateDocument_Success() {
        UpdateDocumentRequest request = new UpdateDocumentRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated content");
        request.setChangeSummary("Fixed typo");

        when(documentRepository.findActiveById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);
        doNothing().when(versionService).createVersion(any(Document.class), anyString());

        DocumentDTO result = documentService.updateDocument(1L, request);

        assertThat(result).isNotNull();
        assertThat(testDocument.getCurrentVersion()).isEqualTo(2);
        verify(versionService).createVersion(any(Document.class), eq("Fixed typo"));
    }

    @Test
    void testSoftDeleteDocument_Success() {
        when(documentRepository.findActiveById(1L)).thenReturn(Optional.of(testDocument));

        documentService.softDeleteDocument(1L);

        assertThat(testDocument.isDeleted()).isTrue();
        verify(documentRepository).save(testDocument);
    }

    @Test
    void testRestoreDocument_Success() {
        testDocument.softDelete();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        DocumentDTO result = documentService.restoreDocument(1L);

        assertThat(result).isNotNull();
        assertThat(testDocument.isDeleted()).isFalse();
    }
}
