package com.onion.backend.advertisement.controller;


import com.onion.backend.advertisement.dto.AdResponse;
import com.onion.backend.advertisement.dto.AdWriteRequest;
import com.onion.backend.advertisement.service.AdvertisementCommandService;
import com.onion.backend.advertisement.service.AdvertisementQueryService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/advertisements")
public class AdvertisementController {

    private final AdvertisementQueryService advertisementQueryService;
    private final AdvertisementCommandService advertisementCommandService;

    @PostMapping("/admin")
    public ResponseEntity<Void> writeAdvertisement(@RequestBody AdWriteRequest request) {
        advertisementCommandService.writeAdvertisement(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AdResponse>> getAdvertisements() {
        return ResponseEntity.ok(advertisementQueryService.getAdvertisementsIsDeletedFalse());
    }

    @GetMapping("/{advertisementId}")
    public ResponseEntity<AdResponse> getAdvertisement(
            @PathVariable UUID advertisementId,
            @RequestParam(value = "isTrueView", defaultValue = "false") Boolean isTrueView,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                advertisementQueryService.getAdvertisement(advertisementId, request.getRemoteAddr(), isTrueView));
    }

    @PostMapping("/{advertisementId}/actions/click")
    public ResponseEntity<Void> clickAdvertisement(
            @PathVariable UUID advertisementId,
            HttpServletRequest request) {
        advertisementQueryService.clickAdvertisement(advertisementId, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
