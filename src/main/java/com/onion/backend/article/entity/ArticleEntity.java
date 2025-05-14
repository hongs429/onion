package com.onion.backend.article.entity;


import static jakarta.persistence.FetchType.LAZY;

import com.onion.backend.article.dto.ArticleEditRequest;
import com.onion.backend.board.entity.BoardEntity;
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

@Entity(name = "article")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BoardEntity board;

    public void edit(ArticleEditRequest request) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getContent() != null) {
            this.content = request.getContent();
        }
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
