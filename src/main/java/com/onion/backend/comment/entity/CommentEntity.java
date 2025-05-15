package com.onion.backend.comment.entity;


import static jakarta.persistence.FetchType.LAZY;

import com.onion.backend.article.entity.ArticleEntity;
import com.onion.backend.comment.dto.CommentEditRequest;
import com.onion.backend.common.entity.BaseEntity;
import com.onion.backend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "comment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "article_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ArticleEntity article;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public void edit(CommentEditRequest request) {
        if (request.getContent() != null) {
            this.content = request.getContent();
        }
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
