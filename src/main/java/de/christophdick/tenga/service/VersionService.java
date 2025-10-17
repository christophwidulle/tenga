package de.christophdick.tenga.service;

import de.christophdick.tenga.dto.DocumentVersionDTO;
import de.christophdick.tenga.entity.Document;

import java.util.List;

public interface VersionService {

    /**
     * Create a new version for a document
     */
    void createVersion(Document document, String changeSummary);

    /**
     * Get version history for a document
     */
    List<DocumentVersionDTO> getVersionHistory(Long documentId);

    /**
     * Get a specific version
     */
    DocumentVersionDTO getVersion(Long documentId, Integer versionNumber);

    /**
     * Restore a document to a specific version (creates new version)
     */
    DocumentVersionDTO restoreVersion(Long documentId, Integer versionNumber);

    /**
     * Compare two versions (returns diff)
     */
    String compareVersions(Long documentId, Integer version1, Integer version2);

    /**
     * Prune old versions (keep latest 10)
     */
    void pruneOldVersions(Long documentId);
}
