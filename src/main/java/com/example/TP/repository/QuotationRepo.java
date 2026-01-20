package com.example.TP.repository;

import com.example.TP.model.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepo extends JpaRepository<Quotation, Long> {
    Optional<Quotation> findOptionalByBusinessIdAndIdAndArchiveFalse(long businessId, Long id);

    @Query("SELECT u FROM Quotation u WHERE u.businessId = :businessId AND u.archive = false " +
            "AND (:title IS NULL OR LOWER(u.title) LIKE CONCAT('%', LOWER(:title), '%')) ")
    Page<Quotation> findByBusinessIdAndFilters(@Param("businessId") Long businessId,
                                           @Param("title") String title,
                                           Pageable pageable);

    List<Quotation> findByBusinessIdAndTitleContainingIgnoreCaseAndArchiveFalse(long businessId, String trim);

    List<Quotation> findByBusinessIdAndArchiveFalse(long businessId);
}
