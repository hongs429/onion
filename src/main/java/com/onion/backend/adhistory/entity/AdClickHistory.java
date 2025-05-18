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
@Document(collection = "adClickHistory")
public class AdClickHistory implements Serializable {

    @Id
    private String id;

    private String advertisementId;

    private String userId;

    private String ip;

    @CreatedDate
    private LocalDateTime createDateTime;

    public static AdClickHistory of(UUID advertisementId, UUID userId, String ip) {

        if (userId == null) {
            return AdClickHistory.builder()
                    .advertisementId(advertisementId.toString())
                    .ip(ip)
                    .userId(null)
                    .build();
        }

        return AdClickHistory.builder()
                .advertisementId(advertisementId.toString())
                .userId(userId.toString())
                .ip(ip)
                .build();
    }
}
