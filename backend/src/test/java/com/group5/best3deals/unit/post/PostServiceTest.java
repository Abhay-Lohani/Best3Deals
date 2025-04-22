package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.dto.request.PostPutRequest;
import com.group5.best3deals.post.dto.request.PostRequest;
import com.group5.best3deals.post.dto.response.PostResponse;
import com.group5.best3deals.post.entity.Post;
import com.group5.best3deals.post.repository.PostRepository;
import com.group5.best3deals.post.service.PostService;
import com.group5.best3deals.post.service.ReactionService;
import com.group5.best3deals.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PostRequest postRequest;
    private PostPutRequest postPutRequest;

    @BeforeEach
    void setUp() {
        final LocalDateTime endDate = LocalDateTime.now();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .imgUrl("http://image.com")
                .originalPrice(100.0f)
                .discountedPrice(80.0f)
                .isValid(true)
                .userId(1L)
                .storeId(1L)
                .endDate(endDate)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        postRequest = new PostRequest(
                "Test Title",
                "Test Content",
                "http://image.com",
                100.0f,
                80.0f,
                1L,
                endDate
                );

        postPutRequest = new PostPutRequest ();
        postPutRequest.setTitle ("Test Title Modified");
        postPutRequest.setOriginalPrice(150.0f);
        postPutRequest.setStoreId(2L);
    }

    // Test get methods
    @Test
    void testGetPostById() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(reactionService.getAllByPostId(any())).thenReturn(new ArrayList<>());

        PostResponse returnedPost = postService.getPostById(post.getId());

        assertNotNull(returnedPost);
        assertEquals(post.getId(), returnedPost.getId());
        assertEquals(post.getTitle(), returnedPost.getTitle());
        assertEquals(post.getContent(), returnedPost.getContent());
        assertEquals(post.getImgUrl(), returnedPost.getImgUrl());
        assertEquals(post.getOriginalPrice(), returnedPost.getOriginalPrice());
        assertEquals(post.getDiscountedPrice(), returnedPost.getDiscountedPrice());
        assertEquals(post.getIsValid(), returnedPost.getIsValid());
        assertEquals(post.getUserId(), returnedPost.getUserId());
        assertEquals(post.getStoreId(), returnedPost.getStoreId());
        assertEquals(post.getEndDate(), returnedPost.getEndDate());
        assertEquals(post.getCreatedAt(), returnedPost.getCreatedAt());
    }

    @Test
    void testGetAllPosts() {
        when(postRepository.findAll()).thenReturn(List.of(post));

        List<Post> foundPosts = postService.getAllPosts();

        assertNotNull(foundPosts);
        assertEquals(1, foundPosts.size());
        assertEquals(post, foundPosts.get(0));
    }

    @Test
    void testGetAllByUserId() {
        when(postRepository.findAllByUserId(post.getUserId())).thenReturn(List.of(post));

        List<Post> foundPosts = postService.getAllByUserId(post.getUserId());

        assertNotNull(foundPosts);
        assertEquals(1, foundPosts.size());
        assertEquals(post, foundPosts.get(0));
    }

    @Test
    void testGetAllByStoreId() {
        when(postRepository.findAllByStoreId(post.getStoreId())).thenReturn(List.of(post));

        List<Post> foundPosts = postService.getAllByStoreId(post.getStoreId());

        assertNotNull(foundPosts);
        assertEquals(1, foundPosts.size());
        assertEquals(post, foundPosts.get(0));
    }

    // Test createPost
    @Test
    void testCreatePost_Success() {
        when(storeRepository.existsById(postRequest.getStoreId())).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(reactionService.getAllByPostId(any())).thenReturn(new ArrayList<>());

        PostResponse createdPost = postService.createPost(postRequest, post.getUserId());

        assertNotNull(createdPost);
        assertEquals(postRequest.getTitle(), createdPost.getTitle());
        assertEquals(postRequest.getContent(), createdPost.getContent());
        assertEquals(postRequest.getImgUrl(), createdPost.getImgUrl());
        assertEquals(postRequest.getOriginalPrice(), createdPost.getOriginalPrice());
        assertEquals(postRequest.getDiscountedPrice(), createdPost.getDiscountedPrice());
        assertEquals(post.getIsValid(), createdPost.getIsValid());
        assertEquals(post.getUserId(), createdPost.getStoreId());
        assertEquals(postRequest.getStoreId(), createdPost.getStoreId());
        assertEquals(post.getEndDate(), createdPost.getEndDate());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_StoreNotFound() {
        when(storeRepository.existsById(postRequest.getStoreId())).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            postService.createPost(postRequest, 1L);
        });
        assertEquals("Store not found with " + postRequest.getStoreId(), exception.getMessage());
    }

    // Test updatePost
    @Test
    void testUpdatePost_Success() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(storeRepository.existsById(postPutRequest.getStoreId())).thenReturn(true);
        when(reactionService.getAllByPostId(any())).thenReturn(new ArrayList<>());

        PostResponse updatedPost = postService.updatePost(post.getId(), postPutRequest, post.getUserId());

        assertNotNull(updatedPost);
        assertEquals(postPutRequest.getTitle(), updatedPost.getTitle());
        assertEquals(post.getContent(), updatedPost.getContent());
        assertEquals(postPutRequest.getOriginalPrice(), updatedPost.getOriginalPrice());
        assertEquals(post.getDiscountedPrice(), updatedPost.getDiscountedPrice());
        assertEquals(post.getUserId(), updatedPost.getUserId());
        assertEquals(postPutRequest.getStoreId(), updatedPost.getStoreId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePost_UserMismatch() {
        final Long differentUserId = 2L;

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.updatePost(post.getId(), postPutRequest, differentUserId);
        });
        assertEquals("User can only update the post they have made", exception.getMessage());
    }

    @Test
    void testUpdatePost_StoreNotFound() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(storeRepository.existsById(postPutRequest.getStoreId())).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            postService.updatePost(post.getId(), postPutRequest, post.getUserId());
        });
        assertEquals("Store not found with " + postPutRequest.getStoreId(), exception.getMessage());
    }

    // Test deletePost
    @Test
    void testDeletePost_Success() {
        when(postRepository.existsById(post.getId())).thenReturn(true);

        postService.deletePostById(post.getId());

        verify(postRepository, times(1)).deleteById(post.getId());
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(postRepository.existsById(post.getId())).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            postService.deletePostById(post.getId());
        });

        assertEquals("Post not found with " + post.getId(), exception.getMessage());
    }
}
