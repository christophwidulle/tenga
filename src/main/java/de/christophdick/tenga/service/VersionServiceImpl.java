package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.DocumentVersionDTO;
import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.DocumentVersion;
import de.christophdick.tenga.entity.Tag;
import de.christophdick.tenga.exception.ResourceNotFoundException;
import de.christophdick.tenga.repository.DocumentRepository;
import de.christophdick.tenga.repository.DocumentVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VersionServiceImpl implements VersionService {

    private static final int MAX_VERSIONS_TO_KEEP = 10;

    private final DocumentVersionRepository versionRepository;
    private final DocumentRepository documentRepository;

    public VersionServiceImpl(DocumentVersionRepository versionRepository,
                             DocumentRepository documentRepository) {
        this.versionRepository = versionRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public void createVersion(Document document, String changeSummary) {
        // Create version snapshot
        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber(document.getCurrentVersion());
        version.setTitle(document.getTitle());
        version.setContent(document.getContent());
        version.setChangeSummary(changeSummary);

        // Snapshot tags as JSON array
        if (document.getTags() != null && !document.getTags().isEmpty()) {
            String tagsSnapshot = document.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.joining(",", "[", "]"));
            version.setTagsSnapshot(tagsSnapshot);
        }

        versionRepository.save(version);

        // Prune old versions
        pruneOldVersions(document.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentVersionDTO> getVersionHistory(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        return versionRepository.findByDocumentOrderByVersionNumberDesc(document).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVersionDTO getVersion(Long documentId, Integer versionNumber) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        DocumentVersion version = versionRepository.findByDocumentAndVersionNumber(document, versionNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Version " + versionNumber + " of document", documentId));

        return convertToDTO(version);
    }

    @Override
    public DocumentVersionDTO restoreVersion(Long documentId, Integer versionNumber) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        DocumentVersion version = versionRepository.findByDocumentAndVersionNumber(document, versionNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Version " + versionNumber + " of document", documentId));

        // Update document with version content
        document.setTitle(version.getTitle());
        document.setContent(version.getContent());
        document.setCurrentVersion(document.getCurrentVersion() + 1);

        documentRepository.save(document);

        // Create new version
        createVersion(document, "Restored from version " + versionNumber);

        return convertToDTO(version);
    }

    @Override
    @Transactional(readOnly = true)
    public String compareVersions(Long documentId, Integer version1, Integer version2) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        DocumentVersion v1 = versionRepository.findByDocumentAndVersionNumber(document, version1)
            .orElseThrow(() -> new ResourceNotFoundException("Version " + version1 + " of document", documentId));

        DocumentVersion v2 = versionRepository.findByDocumentAndVersionNumber(document, version2)
            .orElseThrow(() -> new ResourceNotFoundException("Version " + version2 + " of document", documentId));

        // Simple line-by-line diff (basic implementation)
        StringBuilder diff = new StringBuilder();
        diff.append("Comparison between version ").append(version1)
            .append(" and version ").append(version2).append("\n\n");

        // Title diff
        if (!v1.getTitle().equals(v2.getTitle())) {
            diff.append("Title changed:\n");
            diff.append("  - ").append(v1.getTitle()).append("\n");
            diff.append("  + ").append(v2.getTitle()).append("\n\n");
        }

        // Content diff (simplified - just show if changed)
        if (!v1.getContent().equals(v2.getContent())) {
            diff.append("Content changed:\n");
            diff.append("  Version ").append(version1).append(" content length: ")
                .append(v1.getContent().length()).append(" characters\n");
            diff.append("  Version ").append(version2).append(" content length: ")
                .append(v2.getContent().length()).append(" characters\n");
        } else {
            diff.append("Content unchanged\n");
        }

        return diff.toString();
    }

    @Override
    public void pruneOldVersions(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        Long versionCount = versionRepository.countByDocument(document);

        if (versionCount > MAX_VERSIONS_TO_KEEP) {
            versionRepository.deleteOldVersions(document, MAX_VERSIONS_TO_KEEP);
        }
    }

    // Helper methods

    private DocumentVersionDTO convertToDTO(DocumentVersion version) {
        DocumentVersionDTO dto = new DocumentVersionDTO();
        dto.setId(version.getId());
        dto.setDocumentId(version.getDocument().getId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setTitle(version.getTitle());
        dto.setContent(version.getContent());
        dto.setTagsSnapshot(version.getTagsSnapshot());
        dto.setCreatedAt(version.getCreatedAt());
        dto.setChangeSummary(version.getChangeSummary());
        return dto;
    }
}
