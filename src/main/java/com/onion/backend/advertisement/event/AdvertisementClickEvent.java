package com.onion.backend.advertisement.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdvertisementClickEvent {
    private UUID advertisementId;
    private UUID userId;
    private String ip;
}
