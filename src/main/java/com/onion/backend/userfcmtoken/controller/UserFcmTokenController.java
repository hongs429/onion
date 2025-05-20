package com.onion.backend.userfcmtoken.controller;


import com.onion.backend.user.domain.UserDetailsImpl;
import com.onion.backend.userfcmtoken.dto.UserFcmTokenCreateRequest;
import com.onion.backend.userfcmtoken.dto.UserFcmTokenResponse;
import com.onion.backend.userfcmtoken.service.UserFcmTokenCommandService;
import com.onion.backend.userfcmtoken.service.UserFcmTokenQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-fcm-tokens")
public class UserFcmTokenController {

    private final UserFcmTokenCommandService userFcmTokenCommandService;
    private final UserFcmTokenQueryService userFcmTokenQueryService;


    @PostMapping
    public ResponseEntity<Void> addDevice(
            @RequestBody UserFcmTokenCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userFcmTokenCommandService.addDevice(userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserFcmTokenResponse> getUserToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return ResponseEntity.ok(userFcmTokenQueryService.getUserFcmTokens(userDetails.getUserId()));

    }
}
