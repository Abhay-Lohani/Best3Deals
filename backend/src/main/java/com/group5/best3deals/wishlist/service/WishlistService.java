package com.group5.best3deals.wishlist.service;

import com.group5.best3deals.product.service.ProductService;
import com.group5.best3deals.wishlist.dto.request.WishlistRequest;
import com.group5.best3deals.wishlist.dto.response.WishlistResponse;
import com.group5.best3deals.wishlist.entity.Wishlist;
import com.group5.best3deals.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<Wishlist> getAllWishlistByUserId(Long userId) {
        return wishlistRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Wishlist getWishlistByUserIdAndProductId(Long userId, Long productId) {
        return wishlistRepository.findByUserIdAndStoreProductId(userId, productId)
                .orElseThrow(() -> new NoSuchElementException("Wishlist not found with product id " + productId));
    }

    @Transactional
    public WishlistResponse addToWishlist(Long userId, WishlistRequest wishlistRequest) {
        if (productService.getProductById(wishlistRequest.getStoreProductId()) == null) {
            throw new NoSuchElementException("Product not found with " + wishlistRequest.getStoreProductId());
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .storeProductId(wishlistRequest.getStoreProductId())
                .build();

        wishlist = wishlistRepository.save(wishlist);

        return new WishlistResponse(wishlist.getId(), wishlist.getStoreProductId(), wishlist.getCreatedAt());
    }

    @Transactional
    public void deleteFromWishlist(Long userId, Long productId) {
        Wishlist existingWishlist = getWishlistByUserIdAndProductId(userId, productId);

        if (existingWishlist == null) {
            throw new NoSuchElementException("Wishlist not found with product id " + productId);
        }

        wishlistRepository.deleteById(existingWishlist.getId());
    }
}
