package com.onion.backend.article.event;

import com.onion.backend.article.dto.ArticleResponse;
import com.onion.backend.common.domain.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditArticleEvent {
    private Author author;
    private ArticleResponse article;
}
