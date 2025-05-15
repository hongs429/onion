package com.onion.backend.article.repository;

import com.onion.backend.article.entity.ArticleEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    boolean existsByIdAndIsDeletedFalse(Long articleId);

    Optional<ArticleEntity> findByIsDeletedFalseAndIdAndAuthorId(Long articleId, UUID authorId);

    List<ArticleEntity> findAllByIsDeletedFalseAndBoardIdOrderByCreatedAtDesc(Long boardId);

    List<ArticleEntity> findTop10ByIsDeletedFalseAndBoardIdAndIdLessThanOrderByCreatedAtDesc(Long boardId, Long lastArticleId);

    List<ArticleEntity> findTop10ByIsDeletedFalseAndBoardIdAndIdGreaterThanOrderByCreatedAtDesc(Long boardId, Long firstArticleId);

    @Query("select a from article a "
            + "left join fetch a.comments c "
            + "where a.isDeleted = false "
            + "and a.id = :articleId "
            + "and (c.isDeleted = false or c is null)")
    Optional<ArticleEntity> findArticleWithComments(Long articleId);

    default ArticleEntity findByIsDeletedFalseAndIdAndAuthorIdOrThrow(Long articleId, UUID userId) {
        return findByIsDeletedFalseAndIdAndAuthorId(articleId, userId).orElseThrow(() -> new RuntimeException("Article not found"));
    }

    default boolean isNotExistByIdAndIsDeletedFalse(Long articleId) {
        return !existsByIdAndIsDeletedFalse(articleId);
    }
}
