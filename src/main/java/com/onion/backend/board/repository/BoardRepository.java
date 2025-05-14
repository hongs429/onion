package com.onion.backend.board.repository;

import com.onion.backend.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    boolean existsById(Long boardId);

    default BoardEntity findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시판")
        );
    }
}
