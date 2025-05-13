package com.onion.backend.article.service;


import com.onion.backend.article.dto.ArticleCreateRequest;
import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.article.repository.ArticleRepository;
import com.onion.backend.board.entity.BoardEntity;
import com.onion.backend.board.repository.BoardRepository;
import com.onion.backend.user.entity.UserEntity;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
