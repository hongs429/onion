package com.onion.backend.user.service;


import com.onion.backend.common.jwt.JwtProvider;
import com.onion.backend.user.dto.LoginRequest;
import com.onion.backend.user.dto.LoginResponse;
import com.onion.backend.user.dto.UserCreateRequest;
import com.onion.backend.user.entity.UserEntity;
import com.onion.backend.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;

    public UUID createUser(UserCreateRequest request) {

        UserEntity newUser = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtProvider.createToken(user.getEmail());
        return new LoginResponse(accessToken);
    }
}
