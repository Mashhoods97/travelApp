package com.example.TP.repository;

import com.example.TP.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
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

    boolean existsByEmailAndArchive(String email, boolean b);

    Optional<User> findOptionalByUsernameAndActive(String name, boolean b);
}
