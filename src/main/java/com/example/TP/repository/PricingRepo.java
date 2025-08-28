package com.example.TP.repository;

import com.example.TP.model.Pricing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PricingRepo extends JpaRepository<Pricing, Long> {
    Optional<Pricing> findOptionalById(Long id);

    @Query("SELECT u FROM Pricing u WHERE " +
            "(:packageId IS NULL OR u.packageId = :packageId)")
    Page<Pricing> findByFilters(@Param("packageId") Long packageId,
                                  Pageable pageable);
}
