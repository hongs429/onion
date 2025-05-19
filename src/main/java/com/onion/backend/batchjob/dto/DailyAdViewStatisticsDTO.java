package com.onion.backend.batchjob.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyAdViewStatisticsDTO {
    private UUID dailyAdViewStatisticsId;
    private UUID advertisementId;
    private long uniqueViewCount;
    private LocalDate statisticsDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
