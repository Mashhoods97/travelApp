package com.example.TP.repository;

import com.example.TP.model.Package;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepo extends JpaRepository<Package, Long> {

    Optional<Package> findOptionalByBusinessIdAndIdAndArchiveFalse(long businessId, Long id);

    @Query("SELECT u FROM Package u WHERE u.businessId = :businessId AND u.archive = false " +
            "AND (:name IS NULL OR LOWER(u.name) LIKE CONCAT('%', LOWER(:name), '%')) " +
            "AND (:code IS NULL OR u.code LIKE CONCAT('%', :code, '%'))")
    Page<Package> findByBusinessIdAndFilters(@Param("businessId") Long businessId,
                                           @Param("name") String name,
                                           @Param("code") String code,
                                           Pageable pageable);
}
