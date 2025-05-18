package com.onion.backend.adhistory.service;

import com.onion.backend.adhistory.entity.AdClickHistory;
import com.onion.backend.adhistory.repository.AdClickHistoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdClickHistoryService {

    private final AdClickHistoryRepository adClickHistoryRepository;

    public void insertAdClickHistory(UUID advertisementId, UUID userId, String ip) {
        AdClickHistory adClickHistory = AdClickHistory.of(advertisementId, userId, ip);
        adClickHistoryRepository.save(adClickHistory);
    }
}
