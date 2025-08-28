package com.example.TP.repository;

import com.example.TP.model.Itinerary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItineraryRepo extends JpaRepository<Itinerary, Long> {
    Optional<Itinerary> findOptionalById(Long id);

    @Query("SELECT u FROM Itinerary u WHERE " +
            "(:title IS NULL OR LOWER(u.title) LIKE CONCAT('%', LOWER(:title), '%')) " +
            "AND (:packageId IS NULL OR u.packageId = :packageId)")
    Page<Itinerary> findByFilters(@Param("title") String title,
                                  @Param("packageId") Long packageId,
                                  Pageable pageable);
}
