package com.group5.best3deals.post.repository;

import com.group5.best3deals.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);
    List<Post> findAllByStoreId(Long storeId);
}
