package com.onion.backend.user.controller;


import com.onion.backend.user.dto.UserCreateRequest;
import com.onion.backend.user.service.UserCommandService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserCommandService userCommandService;


    @PostMapping
    public ResponseEntity<UUID> createUser(
            @RequestBody UserCreateRequest request
    ) {
        UUID id = userCommandService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userCommandService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
