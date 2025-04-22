package com.group5.best3deals.wishlist.repository;

import com.group5.best3deals.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByUserId(Long userId);
    Optional<Wishlist> findByUserIdAndStoreProductId(Long userId, Long productId);
}
