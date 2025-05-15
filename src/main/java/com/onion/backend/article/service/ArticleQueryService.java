package com.onion.backend.article.service;


import com.onion.backend.article.dto.ArticleResponse;
import com.onion.backend.article.dto.ArticleWithCommentResponse;
import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.article.repository.ArticleRepository;
import com.onion.backend.board.repository.BoardRepository;
import com.onion.backend.comment.dto.CommentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ArticleQueryService {

    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;

    public List<ArticleResponse> getArticlesV1(Long boardId) {

        isExistsBoard(boardId);

        return articleRepository.findAllByIsDeletedFalseAndBoardIdOrderByCreatedAtDesc(boardId).stream()
                .map(article -> ArticleResponse.builder()
                        .articleId(article.getId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .createdAt(article.getCreatedAt())
                        .updatedAt(article.getUpdatedAt())
                        .authorId(article.getAuthor().getId())
                        .boardId(article.getBoard().getId())
                        .build())
                .toList();
    }


    public List<ArticleResponse> getArticlesV2(Long boardId, Long lastId, Long firstId) {

        isExistsBoard(boardId);

        List<ArticleEntity> result;

        if (lastId > 0) {
            result = articleRepository.findTop10ByIsDeletedFalseAndBoardIdAndIdLessThanOrderByCreatedAtDesc(boardId, lastId);
        } else if (firstId > 0) {
            result = articleRepository.findTop10ByIsDeletedFalseAndBoardIdAndIdGreaterThanOrderByCreatedAtDesc(boardId, firstId);
        } else {
            result = articleRepository.findAllByIsDeletedFalseAndBoardIdOrderByCreatedAtDesc(boardId);
        }

        return result.stream()
                .map(article -> ArticleResponse.builder()
                        .articleId(article.getId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .createdAt(article.getCreatedAt())
                        .updatedAt(article.getUpdatedAt())
                        .authorId(article.getAuthor().getId())
                        .boardId(article.getBoard().getId())
                        .build())
                .toList();
    }


    public ArticleWithCommentResponse getArticleWithComments(Long boardId, Long articleId) {
        isExistsBoard(boardId);

        ArticleEntity articleWithComments = articleRepository.findArticleWithComments(articleId).orElseThrow(
                () -> new RuntimeException("Article not found")
        );

        return ArticleWithCommentResponse.builder()
                .articleId(articleWithComments.getId())
                .title(articleWithComments.getTitle())
                .content(articleWithComments.getContent())
                .createdAt(articleWithComments.getCreatedAt())
                .updatedAt(articleWithComments.getUpdatedAt())
                .comments(articleWithComments.getComments().stream().map(
                        comment -> CommentResponse.builder()
                                .commentId(comment.getId())
                                .content(comment.getContent())
                                .authorId(comment.getAuthor().getId())
                                .articleId(comment.getArticle().getId())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                        .build()).toList())
                .build();
    }

    private void isExistsBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }
    }
}
