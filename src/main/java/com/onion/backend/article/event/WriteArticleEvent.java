package com.onion.backend.article.event;

import com.onion.backend.common.domain.Author;
import com.onion.backend.article.dto.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriteArticleEvent {
    private Author author;
    private ArticleResponse article;
}
