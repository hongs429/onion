package com.onion.backend.user.service;


import com.onion.backend.user.dto.UserResponse;
import com.onion.backend.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(entity -> UserResponse.builder()
                        .userId(entity.getId())
                        .username(entity.getUsername())
                        .email(entity.getEmail())
                        .build())
                .toList();
    }
}
