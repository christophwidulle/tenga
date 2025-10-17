package de.christophdick.tenga.repository;

import de.christophdick.tenga.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Find tag by name and parent (for uniqueness check)
     */
    @Query("SELECT t FROM Tag t WHERE t.name = :name AND t.parent = :parent")
    Optional<Tag> findByNameAndParent(@Param("name") String name, @Param("parent") Tag parent);

    /**
     * Find tag by name with no parent
     */
    @Query("SELECT t FROM Tag t WHERE t.name = :name AND t.parent IS NULL")
    Optional<Tag> findByNameAndParentIsNull(@Param("name") String name);

    /**
     * Find all root tags (tags with no parent)
     */
    @Query("SELECT t FROM Tag t WHERE t.parent IS NULL ORDER BY t.name")
    List<Tag> findAllRootTags();

    /**
     * Find all children of a tag
     */
    @Query("SELECT t FROM Tag t WHERE t.parent = :parent ORDER BY t.name")
    List<Tag> findByParent(@Param("parent") Tag parent);

    /**
     * Find all descendants of a tag (recursive query using CTE)
     */
    @Query(value = """
        WITH RECURSIVE tag_tree AS (
            SELECT id, name, parent_id, created_at, 0 AS depth
            FROM tags
            WHERE id = :tagId
            UNION ALL
            SELECT t.id, t.name, t.parent_id, t.created_at, tt.depth + 1
            FROM tags t
            INNER JOIN tag_tree tt ON t.parent_id = tt.id
            WHERE tt.depth < :maxDepth
        )
        SELECT * FROM tag_tree
        ORDER BY depth, name
        """, nativeQuery = true)
    List<Tag> findAllDescendants(@Param("tagId") Long tagId, @Param("maxDepth") int maxDepth);

    /**
     * Find all ancestors of a tag (recursive query using CTE)
     */
    @Query(value = """
        WITH RECURSIVE tag_tree AS (
            SELECT id, name, parent_id, created_at, 0 AS depth
            FROM tags
            WHERE id = :tagId
            UNION ALL
            SELECT t.id, t.name, t.parent_id, t.created_at, tt.depth + 1
            FROM tags t
            INNER JOIN tag_tree tt ON tt.parent_id = t.id
            WHERE tt.depth < :maxDepth
        )
        SELECT * FROM tag_tree
        ORDER BY depth DESC
        """, nativeQuery = true)
    List<Tag> findAllAncestors(@Param("tagId") Long tagId, @Param("maxDepth") int maxDepth);

    /**
     * Check if a tag has any documents associated
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Tag t JOIN t.documents d WHERE t = :tag")
    boolean hasDocuments(@Param("tag") Tag tag);

    /**
     * Count documents for a tag
     */
    @Query("SELECT COUNT(d) FROM Tag t JOIN t.documents d WHERE t = :tag AND d.deletedAt IS NULL")
    Long countDocuments(@Param("tag") Tag tag);

    /**
     * Search tags by name (case-insensitive partial match)
     */
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY t.name")
    List<Tag> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find tags with most documents (popular tags)
     */
    @Query("SELECT t FROM Tag t LEFT JOIN t.documents d WHERE d.deletedAt IS NULL GROUP BY t ORDER BY COUNT(d) DESC")
    List<Tag> findMostPopularTags();
}
