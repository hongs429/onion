package com.onion.backend.advertisement.event.handler;

import com.onion.backend.adhistory.service.AdClickHistoryService;
import com.onion.backend.adhistory.service.AdViewHistoryService;
import com.onion.backend.advertisement.event.AdvertisementClickEvent;
import com.onion.backend.advertisement.event.AdvertisementReadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdvertisementHandler {

    private final AdViewHistoryService adViewHistoryService;
    private final AdClickHistoryService adClickHistoryService;

    @Async(value = "default-async")
    @EventListener
    public void handleReadAdvertisement(AdvertisementReadEvent event) {
        adViewHistoryService.insertAdViewHistory(
                event.getAdvertisementId(),
                event.getUserId(),
                event.getIp(),
                event.isTrueView());
    }

    @Async(value = "default-async")
    @EventListener
    public void handleClickAdvertisement(AdvertisementClickEvent event) {
        adClickHistoryService.insertAdClickHistory(
                event.getAdvertisementId(),
                event.getUserId(),
                event.getIp());
    }
}
