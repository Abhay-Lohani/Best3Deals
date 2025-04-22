package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.dto.request.CommentRequest;
import com.group5.best3deals.post.dto.response.CommentResponse;
import com.group5.best3deals.post.entity.Comment;
import com.group5.best3deals.post.repository.CommentRepository;
import com.group5.best3deals.post.repository.PostRepository;
import com.group5.best3deals.post.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentRequest commentReq;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .content("Test Comment")
                .userId(1L)
                .postId(1L)
                .build();

        commentReq = new CommentRequest(comment.getContent(), comment.getPostId());
    }

    // Test get methods
    @Test
    void getCommentById_ShowReturnComment_WhenCommentExists() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Comment foundComment = commentService.getCommentById(comment.getId());

        assertNotNull(foundComment);
        assertEquals(comment, foundComment);
    }

    @Test
    public void getCommentById_ShouldThrowException_WhenCommentDoesNotExist() {
        final Long wrongId = 2L;
        when(commentRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentService.getCommentById(wrongId));
    }

    @Test
    public void getAllByPostId_ShouldReturnComments_WhenPostExists() {
        when(postRepository.existsById(comment.getPostId())).thenReturn(true);
        when(commentRepository.findByPostId(comment.getPostId())).thenReturn(List.of(comment));

        List<Comment> foundComments = commentService.getAllByPostId(comment.getPostId());

        assertNotNull(foundComments);
        assertEquals(1, foundComments.size());
        assertEquals(comment, foundComments.get(0));
    }

    @Test
    void getAllByPostId_ShouldThrowException_WhenPostDoesNotExist() {
        final Long wrongPostId = 2L;
        when(postRepository.existsById(wrongPostId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> commentService.getAllByPostId(wrongPostId));
    }

    // Test create
    @Test
    void createComment_ShouldReturnCommentResponse_WhenPostExists() {
        when(postRepository.existsById(comment.getPostId())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse response = commentService.createComment(commentReq, comment.getUserId());

        assertNotNull(response);
        assertEquals(commentReq.getContent(), response.getContent());
        assertEquals(comment.getUserId(), response.getUserId());
        assertEquals(commentReq.getPostId(), response.getPostId());
    }

    @Test
    void createComment_ShouldReturnCommentResponse_WhenPostDoesNotExist() {
        when(postRepository.existsById(comment.getPostId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> commentService.createComment(commentReq, comment.getId()));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    // Test delete
    @Test
    public void deleteById_ShouldReturnVoid_WhenCommentExists() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.deleteById(comment.getId(), comment.getUserId(), roles);

        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteById_ShouldThrowException_WhenUserDoesNotOwnComment() {
        final Long wrongUserId = 2L;
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class, () -> commentService.deleteById(comment.getId(), wrongUserId, roles)); // Pass roles list

        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

}
