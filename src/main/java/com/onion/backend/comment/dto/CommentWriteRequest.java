package com.onion.backend.comment.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentWriteRequest {
    private String content;
}
