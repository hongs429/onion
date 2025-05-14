package com.onion.backend.article.service;


import com.onion.backend.article.dto.ArticleCreateRequest;
import com.onion.backend.article.dto.ArticleEditRequest;
import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.article.repository.ArticleRepository;
import com.onion.backend.board.entity.BoardEntity;
import com.onion.backend.board.repository.BoardRepository;
import com.onion.backend.user.entity.UserEntity;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class ArticleCommandService {

    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;


    public void writeArticle(Long boardId, UUID userId, ArticleCreateRequest request) {
        BoardEntity board = boardRepository.findByIdOrThrow(boardId);

        ArticleEntity article = ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .board(board)
                .author(UserEntity.getUserEntity(userId))
                .build();

        articleRepository.save(article);
    }

    public void editArticle(Long boardId, Long articleId, UUID userId, ArticleEditRequest request) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        ArticleEntity article = articleRepository.findByIsDeletedFalseAndIdAndAuthorIdOrThrow(articleId, userId);

        article.edit(request);

        articleRepository.save(article);
    }

    public void deleteArticleSoftly(Long boardId, Long articleId, UUID userId) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        ArticleEntity article = articleRepository.findByIsDeletedFalseAndIdAndAuthorIdOrThrow(articleId, userId);

        article.softDelete();
        articleRepository.save(article);
    }
}
