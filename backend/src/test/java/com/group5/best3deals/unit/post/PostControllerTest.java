package com.group5.best3deals.unit.post;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.post.Enum.ReactionType;
import com.group5.best3deals.post.controller.PostController;
import com.group5.best3deals.post.dto.request.*;
import com.group5.best3deals.post.dto.response.*;
import com.group5.best3deals.post.entity.Comment;
import com.group5.best3deals.post.entity.Post;
import com.group5.best3deals.post.entity.Reaction;
import com.group5.best3deals.post.service.CommentService;
import com.group5.best3deals.post.service.PostService;
import com.group5.best3deals.post.service.ReactionService;
import com.group5.best3deals.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private ReactionService reactionService;

    @Mock
    private CommentService commentService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private PostController postController;

    private User mockUser;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        testDateTime = LocalDateTime.now().plusDays(7);
    }

    // Helper methods for creating test objects
    private PostRequest createValidPostRequest() {
        return new PostRequest(
                "Test Post Title",
                "Test Post Content",
                "test.jpg",
                100.0f,
                80.0f,
                1L,
                testDateTime
        );
    }

    private PostPutRequest createValidPostPutRequest() {
        PostPutRequest request = new PostPutRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");
        request.setImgUrl("updated.jpg");
        request.setOriginalPrice(90.0f);
        request.setDiscountedPrice(70.0f);
        request.setIsValid(true);
        request.setStoreId(1L);
        request.setEndDate(testDateTime);
        return request;
    }

    private ReactionRequest createValidReactionRequest() {
        return new ReactionRequest(ReactionType.LIKE, 1L);
    }

    private CommentRequest createValidCommentRequest() {
        return new CommentRequest("Test Comment Content", 1L);
    }

    private PostResponse createValidPostResponse(Long id) {
        return new PostResponse(
                id,
                "Response Title",
                "Response Content",
                "response.jpg",
                100.0f,
                80.0f,
                true,
                1L,
                1L,
                testDateTime,
                new Timestamp(System.currentTimeMillis()),
                Collections.emptyList()
        );
    }

    // Test methods




    @Test
    void getAllByUserId_ShouldReturnUserPosts() {
        List<Post> posts = Collections.singletonList(new Post());

        when(postService.getAllByUserId(mockUser.getId())).thenReturn(posts);

        ResponseEntity<ApiResponse<List<Post>>> response = postController.getAllByUserId();

        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(postService).getAllByUserId(mockUser.getId());
    }



    @Test
    void createPost_ShouldReturnCreatedPost() {
        PostRequest postRequest = createValidPostRequest();
        PostResponse postResponse = createValidPostResponse(1L);

        when(postService.createPost(postRequest, mockUser.getId())).thenReturn(postResponse);

        ResponseEntity<ApiResponse<PostResponse>> response = postController.createPost(postRequest);

        assertTrue(response.getBody().isSuccess());
        assertEquals(postResponse, response.getBody().getData());
        verify(postService).createPost(postRequest, mockUser.getId());
    }

    @Test
    void updatePost_ShouldReturnUpdatedPost() {
        Long postId = 1L;
        PostPutRequest postPutRequest = createValidPostPutRequest();
        PostResponse updatedResponse = createValidPostResponse(postId);

        when(postService.updatePost(postId, postPutRequest, mockUser.getId())).thenReturn(updatedResponse);

        ResponseEntity<ApiResponse<PostResponse>> response = postController.updatePost(postId, postPutRequest);

        assertTrue(response.getBody().isSuccess());
        assertEquals(updatedResponse, response.getBody().getData());
        verify(postService).updatePost(postId, postPutRequest, mockUser.getId());
    }

//

    @Test
    void addReaction_ShouldReturnReaction() {
        ReactionRequest reactionRequest = createValidReactionRequest();
        ReactionResponse reactionResponse = new ReactionResponse(1L, ReactionType.LIKE, 1L, 1L);

        when(reactionService.createReaction(reactionRequest, mockUser.getId())).thenReturn(reactionResponse);

        ResponseEntity<ApiResponse<ReactionResponse>> response = postController.addReaction(reactionRequest);

        assertTrue(response.getBody().isSuccess());
        assertEquals(reactionResponse, response.getBody().getData());
        verify(reactionService).createReaction(reactionRequest, mockUser.getId());
    }

    @Test
    void cancelReaction_ShouldReturnSuccess() {
        Long reactionId = 1L;
        doNothing().when(reactionService).deleteById(reactionId, mockUser.getId());

        ResponseEntity<ApiResponse<Void>> response = postController.cancelReaction(reactionId);

        assertTrue(response.getBody().isSuccess());
        verify(reactionService).deleteById(reactionId, mockUser.getId());
    }





    @Test
    void createComment_ShouldReturnCreatedComment() {
        CommentRequest commentRequest = createValidCommentRequest();
        CommentResponse commentResponse = new CommentResponse(1L, "Test Comment", 1L, 1L, LocalDateTime.now());

        when(commentService.createComment(commentRequest, mockUser.getId())).thenReturn(commentResponse);

        ResponseEntity<ApiResponse<CommentResponse>> response = postController.getAllByPostId(commentRequest);

        assertTrue(response.getBody().isSuccess());
        assertEquals(commentResponse, response.getBody().getData());
        verify(commentService).createComment(commentRequest, mockUser.getId());
    }




}