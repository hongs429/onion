package com.onion.backend.article.repository;

import com.onion.backend.article.entity.ArticleEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {
}
