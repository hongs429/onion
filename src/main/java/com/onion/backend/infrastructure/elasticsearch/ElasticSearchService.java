package com.onion.backend.infrastructure.elasticsearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.onion.backend.article.entity.ArticleDocument;
import com.onion.backend.infrastructure.elasticsearch.domain.SearchResult;
import com.onion.backend.infrastructure.elasticsearch.domain.SearchResult.SearchHit;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchService {
    private final ElasticsearchClient client;

    @Async("elasticsearch-async")
    public <T> void indexDocument(String index, String id, T documentEntity) {
        try {
            IndexResponse response = client.index(builder -> builder
                    .index(index)
                    .id(id)
                    .document(documentEntity)
            );
            log.info("Indexed document with id: {}", response.id());
        } catch (IOException e) {
            log.error("Error indexing document", e);
            throw new RuntimeException("Error indexing document", e);
        }
    }

    public SearchResult searchByKeyword(String index, Long boardId, String keyword, Pageable pageable) {
        try {
            SearchResponse<ArticleDocument> response = client.search(s -> s
                            .index(index)
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize())
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .multiMatch(mm -> mm
                                                            .fields("title^3", "content")
                                                            .query(keyword)
                                                    )
                                            )
                                            .filter(f -> f
                                                    .term(t -> t
                                                            .field("boardId")
                                                            .value(boardId)
                                                    )
                                            )
                                            .filter(f -> f
                                                    .term(t -> t
                                                            .field("deleted")
                                                            .value(false)
                                                    )
                                            )
                                    )
                            )
                            .source(sourceBuilder -> sourceBuilder
                                    .filter(filterBuilder -> filterBuilder
                                            .excludes("*")
                                    )),
                    ArticleDocument.class
            );

            return new SearchResult(
                    response.hits().hits().stream()
                            .map(hit -> new SearchHit(hit.id(), hit.score()))
                            .collect(Collectors.toList()),
                    response.hits().total().value()
            );

        } catch (IOException e) {
            log.error("Error searching articles", e);
            throw new RuntimeException("Error searching articles", e);
        }
    }
}
