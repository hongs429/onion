package com.onion.backend.adhistory.entity;


import com.onion.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity(name = "daily_ad_view_statistics")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyAdViewStatisticsEntity extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "daily_ad_view_statistics_id", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private UUID advertisementId;

    @Column(nullable = false)
    private long uniqueViewCount;

    @Column(nullable = false)
    private LocalDate statisticsDate;
}
