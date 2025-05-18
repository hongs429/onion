package com.onion.backend.adhistory.repository;

import com.onion.backend.adhistory.entity.AdViewHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdViewHistoryRepository extends MongoRepository<AdViewHistory, String> {
}
