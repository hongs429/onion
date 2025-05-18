package com.onion.backend.advertisement.dto;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdResponse {
    private UUID advertisementId;
    private String title;
    private String content;
    private Boolean isDeleted;
    private Boolean isVisible;
    private Long viewCount;
    private Long clickCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
