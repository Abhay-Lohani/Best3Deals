package com.group5.best3deals.post.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.post.dto.request.CommentRequest;
import com.group5.best3deals.post.dto.request.PostPutRequest;
import com.group5.best3deals.post.dto.request.PostRequest;
import com.group5.best3deals.post.dto.request.ReactionRequest;
import com.group5.best3deals.post.dto.response.CommentResponse;
import com.group5.best3deals.post.dto.response.PostResponse;
import com.group5.best3deals.post.dto.response.ReactionResponse;
import com.group5.best3deals.post.entity.Comment;
import com.group5.best3deals.post.entity.Post;
import com.group5.best3deals.post.service.CommentService;
import com.group5.best3deals.post.service.PostService;
import com.group5.best3deals.post.service.ReactionService;
import com.group5.best3deals.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Posts", description = "API for posts")
public class PostController {

    private final PostService postService;
    private final ReactionService reactionService;
    private final CommentService commentService;

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        return ResponseEntity.ok(new ApiResponse<>(true, postService.getAllPosts()));
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PostResponse>> getPostsById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, postService.getPostById(id)));
    }

    @GetMapping("/all/my")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Post>>> getAllByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, postService.getAllByUserId(user.getId())));
    }

    @GetMapping("/all/{storeId}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Post>>> getAllByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, postService.getAllByStoreId(storeId)));
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody PostRequest post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, postService.createPost(post, user.getId())));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@PathVariable Long id, @RequestBody PostPutRequest post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, postService.updatePost(id, post, user.getId())));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return ResponseEntity.ok(new ApiResponse<>(true));
    }

    // APIs for reaction creation and deletion
    @PostMapping("/reactions")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReactionResponse>> addReaction(@RequestBody ReactionRequest reaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, reactionService.createReaction(reaction, user.getId())));
    }

    @DeleteMapping("/reactions/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelReaction(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        reactionService.deleteById(id, user.getId());

        return ResponseEntity.ok(new ApiResponse<>(true));
    }

    // APIs for comment search, creation and deletion
    @GetMapping("/comments/{id}")
    @Operation(
            summary = "Fetch a comment with id",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Comment>> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, commentService.getCommentById(id)));
    }

    @GetMapping("/comments/all/{postId}")
    @Operation(
            summary = "Fetch all comments with postId",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Comment>>> getAllByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(new ApiResponse<>(true, commentService.getAllByPostId(postId)));
    }

    @PostMapping("/comments")
    @Operation(
            summary = "Create a comment",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentResponse>> getAllByPostId(@RequestBody CommentRequest comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, commentService.createComment(comment, user.getId())));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(
            summary = "Delete a comment",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Extract roles from the authenticated user
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        commentService.deleteById(id, user.getId(), roles);

        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
