package com.onion.backend.advertisement.service;


import static com.onion.backend.common.OnionConstant.AD_CACHE_KEY_PREFIX;
import static com.onion.backend.common.OnionConstant.AD_CACHE_TTL;
import static com.onion.backend.common.OnionConstant.CACHE_KEY_SEPARATOR;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onion.backend.advertisement.dto.AdResponse;
import com.onion.backend.advertisement.event.AdvertisementClickEvent;
import com.onion.backend.advertisement.event.AdvertisementReadEvent;
import com.onion.backend.advertisement.mapper.AdvertisementMapper;
import com.onion.backend.advertisement.repository.AdvertisementRepository;
import com.onion.backend.common.util.AuthUtil;
import com.onion.backend.infrastructure.redis.RedisHandler;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AdvertisementQueryService {

    private final AdvertisementRepository advertisementRepository;
    private final RedisHandler redisHandler;
    private final ApplicationEventPublisher publisher;

    public AdResponse getAdvertisement(UUID advertisementId, String ip, boolean isTrueView) {
        String key = AD_CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + advertisementId;

        AdResponse adResponse = redisHandler.get(key, AdResponse.class).orElseGet(() -> {
                    AdResponse response = AdvertisementMapper.toResponse(
                            advertisementRepository.findByIdAndIsDeletedFalseOrThrow(advertisementId));

                    redisHandler.set(key, response, AD_CACHE_TTL);
                    return response;
                }
        );

        publishReadAdvertisement(advertisementId, ip, isTrueView);

        return adResponse;
    }

    private void publishReadAdvertisement(UUID advertisementId, String ip, boolean isTrueView) {
        UUID userId = AuthUtil.getUserId();
        publisher.publishEvent(new AdvertisementReadEvent(advertisementId, userId, ip, isTrueView));
    }

    public List<AdResponse> getAdvertisementsIsDeletedFalse() {
        String key = AD_CACHE_KEY_PREFIX + CACHE_KEY_SEPARATOR + "all";

        return redisHandler.getList(key, new TypeReference<List<AdResponse>>() {
        }).orElseGet(() -> {
            List<AdResponse> advertisements = advertisementRepository.findAllByIsDeletedFalse().stream()
                    .map(AdvertisementMapper::toResponse)
                    .toList();

            redisHandler.set(key, advertisements, AD_CACHE_TTL);
            return advertisements;
        });
    }

    public void clickAdvertisement(UUID advertisementId, String remoteAddr) {
        UUID userId = AuthUtil.getUserId();
        publisher.publishEvent(new AdvertisementClickEvent(advertisementId, userId, remoteAddr));
    }
}
