package com.onion.backend.article.controller;


import com.onion.backend.article.dto.ArticleCreateRequest;
import com.onion.backend.article.dto.ArticleEditRequest;
import com.onion.backend.article.dto.ArticleResponse;
import com.onion.backend.article.dto.ArticleWithCommentResponse;
import com.onion.backend.article.service.ArticleCommandService;
import com.onion.backend.article.service.ArticleQueryService;
import com.onion.backend.user.domain.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ArticleController {

    private final ArticleQueryService articleQueryService;
    private final ArticleCommandService articleCommandService;


    @PostMapping("/boards/{boardId}/articles")
    public ResponseEntity<Void> writeArticle(
            @PathVariable Long boardId,
            @RequestBody ArticleCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        articleCommandService.writeArticle(boardId, userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/boards/{boardId}/articles")
//    public ResponseEntity<List<ArticleResponse>> getArticlesV1(@PathVariable Long boardId) {
//        List<ArticleResponse> result = articleQueryService.getArticlesV1(boardId);
//
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("/boards/{boardId}/articles")
    public ResponseEntity<List<ArticleResponse>> getArticlesV2(
            @PathVariable Long boardId,
            @RequestParam(required = false, defaultValue = "0") Long lastId,
            @RequestParam(required = false, defaultValue = "0") Long firstId) {

        return ResponseEntity.ok(articleQueryService.getArticlesV2(boardId, lastId, firstId));
    }

    @GetMapping("/boards/{boardId}/articles/{articleId}")
    public ResponseEntity<ArticleWithCommentResponse> getArticleWithComments(
            @PathVariable Long boardId,
            @PathVariable Long articleId
    ) {
        ArticleWithCommentResponse result = articleQueryService.getArticleWithComments(boardId, articleId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/boards/{boardId}/articles/{articleId}")
    public ResponseEntity<Void> updateArticle(
            @PathVariable Long boardId,
            @PathVariable Long articleId,
            @RequestBody ArticleEditRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        articleCommandService.editArticle(boardId, articleId, userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/boards/{boardId}/articles/{articleId}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable Long boardId,
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        articleCommandService.deleteArticleSoftly(boardId, articleId, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }


}
