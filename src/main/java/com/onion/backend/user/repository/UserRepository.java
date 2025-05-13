package com.onion.backend.user.repository;

import com.onion.backend.user.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);


    default UserEntity findByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
