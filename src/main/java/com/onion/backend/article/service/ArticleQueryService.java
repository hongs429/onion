package com.onion.backend.article.service;


import com.onion.backend.article.dto.ArticleResponse;
import com.onion.backend.article.dto.ArticleWithCommentResponse;
import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.article.event.EditArticleEvent;
import com.onion.backend.article.mapper.ArticleMapper;
import com.onion.backend.article.repository.ArticleRepository;
import com.onion.backend.board.repository.BoardRepository;
import com.onion.backend.comment.dto.CommentResponse;
import com.onion.backend.common.domain.Author;
import com.onion.backend.config.ElasticSearchProperties;
import com.onion.backend.infrastructure.elasticsearch.ElasticSearchService;
import com.onion.backend.infrastructure.elasticsearch.domain.SearchResult;
import com.onion.backend.infrastructure.elasticsearch.domain.SearchResult.SearchHit;
import com.onion.backend.user.domain.UserDetailsImpl;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ArticleQueryService {

    private final ElasticSearchProperties elasticSearchProperties;
    private final ElasticSearchService elasticSearchService;
    private final ArticleCommandService articleCommandService;
    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;

    private final ApplicationEventPublisher publisher;

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
            result = articleRepository.findTop10ByIsDeletedFalseAndBoardIdAndIdLessThanOrderByCreatedAtDesc(boardId,
                    lastId);
        } else if (firstId > 0) {
            result = articleRepository.findTop10ByIsDeletedFalseAndBoardIdAndIdGreaterThanOrderByCreatedAtDesc(boardId,
                    firstId);
        } else {
            result = articleRepository.findAllByIsDeletedFalseAndBoardIdOrderByCreatedAtDesc(boardId);
        }

        return result.stream()
                .map(article -> ArticleResponse.builder()
                        .articleId(article.getId())
                        .title(article.getTitle())
                        .viewCount(article.getViewCount())
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

        articleCommandService.increaseViewCount(articleId);

        return ArticleWithCommentResponse.builder()
                .articleId(articleWithComments.getId())
                .title(articleWithComments.getTitle())
                .content(articleWithComments.getContent())
                .viewCount(articleWithComments.getViewCount())
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

    /**
     * 데이터 정합성 - 항상 MySQL의 최신 데이터를 보여줌 - refresh 지연 문제 없음 성능 - Elasticsearch는 검색에만 집중 - 필요한 필드만 조회하여 네트워크 부하 감소 유연성 -
     * MySQL의 관계 데이터도 함께 조회 가능 - 추가 데이터 조회가 용이 유지보수 - 각 시스템의 역할이 명확 - 데이터 동기화 문제 감소 결론 - 이렇게 하면 Elasticsearch의 강점(검색)과
     * MySQL의 강점(데이터 정합성)을 모두 활용
     */
    public Page<ArticleResponse> searchArticle(Long boardId, String keyword, Pageable pageable) {
        SearchResult searchResult
                = elasticSearchService.searchByKeyword(
                elasticSearchProperties.getArticleIndex(),
                boardId, keyword, pageable);

        List<ArticleEntity> articles = articleRepository.findAllByIdIn(searchResult.getHits().stream()
                .map(SearchHit::getId)
                .map(Long::parseLong)
                .toList()
        );

        List<ArticleResponse> responses = articles.stream()
                .map(ArticleMapper::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(
                responses,
                pageable,
                searchResult.getTotal()
        );
    }


    private void isExistsBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found");
        }
    }

    private void publishEditArticleEvent(UserDetailsImpl user, ArticleEntity savedArticle) {
        Author author = Author.of(user.getUserId(), user.getUsername(), user.getEmail());

        ArticleResponse articleResponse = ArticleMapper.toResponse(savedArticle);

        publisher.publishEvent(new EditArticleEvent(author, articleResponse));
    }
}
