package com.onion.backend.adhistory.service;


import com.onion.backend.adhistory.entity.AdViewHistory;
import com.onion.backend.adhistory.repository.AdViewHistoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdViewHistoryService {

    private final AdViewHistoryRepository adViewHistoryRepository;

    public void insertAdViewHistory(UUID advertisementId, UUID userId, String ip, boolean isTrueView) {
        AdViewHistory adViewHistory = AdViewHistory.of(advertisementId, userId, ip, isTrueView);
        adViewHistoryRepository.save(adViewHistory);
    }
}
