package com.onion.backend.comment.controller;


import com.onion.backend.comment.dto.CommentEditRequest;
import com.onion.backend.comment.dto.CommentResponse;
import com.onion.backend.comment.dto.CommentWriteRequest;
import com.onion.backend.comment.service.CommentCommandService;
import com.onion.backend.comment.service.CommentQueryService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @PostMapping("/boards/{boardId}/articles/{articleId}/comments")
    public ResponseEntity<Void> writeComment(
            @PathVariable Long boardId,
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentWriteRequest request
    ) {

        commentCommandService.writeComment(boardId, articleId, userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/boards/{boardId}/articles/{articleId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long boardId,
            @PathVariable Long articleId
    ) {
        List<CommentResponse> result = commentQueryService.getCommentsIsDeletedFalse(articleId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/boards/{boardId}/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> editComment(
            @PathVariable Long boardId,
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentEditRequest request
            ) {

        commentCommandService.editComment(boardId, articleId, commentId, userDetails.getUserId(), request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/boards/{boardId}/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        commentCommandService.deleteComment(boardId, articleId, commentId, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }
}
