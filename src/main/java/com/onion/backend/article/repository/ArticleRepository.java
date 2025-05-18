package com.onion.backend.article.repository;

import com.onion.backend.article.entity.ArticleEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    @Query("update article a set a.viewCount = a.viewCount + 1 where a.id = :articleId")
    @Modifying
    void increaseViewCount(@Param("articleId") Long articleId);

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

    default ArticleEntity findByIdOrThrow(Long articleId) {
        return findById(articleId).orElseThrow(() -> new RuntimeException("Article not found"));
    }

    List<ArticleEntity> findAllByIdIn(Collection<Long> ids);
}
