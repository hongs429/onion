package com.onion.backend.article.entity;


import com.onion.backend.common.domain.Author;
import com.onion.backend.common.entity.DocumentEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDocument implements DocumentEntity {

    private String title;
    private String content;
    private Long viewCount;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long boardId;
    private Author author;

}
