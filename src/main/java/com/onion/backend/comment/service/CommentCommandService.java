package com.onion.backend.comment.service;


import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.article.repository.ArticleRepository;
import com.onion.backend.board.repository.BoardRepository;
import com.onion.backend.comment.dto.CommentEditRequest;
import com.onion.backend.comment.dto.CommentWriteRequest;
import com.onion.backend.comment.entity.CommentEntity;
import com.onion.backend.comment.repository.CommentRepository;
import com.onion.backend.user.entity.UserEntity;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public void writeComment(Long boardId, Long articleId, UUID userId, CommentWriteRequest request) {

        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        if (articleRepository.isNotExistByIdAndIsDeletedFalse(articleId)) {
            throw new RuntimeException("Article not found");
        }

        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .author(UserEntity.getUserEntity(userId))
                .article(ArticleEntity.getArticleEntity(articleId))
                .build();

        commentRepository.save(comment);
    }

    public void editComment(Long boardId, Long articleId, Long commentId, UUID userId, CommentEditRequest request) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        if (articleRepository.isNotExistByIdAndIsDeletedFalse(articleId)) {
            throw new RuntimeException("Article not found");
        }

        CommentEntity comment = commentRepository.findByIdAndAuthorIdAndIsDeletedIsFalseOrThrow(
                commentId, userId);

        comment.edit(request);

        commentRepository.save(comment);
    }


    public void deleteComment(Long boardId, Long articleId, Long commentId, UUID userId) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        if (articleRepository.isNotExistByIdAndIsDeletedFalse(articleId)) {
            throw new RuntimeException("Article not found");
        }

        CommentEntity comment = commentRepository.findByIdAndAuthorIdAndIsDeletedIsFalseOrThrow(commentId, userId);

        comment.softDelete();

        commentRepository.save(comment);
    }
}
