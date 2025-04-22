package com.group5.best3deals.post.service;

import com.group5.best3deals.post.dto.request.CommentRequest;
import com.group5.best3deals.post.dto.response.CommentResponse;
import com.group5.best3deals.post.entity.Comment;
import com.group5.best3deals.post.repository.CommentRepository;
import com.group5.best3deals.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow((() -> new NoSuchElementException("Comment not found with " + id)));
    }

    public List<Comment> getAllByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("Post not found with " + postId);
        }

        return commentRepository.findByPostId(postId);
    }

    public CommentResponse createComment(CommentRequest commentRequest, Long userId) {
        if (!postRepository.existsById(commentRequest.getPostId())) {
            throw new NoSuchElementException("Post not found with " + commentRequest.getPostId());
        }

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .userId(userId)
                .postId(commentRequest.getPostId())
                .build();

        commentRepository.save(comment);

        return new CommentResponse(comment.getId(), comment.getContent(), comment.getUserId(), comment.getPostId(), comment.getCreatedAt());
    }

    public void deleteById(Long commentId, Long userId, List<String> roles) {
        Comment comment = getCommentById(commentId);

        // Check if the user is an admin
        boolean isAdmin = roles.contains("ROLE_ADMIN");

        if (!comment.getUserId().equals(userId) && !isAdmin) {
            throw new IllegalArgumentException("User can only delete the comment they have made");
        }

        commentRepository.delete(comment);
    }
}
