package com.onion.backend.userfcmtoken.dto;


import com.onion.backend.userfcmtoken.entity.UserFcmTokenDocument.DeviceToken;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFcmTokenResponse {
    private String userId;
    private List<DeviceToken> devices;
}
