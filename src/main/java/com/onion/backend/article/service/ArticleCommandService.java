package com.onion.backend.article.service;


import com.onion.backend.article.dto.ArticleCreateRequest;
import com.onion.backend.article.dto.ArticleEditRequest;
import com.onion.backend.article.dto.ArticleResponse;
import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.article.event.EditArticleEvent;
import com.onion.backend.article.event.WriteArticleEvent;
import com.onion.backend.article.mapper.ArticleMapper;
import com.onion.backend.article.repository.ArticleRepository;
import com.onion.backend.board.entity.BoardEntity;
import com.onion.backend.board.repository.BoardRepository;
import com.onion.backend.common.domain.Author;
import com.onion.backend.user.domain.UserDetailsImpl;
import com.onion.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ArticleCommandService {

    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final ApplicationEventPublisher publisher;

    public void writeArticle(Long boardId, UserDetailsImpl user, ArticleCreateRequest request) {
        BoardEntity board = boardRepository.findByIdOrThrow(boardId);

        ArticleEntity article = ArticleEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .board(board)
                .author(UserEntity.getUserEntity(user.getUserId()))
                .build();

        ArticleEntity savedArticle = articleRepository.save(article);

        publishWriteArticleEvent(user, savedArticle);
    }

    public void editArticle(Long boardId, Long articleId, UserDetailsImpl user, ArticleEditRequest request) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        ArticleEntity article = articleRepository.findByIsDeletedFalseAndIdAndAuthorIdOrThrow(articleId, user.getUserId());

        article.edit(request);

        ArticleEntity savedArticle = articleRepository.save(article);

        publishEditArticleEvent(user, savedArticle);
    }


    public void deleteArticleSoftly(Long boardId, Long articleId, UserDetailsImpl user) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }

        ArticleEntity article = articleRepository.findByIsDeletedFalseAndIdAndAuthorIdOrThrow(articleId,
                user.getUserId());

        article.softDelete();
        ArticleEntity savedArticle = articleRepository.save(article);

        publishEditArticleEvent(user, savedArticle);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseViewCount(Long articleId) {
        try {
            articleRepository.increaseViewCount(articleId);

            publishIncreaseViewCountEvent(articleId);
        } catch (Exception e) {
            log.error("Error in increaseViewCount: {}", e.getMessage());
        }
    }

    private void publishIncreaseViewCountEvent(Long articleId) {
        ArticleEntity article = articleRepository.findByIdOrThrow(articleId);

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        publishEditArticleEvent(userDetails, article);
    }

    private void publishEditArticleEvent(UserDetailsImpl user, ArticleEntity savedArticle) {
        Author author = Author.of(user.getUserId(), user.getUsername(), user.getEmail());

        ArticleResponse articleResponse = ArticleMapper.toResponse(savedArticle);

        publisher.publishEvent(new EditArticleEvent(author, articleResponse));
    }

    private void publishWriteArticleEvent(UserDetailsImpl user, ArticleEntity savedArticle) {
        Author author = Author.of(user.getUserId(), user.getUsername(), user.getEmail());

        ArticleResponse articleResponse = ArticleMapper.toResponse(savedArticle);

        publisher.publishEvent(new WriteArticleEvent(author, articleResponse));
    }
}
