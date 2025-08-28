package com.example.TP.repository;

import com.example.TP.model.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @NonNull
    Optional<User> findById(@NonNull Long userId);

    Optional<User> findOptionalByUsernameAndArchive(String username, boolean b);

    boolean existsByEmailAndArchiveFalse(String email);

    boolean existsByUsernameAndArchiveFalse(String username);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User e " +
            "WHERE e.archive = false and " +
            "LOWER(TRIM(e.email)) = LOWER(TRIM(:email)) AND " +
            "e.id <> :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    Optional<User> findOptionalByUsernameAndActive(String name, boolean b);

    @Query("SELECT u FROM User u WHERE u.businessId = :businessId AND u.archive = false " +
            "AND (:name IS NULL OR LOWER(u.firstName) LIKE CONCAT('%', LOWER(:name), '%')) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE CONCAT('%', LOWER(:email), '%')) " +
            "AND (:phone IS NULL OR u.phone LIKE CONCAT('%', :phone, '%'))")
    Page<User> findByBusinessIdAndFilters(@Param("businessId") Long businessId,
                                          @Param("name") String name,
                                          @Param("email") String email,
                                          @Param("phone") String phone,
                                          Pageable pageable);

    Optional<User> findOptionalByBusinessIdAndIdAndArchiveFalse(Long businessId, Long id);
}
