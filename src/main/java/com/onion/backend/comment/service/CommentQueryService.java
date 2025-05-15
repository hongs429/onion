package com.onion.backend.comment.service;


import com.onion.backend.comment.dto.CommentResponse;
import com.onion.backend.comment.entity.CommentEntity;
import com.onion.backend.comment.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;

    public List<CommentResponse> getCommentsIsDeletedFalse(Long articleId) {
        List<CommentEntity> comments = commentRepository.findByArticleIdAndIsDeletedIsFalse(articleId);

        return comments.stream()
                .map(commentEntity -> CommentResponse.builder()
                        .commentId(commentEntity.getId())
                        .content(commentEntity.getContent())
                        .authorId(commentEntity.getAuthor().getId())
                        .articleId(commentEntity.getArticle().getId())
                        .createdAt(commentEntity.getCreatedAt())
                        .updatedAt(commentEntity.getUpdatedAt())
                        .build())
                .toList();
    }
}

