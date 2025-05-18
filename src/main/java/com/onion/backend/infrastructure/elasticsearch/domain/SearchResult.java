package com.onion.backend.infrastructure.elasticsearch.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResult {
    private List<SearchHit> hits;
    private long total;

    @Getter
    @AllArgsConstructor
    public static class SearchHit {
        private String id;
        private double score;
    }
}
