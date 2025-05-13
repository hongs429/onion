package com.onion.backend.article.controller;


import com.onion.backend.article.dto.ArticleCreateRequest;
import com.onion.backend.article.service.ArticleCommandService;
import com.onion.backend.user.domain.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ArticleController {


    private final ArticleCommandService articleCommandService;


    @PostMapping("/boards/{boardId}/articles")
    public ResponseEntity<Void> writeArticle(
            @PathVariable Long boardId,
            @RequestBody ArticleCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        articleCommandService.writeArticle(boardId, userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }
}
