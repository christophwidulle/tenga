package de.christophdick.tenga.repository;

import de.christophdick.tenga.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    /**
     * Find API key by key hash
     */
    @Query("SELECT ak FROM ApiKey ak WHERE ak.keyHash = :keyHash")
    Optional<ApiKey> findByKeyHash(@Param("keyHash") String keyHash);

    /**
     * Check if an API key hash exists
     */
    @Query("SELECT CASE WHEN COUNT(ak) > 0 THEN true ELSE false END FROM ApiKey ak WHERE ak.keyHash = :keyHash")
    boolean existsByKeyHash(@Param("keyHash") String keyHash);
}
