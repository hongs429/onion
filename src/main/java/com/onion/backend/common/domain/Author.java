package com.onion.backend.common.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    private UUID userId;
    private String username;
    private String email;


    public static Author of(UUID userId, String username, String email) {
        return Author.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .build();
    }
}