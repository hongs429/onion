package com.onion.backend.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Long commentId;
    private String content;
    private UUID authorId;
    private Long articleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
