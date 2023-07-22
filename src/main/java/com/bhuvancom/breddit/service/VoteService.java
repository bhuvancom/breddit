package com.bhuvancom.breddit.service;

import com.bhuvancom.breddit.exception.PostNotFoundException;
import com.bhuvancom.breddit.exception.SpringRedditException;
import com.bhuvancom.breddit.model.entity.Post;
import com.bhuvancom.breddit.model.entity.User;
import com.bhuvancom.breddit.model.entity.Vote;
import com.bhuvancom.breddit.model.request.VoteReq;
import com.bhuvancom.breddit.repository.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.bhuvancom.breddit.model.entity.VoteType.UPVOTE;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostService postService;
    private final AuthService authService;

    public VoteService(VoteRepository voteRepository, PostService postService, AuthService authService) {
        this.voteRepository = voteRepository;
        this.postService = postService;
        this.authService = authService;
    }

    @Transactional
    public void vote(VoteReq voteDto, Long postId) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + postId));
        Optional<User> currentUser = authService.getCurrentUser();
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc
                (post, currentUser.orElseThrow(() -> new SpringRedditException("User not logged in", HttpStatus.BAD_REQUEST)));
        Vote vote;
        // if already voted
        if (voteByPostAndUser.isPresent()) {
            vote = voteByPostAndUser.get();
            // IF current type is same as old
            if (vote.getVoteType()
                    .equals(voteDto.getVoteType())) {
                throw new SpringRedditException("You have already "
                        + voteDto.getVoteType() + "'d for this post", HttpStatus.BAD_REQUEST);
            } else {
                // vote is updating
                vote.setVoteType(voteDto.getVoteType());
                if (UPVOTE.equals(voteDto.getVoteType())) {
                    post.setVoteCount(post.getVoteCount() + 2);
                } else {
                    post.setVoteCount(post.getVoteCount() - 2);
                }
            }
        } else { // Fresh new vote
            vote = Vote.builder()
                    .post(post)
                    .user(currentUser.get())
                    .voteType(voteDto.getVoteType())
                    .build();
            if (UPVOTE.equals(voteDto.getVoteType())) {
                post.setVoteCount(post.getVoteCount() + 1);
            } else {
                post.setVoteCount(post.getVoteCount() - 1);
            }
        }
        postService.save(post);
        voteRepository.save(vote);
    }
}
