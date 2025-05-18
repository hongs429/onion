package com.onion.backend.advertisement.mapper;

import com.onion.backend.advertisement.dto.AdResponse;
import com.onion.backend.advertisement.dto.AdWriteRequest;
import com.onion.backend.advertisement.entity.AdvertisementEntity;

public class AdvertisementMapper {

    public static AdvertisementEntity toEntity(AdWriteRequest request) {
        return AdvertisementEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .viewCount(request.getViewCount())
                .clickCount(request.getClickCount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    public static AdResponse toResponse(AdvertisementEntity entity) {
        return AdResponse.builder()
                .advertisementId(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .isDeleted(entity.getIsDeleted())
                .isVisible(entity.getIsVisible())
                .clickCount(entity.getClickCount())
                .viewCount(entity.getViewCount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }
}
