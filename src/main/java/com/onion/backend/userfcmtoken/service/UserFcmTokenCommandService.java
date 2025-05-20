package com.onion.backend.userfcmtoken.service;


import com.onion.backend.userfcmtoken.dto.UserFcmTokenCreateRequest;
import com.onion.backend.userfcmtoken.entity.UserFcmTokenDocument;
import com.onion.backend.userfcmtoken.mapper.UserFcmTokenMapper;
import com.onion.backend.userfcmtoken.repository.UserFcmTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserFcmTokenCommandService {

    private final UserFcmTokenRepository userFcmTokenRepository;

    public void addDevice(UUID userId, UserFcmTokenCreateRequest request) {
        userFcmTokenRepository.findByUserId(userId.toString()).ifPresentOrElse(
                document -> {
                    document.upsertDeviceToken(
                            request.getDeviceId(),
                            request.getFcmToken(),
                            request.getDeviceType());

                    userFcmTokenRepository.save(document);
                },
                ()-> {
                    UserFcmTokenDocument document = UserFcmTokenMapper.toDocument(userId, request);
                    userFcmTokenRepository.save(document);
                }
        );
    }
}
