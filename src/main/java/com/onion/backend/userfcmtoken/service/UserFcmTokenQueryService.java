package com.onion.backend.userfcmtoken.service;


import com.onion.backend.userfcmtoken.dto.UserFcmTokenResponse;
import com.onion.backend.userfcmtoken.entity.UserFcmTokenDocument;
import com.onion.backend.userfcmtoken.mapper.UserFcmTokenMapper;
import com.onion.backend.userfcmtoken.repository.UserFcmTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserFcmTokenQueryService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    public UserFcmTokenResponse getUserFcmTokens(UUID userId) {
        UserFcmTokenDocument tokenDocument = userFcmTokenRepository.findByUserId(userId.toString()).orElseThrow(
                () -> new IllegalStateException("User with id " + userId + " not found")
        );

        return UserFcmTokenMapper.toResponse(tokenDocument);
    }
}
