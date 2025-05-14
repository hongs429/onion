package com.onion.backend.article.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleEditRequest {
    private String title;
    private String content;
}
