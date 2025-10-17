package de.christophdick.tenga.repository;

import de.christophdick.tenga.entity.Document;
import de.christophdick.tenga.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find all non-deleted documents
     */
    @Query("SELECT d FROM Document d WHERE d.deletedAt IS NULL")
    Page<Document> findAllActive(Pageable pageable);

    /**
     * Find a non-deleted document by ID
     */
    @Query("SELECT d FROM Document d WHERE d.id = :id AND d.deletedAt IS NULL")
    Optional<Document> findActiveById(@Param("id") Long id);

    /**
     * Find all deleted documents
     */
    @Query("SELECT d FROM Document d WHERE d.deletedAt IS NOT NULL")
    Page<Document> findAllDeleted(Pageable pageable);

    /**
     * Find documents by tag
     */
    @Query("SELECT DISTINCT d FROM Document d JOIN d.tags t WHERE t = :tag AND d.deletedAt IS NULL")
    Page<Document> findByTag(@Param("tag") Tag tag, Pageable pageable);

    /**
     * Find documents by multiple tags (intersection)
     */
    @Query("SELECT d FROM Document d JOIN d.tags t WHERE t IN :tags AND d.deletedAt IS NULL GROUP BY d HAVING COUNT(DISTINCT t) = :tagCount")
    Page<Document> findByAllTags(@Param("tags") List<Tag> tags, @Param("tagCount") Long tagCount, Pageable pageable);

    /**
     * Find documents updated within date range
     */
    @Query("SELECT d FROM Document d WHERE d.deletedAt IS NULL AND d.updatedAt BETWEEN :startDate AND :endDate")
    Page<Document> findByUpdatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find documents created within date range
     */
    @Query("SELECT d FROM Document d WHERE d.deletedAt IS NULL AND d.createdAt BETWEEN :startDate AND :endDate")
    Page<Document> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Count active documents
     */
    @Query("SELECT COUNT(d) FROM Document d WHERE d.deletedAt IS NULL")
    Long countActive();
}
