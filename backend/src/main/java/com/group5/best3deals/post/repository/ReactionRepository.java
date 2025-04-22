package com.group5.best3deals.post.repository;

import com.group5.best3deals.post.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findAllByPostId(Long postId);
}
