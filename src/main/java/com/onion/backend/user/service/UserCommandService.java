package com.onion.backend.user.service;


import com.onion.backend.user.dto.UserCreateRequest;
import com.onion.backend.user.entity.UserEntity;
import com.onion.backend.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;

    public UUID createUser(UserCreateRequest request) {

        UserEntity newUser = UserEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getUsername())
                .build();

        userRepository.save(newUser);

        return newUser.getId();
    }

    public void deleteUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }
}
