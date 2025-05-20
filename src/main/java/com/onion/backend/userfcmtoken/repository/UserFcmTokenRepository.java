package com.onion.backend.userfcmtoken.repository;

import com.onion.backend.userfcmtoken.entity.UserFcmTokenDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserFcmTokenRepository extends MongoRepository<UserFcmTokenDocument, String> {
    Optional<UserFcmTokenDocument> findByUserId(String userId);
}
