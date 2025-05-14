package com.onion.backend.article.repository;

import com.onion.backend.article.entity.ArticleEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findByIsDeletedFalseAndIdAndAuthorId(Long articleId, UUID authorId);

    List<ArticleEntity> findAllByIsDeletedFalseAndBoardIdOrderByCreatedAtDesc(Long boardId);

    List<ArticleEntity> findTop10ByIsDeletedFalseAndBoardIdAndIdLessThanOrderByCreatedAtDesc(Long boardId, Long lastArticleId);

    List<ArticleEntity> findTop10ByIsDeletedFalseAndBoardIdAndIdGreaterThanOrderByCreatedAtDesc(Long boardId, Long firstArticleId);

    default ArticleEntity findByIsDeletedFalseAndIdAndAuthorIdOrThrow(Long articleId, UUID userId) {
        return findByIsDeletedFalseAndIdAndAuthorId(articleId, userId).orElseThrow(() -> new RuntimeException("Article not found"));
    }
}
