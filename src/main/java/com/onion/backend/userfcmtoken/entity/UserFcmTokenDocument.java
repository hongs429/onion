package com.onion.backend.userfcmtoken.entity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "userFcmToken")
public class UserFcmTokenDocument {

    @Id
    private String id;

    private String userId;

    @Builder.Default
    private List<DeviceToken> devices = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceToken {
        private String deviceId;

        private String fcmToken;

        private String deviceType;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }


    public void removeDevice(String deviceId) {
        devices.removeIf(device -> device.getDeviceId().equals(deviceId));
    }

    public void upsertDeviceToken(String deviceId, String newToken, String deviceType) {

        boolean tokenExists = devices.stream()
                .anyMatch(d -> !d.getDeviceId().equals(deviceId) &&
                        d.getFcmToken().equals(newToken));

        if (tokenExists) {
            throw new IllegalArgumentException("Token already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        DeviceToken upsertDevice = null;

        for (DeviceToken device : devices) {
            if (device.getDeviceId().equals(deviceId)) {
                upsertDevice = DeviceToken.builder()
                        .deviceId(device.getDeviceId())
                        .fcmToken(newToken)
                        .deviceType(device.getDeviceType())
                        .createdAt(device.getCreatedAt())
                        .updatedAt(now)
                        .build();
            }
        }

        if (upsertDevice == null) {
            upsertDevice = DeviceToken.builder()
                    .deviceId(deviceId)
                    .fcmToken(newToken)
                    .deviceType(deviceType)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

        }

        addOrUpdateDevice(upsertDevice);
    }

    private void addOrUpdateDevice(DeviceToken newDevice) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getDeviceId().equals(newDevice.getDeviceId())) {
                devices.set(i, newDevice);
                return;
            }
        }

        addDevice(newDevice);
    }

    private void addDevice(DeviceToken device) {
        devices.add(device);
    }
}
