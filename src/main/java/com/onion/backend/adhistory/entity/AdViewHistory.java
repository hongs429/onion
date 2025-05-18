package com.onion.backend.adhistory.entity;


import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Document(collection = "adViewHistory")
public class AdViewHistory implements Serializable{
    @Id
    private String id;

    private String advertisementId;

    private String userId;

    private String ip;

    @Builder.Default
    private Boolean isTrueView = false;

    @CreatedDate
    private LocalDateTime createDateTime;

    public static AdViewHistory of(UUID advertisementId, UUID userId, String ip, boolean isTrueView) {

        if (userId == null) {
            return AdViewHistory.builder()
                    .advertisementId(advertisementId.toString())
                    .ip(ip)
                    .userId(null)
                    .isTrueView(isTrueView)
                    .build();
        }

        return AdViewHistory.builder()
                .advertisementId(advertisementId.toString())
                .userId(userId.toString())
                .ip(ip)
                .isTrueView(isTrueView)
                .build();
    }
}
