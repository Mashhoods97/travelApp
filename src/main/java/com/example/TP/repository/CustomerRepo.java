package com.example.TP.repository;

import com.example.TP.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findOptionalByBusinessIdAndIdAndArchiveFalse(long businessId, Long id);

    @Query("SELECT u FROM Customer u WHERE u.businessId = :businessId AND u.archive = false " +
            "AND (:name IS NULL OR LOWER(u.firstName) LIKE CONCAT('%', LOWER(:name), '%'))")
    Page<Customer> findByBusinessIdAndFilters(@Param("businessId") Long businessId,
                                           @Param("name") String name,
                                           Pageable pageable);

    List<Customer> findByBusinessIdAndFirstNameContainingIgnoreCaseAndArchiveFalse(long businessId, String trim);

    List<Customer> findByBusinessIdAndArchiveFalse(long businessId);

    List<Customer> findByBusinessIdAndIdInAndArchiveFalse(
            Long businessId,
            Set<Long> ids
    );

}
