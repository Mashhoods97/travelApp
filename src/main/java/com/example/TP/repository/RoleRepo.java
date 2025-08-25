package com.example.TP.repository;

import com.example.TP.model.Role;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    @NonNull
    Optional<Role> findById(@NonNull Long roleId);
}
