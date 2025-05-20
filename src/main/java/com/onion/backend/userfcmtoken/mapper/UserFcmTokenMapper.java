package com.onion.backend.userfcmtoken.mapper;

import com.onion.backend.userfcmtoken.dto.UserFcmTokenCreateRequest;
import com.onion.backend.userfcmtoken.dto.UserFcmTokenResponse;
import com.onion.backend.userfcmtoken.entity.UserFcmTokenDocument;
import com.onion.backend.userfcmtoken.entity.UserFcmTokenDocument.DeviceToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserFcmTokenMapper {

    public static UserFcmTokenDocument toDocument(UUID userId, UserFcmTokenCreateRequest request) {

        LocalDateTime now = LocalDateTime.now();

        DeviceToken device = DeviceToken.builder()
                .deviceId(request.getDeviceId())
                .deviceType(request.getDeviceType())
                .fcmToken(request.getFcmToken())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return UserFcmTokenDocument.builder()
                .userId(userId.toString())
                .devices(List.of(device))
                .build();
    }

    public static UserFcmTokenResponse toResponse(UserFcmTokenDocument document) {
        return UserFcmTokenResponse.builder()
                .userId(document.getUserId())
                .devices(document.getDevices())
                .build();
    }
}
