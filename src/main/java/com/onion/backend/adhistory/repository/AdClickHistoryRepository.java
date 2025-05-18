package com.onion.backend.adhistory.repository;

import com.onion.backend.adhistory.entity.AdClickHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdClickHistoryRepository extends MongoRepository<AdClickHistory, String> {
}
