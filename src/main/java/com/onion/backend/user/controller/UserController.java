package com.onion.backend.user.controller;


import com.onion.backend.user.dto.LoginRequest;
import com.onion.backend.user.dto.LoginResponse;
import com.onion.backend.user.dto.UserCreateRequest;
import com.onion.backend.user.dto.UserResponse;
import com.onion.backend.user.service.UserCommandService;
import com.onion.backend.user.service.UserQueryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final UserQueryService userQueryService;


    @PostMapping("/actions/sign-up")
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

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userQueryService.getUsers());
    }

    @PostMapping("/actions/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userCommandService.login(request);
        return ResponseEntity.ok(response);
    }
}
