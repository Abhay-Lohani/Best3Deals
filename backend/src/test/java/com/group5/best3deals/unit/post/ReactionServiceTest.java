package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.Enum.ReactionType;
import com.group5.best3deals.post.dto.request.ReactionRequest;
import com.group5.best3deals.post.dto.response.ReactionResponse;
import com.group5.best3deals.post.entity.Reaction;
import com.group5.best3deals.post.repository.PostRepository;
import com.group5.best3deals.post.repository.ReactionRepository;
import com.group5.best3deals.post.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ReactionService reactionService;

    private Reaction reaction;
    private ReactionRequest reactionReq;

    @BeforeEach
    public void setUp() {
        reaction = Reaction.builder()
                .id(1L)
                .reactionType(ReactionType.LIKE)
                .userId(1L)
                .postId(1L)
                .build();

        reactionReq = new ReactionRequest(ReactionType.LIKE, 1L);
    }

    @Test
    public void testGetReactionById_Success() {
        when(reactionRepository.findById(reaction.getId())).thenReturn(Optional.of(reaction));

        Reaction foundReaction = reactionService.getReactionById(reaction.getId());

        assertNotNull(foundReaction);
        assertEquals(reaction, foundReaction);

        verify(reactionRepository, times(1)).findById(reaction.getId());
    }

    @Test
    public void testGetReactionById_NotFound() {
        when(reactionRepository.findById(reaction.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reactionService.getReactionById(reaction.getId()));

        verify(reactionRepository, times(1)).findById(reaction.getId());
    }

    @Test
    public void testGetAllByPostId_Success() {
        when(reactionRepository.findAllByPostId(reaction.getPostId())).thenReturn(List.of(reaction));

        List<Reaction> reactions = reactionService.getAllByPostId(reaction.getPostId());

        assertNotNull(reactions);
        assertEquals(1, reactions.size());
        assertEquals(reaction, reactions.get(0));

        verify(reactionRepository, times(1)).findAllByPostId(reaction.getPostId());
    }

    @Test
    public void testCreateReaction_Success() {
        when(postRepository.existsById(reaction.getPostId())).thenReturn(true);
        when(reactionRepository.save(any(Reaction.class))).thenReturn(reaction);

        ReactionResponse response = reactionService.createReaction(reactionReq, reaction.getUserId());

        assertNotNull(response);
        assertEquals(reaction.getReactionType(), response.getReactionType());
        assertEquals(reaction.getUserId(), response.getUserId());
        assertEquals(reaction.getPostId(), response.getPostId());

        verify(postRepository, times(1)).existsById(reaction.getPostId());
        verify(reactionRepository, times(1)).save(any(Reaction.class));
    }

    @Test
    public void testCreateReaction_PostNotFound() {
        when(postRepository.existsById(reaction.getPostId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reactionService.createReaction(reactionReq, 123L));

        verify(postRepository, times(1)).existsById(reaction.getPostId());
        verify(reactionRepository, never()).save(any(Reaction.class));
    }

    @Test
    public void testDeleteById_Success() {
        when(reactionRepository.findById(reaction.getId())).thenReturn(Optional.of(reaction));

        reactionService.deleteById(reaction.getId(), reaction.getUserId());

        verify(reactionRepository, times(1)).findById(reaction.getId());
        verify(reactionRepository, times(1)).delete(reaction);
    }

    @Test
    public void testDeleteById_ReactionNotFound() {
        when(reactionRepository.findById(reaction.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reactionService.deleteById(reaction.getId(), reaction.getUserId()));

        verify(reactionRepository, times(1)).findById(reaction.getId());
        verify(reactionRepository, never()).delete(any(Reaction.class));
    }

    @Test
    public void testDeleteById_UserNotMatched() {
        final Long wrongUserId = 2L;
        when(reactionRepository.findById(reaction.getId())).thenReturn(Optional.of(reaction));

        assertThrows(IllegalArgumentException.class, () -> reactionService.deleteById(reaction.getId(), wrongUserId));

        verify(reactionRepository, times(1)).findById(reaction.getId());
        verify(reactionRepository, never()).delete(any(Reaction.class));
    }
}