package com.group5.best3deals.post.service;

import com.group5.best3deals.post.dto.request.PostPutRequest;
import com.group5.best3deals.post.dto.request.PostRequest;
import com.group5.best3deals.post.dto.response.PostResponse;
import com.group5.best3deals.post.entity.Post;
import com.group5.best3deals.post.repository.PostRepository;
import com.group5.best3deals.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final StoreRepository storeRepository;
    private final ReactionService reactionService;

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Post not found with " + id));

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImgUrl(),
                post.getOriginalPrice(),
                post.getDiscountedPrice(),
                post.getIsValid(),
                post.getUserId(),
                post.getStoreId(),
                post.getEndDate(),
                post.getCreatedAt(),
                reactionService.getAllByPostId(id)
        );
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Post> getAllByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllByStoreId(Long storeId) {
        return postRepository.findAllByStoreId(storeId);
    }

    @Transactional
    public PostResponse createPost(PostRequest postRequest, Long userId) {
        if (!storeRepository.existsById(postRequest.getStoreId())) {
            throw new NoSuchElementException("Store not found with " + postRequest.getStoreId());
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .imgUrl(postRequest.getImgUrl())
                .discountedPrice(postRequest.getDiscountedPrice())
                .originalPrice(postRequest.getOriginalPrice())
                .isValid(true)
                .storeId(postRequest.getStoreId())
                .userId(userId)
                .endDate(postRequest.getEndDate())
                .build();

        postRepository.save(post);

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImgUrl(),
                post.getOriginalPrice(),
                post.getDiscountedPrice(),
                post.getIsValid(),
                post.getUserId(),
                post.getStoreId(),
                post.getEndDate(),
                post.getCreatedAt(),
                reactionService.getAllByPostId(post.getId()) // Always an empty array
        );
    }

    @Transactional
    public PostResponse updatePost(Long id, PostPutRequest postPutRequest, Long userId) {
        PostResponse existingPost = getPostById(id);

        if (!userId.equals(existingPost.getUserId())) {
            throw new IllegalArgumentException("User can only update the post they have made");
        }

        if (!storeRepository.existsById(postPutRequest.getStoreId())) {
            throw new NoSuchElementException("Store not found with " + postPutRequest.getStoreId());
        }

        Post updatedPost = Post.builder()
                .id(existingPost.getId())
                .title(postPutRequest.getTitle() != null ? postPutRequest.getTitle() : existingPost.getTitle())
                .content(postPutRequest.getContent() != null ? postPutRequest.getContent() : existingPost.getContent())
                .imgUrl(postPutRequest.getImgUrl() != null ? postPutRequest.getImgUrl() : existingPost.getImgUrl())
                .originalPrice(postPutRequest.getOriginalPrice() != null ? postPutRequest.getOriginalPrice() : existingPost.getOriginalPrice())
                .discountedPrice(postPutRequest.getDiscountedPrice() != null ? postPutRequest.getDiscountedPrice() : existingPost.getDiscountedPrice())
                .isValid(postPutRequest.getIsValid() != null ? postPutRequest.getIsValid() : existingPost.getIsValid())
                .userId(existingPost.getUserId())
                .storeId(postPutRequest.getStoreId() != null ? postPutRequest.getStoreId() : existingPost.getStoreId())
                .endDate(postPutRequest.getEndDate() != null ? postPutRequest.getEndDate() : existingPost.getEndDate())
                .createdAt(existingPost.getCreatedAt())
                .build();

        postRepository.save(updatedPost);

        return new PostResponse(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                updatedPost.getImgUrl(),
                updatedPost.getOriginalPrice(),
                updatedPost.getDiscountedPrice(),
                updatedPost.getIsValid(),
                updatedPost.getUserId(),
                updatedPost.getStoreId(),
                updatedPost.getEndDate(),
                updatedPost.getCreatedAt(),
                reactionService.getAllByPostId(updatedPost.getId())
        );
    }

    @Transactional
    public void deletePostById(Long id) {
        if (!postRepository.existsById(id)) {
            throw new NoSuchElementException("Post not found with " + id);
        }

        postRepository.deleteById(id);
    }
}
