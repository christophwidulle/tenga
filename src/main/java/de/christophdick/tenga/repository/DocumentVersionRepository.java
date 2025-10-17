package de.christophdick.tenga.repository;

import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {

    /**
     * Find all versions for a document, ordered by version number descending
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.document = :document ORDER BY dv.versionNumber DESC")
    List<DocumentVersion> findByDocumentOrderByVersionNumberDesc(@Param("document") Document document);

    /**
     * Find a specific version for a document
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.document = :document AND dv.versionNumber = :versionNumber")
    Optional<DocumentVersion> findByDocumentAndVersionNumber(@Param("document") Document document, @Param("versionNumber") Integer versionNumber);

    /**
     * Find the latest version for a document
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.document = :document ORDER BY dv.versionNumber DESC LIMIT 1")
    Optional<DocumentVersion> findLatestByDocument(@Param("document") Document document);

    /**
     * Count versions for a document
     */
    @Query("SELECT COUNT(dv) FROM DocumentVersion dv WHERE dv.document = :document")
    Long countByDocument(@Param("document") Document document);

    /**
     * Find the maximum version number for a document
     */
    @Query("SELECT MAX(dv.versionNumber) FROM DocumentVersion dv WHERE dv.document = :document")
    Optional<Integer> findMaxVersionNumberByDocument(@Param("document") Document document);

    /**
     * Delete old versions beyond the limit (keep latest N versions)
     */
    @Modifying
    @Query("DELETE FROM DocumentVersion dv WHERE dv.document = :document AND dv.versionNumber NOT IN (SELECT dv2.versionNumber FROM DocumentVersion dv2 WHERE dv2.document = :document ORDER BY dv2.versionNumber DESC LIMIT :keepCount)")
    void deleteOldVersions(@Param("document") Document document, @Param("keepCount") int keepCount);

    /**
     * Find versions older than a specific version number
     */
    @Query("SELECT dv FROM DocumentVersion dv WHERE dv.document = :document AND dv.versionNumber < :versionNumber ORDER BY dv.versionNumber DESC")
    List<DocumentVersion> findVersionsOlderThan(@Param("document") Document document, @Param("versionNumber") Integer versionNumber);
}
