package de.christophdick.tenga.repository;

import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DocumentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TagRepository tagRepository;

    private Document testDocument;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        // Create test tag
        testTag = new Tag();
        testTag.setName("test-tag");
        testTag = entityManager.persistAndFlush(testTag);

        // Create test document
        testDocument = new Document();
        testDocument.setTitle("Test Document");
        testDocument.setContent("This is test content");
        testDocument.setTags(Set.of(testTag));
        testDocument = entityManager.persistAndFlush(testDocument);

        entityManager.clear();
    }

    @Test
    void testFindAllActive_ReturnsOnlyActiveDocuments() {
        // Create a soft-deleted document
        Document deletedDoc = new Document();
        deletedDoc.setTitle("Deleted Document");
        deletedDoc.setContent("Deleted content");
        deletedDoc.softDelete();
        entityManager.persistAndFlush(deletedDoc);

        // Query active documents
        Page<Document> activeDocs = documentRepository.findAllActive(PageRequest.of(0, 10));

        assertThat(activeDocs.getContent()).hasSize(1);
        assertThat(activeDocs.getContent().get(0).getTitle()).isEqualTo("Test Document");
    }

    @Test
    void testFindActiveById_ReturnsDocumentWhenActive() {
        Optional<Document> found = documentRepository.findActiveById(testDocument.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Document");
    }

    @Test
    void testFindActiveById_ReturnsEmptyWhenDeleted() {
        testDocument.softDelete();
        entityManager.merge(testDocument);
        entityManager.flush();

        Optional<Document> found = documentRepository.findActiveById(testDocument.getId());

        assertThat(found).isEmpty();
    }

    @Test
    void testFindByAllTags_ReturnsDocumentsWithAllTags() {
        // Create another document with the same tag
        Document doc2 = new Document();
        doc2.setTitle("Document 2");
        doc2.setContent("Content 2");
        doc2.setTags(Set.of(testTag));
        entityManager.persistAndFlush(doc2);

        Page<Document> docs = documentRepository.findByAllTags(
            java.util.List.of(testTag),
            1L,
            PageRequest.of(0, 10)
        );

        assertThat(docs.getContent()).hasSize(2);
    }

    @Test
    void testSoftDelete_DocumentIsNotPermanentlyDeleted() {
        Long docId = testDocument.getId();
        testDocument.softDelete();
        entityManager.merge(testDocument);
        entityManager.flush();

        // Document still exists in database
        Optional<Document> found = documentRepository.findById(docId);
        assertThat(found).isPresent();
        assertThat(found.get().isDeleted()).isTrue();

        // But not in active documents
        Optional<Document> active = documentRepository.findActiveById(docId);
        assertThat(active).isEmpty();
    }
}
