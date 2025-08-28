package com.example.TP.repository;

import com.example.TP.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepo extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findOptionalByBusinessIdAndIdAndArchiveFalse(long businessId, Long id);

    @Query("SELECT u FROM Hotel u WHERE u.businessId = :businessId AND u.archive = false " +
            "AND (:name IS NULL OR LOWER(u.name) LIKE CONCAT('%', LOWER(:name), '%')) " +
            "AND (:slug IS NULL OR u.slug LIKE CONCAT('%', :slug, '%'))")
    Page<Hotel> findByBusinessIdAndFilters(@Param("businessId") Long businessId,
                                                 @Param("name") String name,
                                                 @Param("slug") String slug,
                                                 Pageable pageable);

    List<Hotel> findByBusinessIdAndNameContainingIgnoreCaseAndArchiveFalse(long businessId, String name);

    List<Hotel> findByBusinessIdAndArchiveFalse(long businessId);
}
