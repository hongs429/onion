package com.onion.backend.article.mapper;


import com.onion.backend.article.dto.ArticleResponse;
import com.onion.backend.article.entity.ArticleDocument;
import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.common.domain.Author;

public class ArticleMapper {

    public static ArticleDocument toDocument(Author author, ArticleResponse article) {
        return ArticleDocument.builder()
                .title(article.getTitle())
                .content(article.getContent())
                .isDeleted(article.isDeleted())
                .viewCount(article.getViewCount())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .boardId(article.getBoardId())
                .author(author)
                .build();
    }

    public static ArticleResponse toResponse(ArticleEntity article) {
        return ArticleResponse.builder()
                .articleId(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .isDeleted(article.getIsDeleted())
                .viewCount(article.getViewCount())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .authorId(article.getAuthor().getId())
                .boardId(article.getBoard().getId())
                .build();
    }
}
