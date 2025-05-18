package com.onion.backend.advertisement.service;


import com.onion.backend.advertisement.dto.AdWriteRequest;
import com.onion.backend.advertisement.entity.AdvertisementEntity;
import com.onion.backend.advertisement.mapper.AdvertisementMapper;
import com.onion.backend.advertisement.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class AdvertisementCommandService {

    private final AdvertisementRepository advertisementRepository;

    public void writeAdvertisement(AdWriteRequest request) {
        AdvertisementEntity advertisement = AdvertisementMapper.toEntity(request);
        advertisementRepository.save(advertisement);
    }

}
