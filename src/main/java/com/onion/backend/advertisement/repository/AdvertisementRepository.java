package com.onion.backend.advertisement.repository;

import com.onion.backend.advertisement.entity.AdvertisementEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, UUID> {

    Optional<AdvertisementEntity> findByIdAndIsDeletedFalse(UUID advertisementId);

    List<AdvertisementEntity> findAllByIsDeletedFalse();

    default AdvertisementEntity findByIdAndIsDeletedFalseOrThrow(UUID advertisementId) {
        return findByIdAndIsDeletedFalse(advertisementId).orElseThrow(
                () -> new IllegalStateException("Advertisement with id " + advertisementId + " not found")
        );
    }
}
