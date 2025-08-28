package com.example.TP.repository;

import com.example.TP.model.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepo extends JpaRepository<Destination,Long> {
    Optional<Destination> findOptionalByBusinessIdAndIdAndArchiveFalse(Long businessId,Long id);

    @Query("SELECT u FROM Destination u WHERE u.businessId = :businessId AND u.archive = false " +
            "AND (:name IS NULL OR LOWER(u.name) LIKE CONCAT('%', LOWER(:name), '%')) " +
            "AND (:email IS NULL OR LOWER(u.country) LIKE CONCAT('%', LOWER(:country), '%')) " +
            "AND (:slug IS NULL OR u.slug LIKE CONCAT('%', :slug, '%'))")
    Page<Destination> findByBusinessIdAndFilters(@Param("businessId") Long businessId,
                                          @Param("name") String name,
                                          @Param("country") String country,
                                          @Param("slug") String slug,
                                          Pageable pageable);

    List<Destination> findByBusinessIdAndNameContainingIgnoreCaseAndArchiveFalse(long businessId, String trim);

    List<Destination> findByBusinessIdAndArchiveFalse(long businessId);
}
