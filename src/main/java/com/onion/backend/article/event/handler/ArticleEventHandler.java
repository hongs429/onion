package com.onion.backend.article.event.handler;


import com.onion.backend.article.entity.ArticleDocument;
import com.onion.backend.article.event.EditArticleEvent;
import com.onion.backend.article.event.WriteArticleEvent;
import com.onion.backend.article.mapper.ArticleMapper;
import com.onion.backend.config.ElasticSearchProperties;
import com.onion.backend.infrastructure.elasticsearch.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ArticleEventHandler {

    private final ElasticSearchService elasticSearchService;
    private final ElasticSearchProperties elasticSearchProperties;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWriteArticleEvent(WriteArticleEvent event) {

        ArticleDocument document = ArticleMapper.toDocument(event.getAuthor(), event.getArticle());
        elasticSearchService.indexDocument(elasticSearchProperties.getArticleIndex(), event.getArticle().getArticleId().toString(), document);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEditArticleEvent(EditArticleEvent event) {

        ArticleDocument document = ArticleMapper.toDocument(event.getAuthor(), event.getArticle());
        elasticSearchService.indexDocument(elasticSearchProperties.getArticleIndex(), event.getArticle().getArticleId().toString(), document);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleIncreaseViewCountEvent(Long articleId) {
        elasticSearchService.indexDocument(elasticSearchProperties.getArticleIndex(), articleId.toString(), null);
    }
}
