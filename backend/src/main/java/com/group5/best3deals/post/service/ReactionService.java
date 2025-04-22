package com.group5.best3deals.post.service;

import com.group5.best3deals.post.dto.request.ReactionRequest;
import com.group5.best3deals.post.dto.response.ReactionResponse;
import com.group5.best3deals.post.entity.Reaction;
import com.group5.best3deals.post.repository.PostRepository;
import com.group5.best3deals.post.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    public Reaction getReactionById(Long id) {
        return reactionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Reaction not found with " + id));
    }

    public List<Reaction> getAllByPostId(Long postId) {
        return reactionRepository.findAllByPostId(postId);
    }

    public ReactionResponse createReaction(ReactionRequest reactionRequest, Long userId) {
        if (!postRepository.existsById(reactionRequest.getPostId())) {
            throw new NoSuchElementException("Post not found with " + reactionRequest.getPostId());
        }

        Reaction reaction = Reaction.builder()
                .reactionType(reactionRequest.getReactionType())
                .userId(userId)
                .postId(reactionRequest.getPostId())
                .build();

        reactionRepository.save(reaction);

        return new ReactionResponse(reaction.getId(), reaction.getReactionType(), reaction.getUserId(), reaction.getPostId());
    }

    public void deleteById(Long id, Long userId) {
        Reaction existingReaction = getReactionById(id);

        if (!existingReaction.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User can only delete the reaction they have made");
        }

        reactionRepository.delete(existingReaction);
    }
}
