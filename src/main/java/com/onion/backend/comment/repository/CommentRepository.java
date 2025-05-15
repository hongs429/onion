package com.onion.backend.comment.repository;

import com.onion.backend.comment.entity.CommentEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByArticleIdAndIsDeletedIsFalse(Long articleId);

    Optional<CommentEntity> findByIdAndIsDeletedIsFalse(Long commentId);

    Optional<CommentEntity> findByIdAndAuthorIdAndIsDeletedIsFalse(Long commentId, UUID authorId);

    default CommentEntity findByIdAndIsDeletedIsFalseOrThrow(Long commentId) {
        return findByIdAndIsDeletedIsFalse(commentId).orElseThrow(
                () -> new IllegalStateException("Comment with id " + commentId + " not found")
        );
    }

    default CommentEntity findByIdAndAuthorIdAndIsDeletedIsFalseOrThrow(Long commentId, UUID authorId) {
        return findByIdAndAuthorIdAndIsDeletedIsFalse(commentId, authorId).orElseThrow(
                () -> new IllegalStateException("Comment with id " + commentId + " not found")
        );
    }
}
