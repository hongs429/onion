package com.onion.backend.userfcmtoken.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFcmTokenCreateRequest {
    private String deviceId;
    private String fcmToken;
    private String deviceType;
}
