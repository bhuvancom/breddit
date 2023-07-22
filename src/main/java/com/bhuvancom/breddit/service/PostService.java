package com.bhuvancom.breddit.service;

import com.bhuvancom.breddit.exception.PostNotFoundException;
import com.bhuvancom.breddit.exception.SpringRedditException;
import com.bhuvancom.breddit.exception.SubredditNotFoundException;
import com.bhuvancom.breddit.model.IsMy;
import com.bhuvancom.breddit.model.entity.Post;
import com.bhuvancom.breddit.model.entity.Subreddit;
import com.bhuvancom.breddit.model.entity.Vote;
import com.bhuvancom.breddit.model.request.PostRequest;
import com.bhuvancom.breddit.model.response.PostResponse;
import com.bhuvancom.breddit.repository.PostRepository;
import com.bhuvancom.breddit.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.bhuvancom.breddit.model.entity.VoteType.DOWNVOTE;
import static com.bhuvancom.breddit.model.entity.VoteType.UPVOTE;

@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final SubredditService subredditService;
    private final AuthService authService;

    private final VoteRepository voteRepository;

    public PostService(PostRepository postRepository, SubredditService subredditService, AuthService authService, VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.subredditService = subredditService;
        this.authService = authService;
        this.voteRepository = voteRepository;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsBySubredditId(Long subredditId, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        return postRepository.findByIsDeletedFalseAndSubredditIdAndSubredditIsDeletedFalseOrderByCreatedDateDesc(subredditId, pageRequest).map(p -> p.toPostRes(false, getIsMy(p), true));
    }

    public IsMy getIsMy(Post e) {
        var user = authService.getCurrentUser();
        var currentUserOfPost = user.map(u -> u.getUserId().equals(e.getUser().getUserId())).orElse(false);
        var isMy = new IsMy();
        isMy.setPost(currentUserOfPost);
        var vote = getVote(e);
        isMy.setUpVote(vote.getFirst());
        isMy.setDownVote(vote.getSecond());
        return isMy;
    }


    @Transactional(readOnly = true)
    public Pair<Boolean, Boolean> getVote(Post post) {
        var pair = Pair.of(false, false);
        var user = authService.getCurrentUser();
        pair = user.map(u -> {
            Optional<Vote> vote = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, u);
            boolean isUp = false;
            boolean isDown = false;
            if (vote.isPresent()) {
                isUp = vote.get().getVoteType().equals(UPVOTE);
                isDown = vote.get().getVoteType().equals(DOWNVOTE);
            }
            return Pair.of(isUp, isDown);
        }).orElse(pair);

        return pair;
    }

    @Transactional
    public void deletePost(Long postId, Long id) {
        if (postId == null || id == null) throw new SpringRedditException("Invalid request", HttpStatus.BAD_REQUEST);
        postRepository.findById(postId).map(e -> {
            if (!id.equals(e.getSubreddit().getId()))
                throw new SpringRedditException("Invalid request for the postId " + postId, HttpStatus.BAD_REQUEST);
            var user = authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("Login first", HttpStatus.BAD_REQUEST));
            if (!e.getUser().getUserId().equals(user.getUserId())) {
                throw new SpringRedditException("You are not owner of this post", HttpStatus.BAD_REQUEST);
            }
            e.setDeleted(true);
            postRepository.save(e);
            return 1;
        }).orElseThrow(() -> new SpringRedditException("Post not found for id " + postId, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public PostResponse save(PostRequest post, Long subRedditId) {
        log.info("saving post for subreddit id {}, post name is {}", subRedditId, post.getPostName());
        return subredditService.getById(subRedditId)
                .map(e -> {
                    Post toPost = maptToPost(post);
                    Subreddit s = new Subreddit();
                    s.setId(subRedditId);
                    toPost.setSubreddit(s);
                    toPost.setVoteCount(0);
                    toPost = postRepository.save(toPost);
                    var subre = toPost.getSubreddit();
                    var u = toPost.getUser();
                    log.info("saved post for subreddit id {}, post name is {}", subRedditId, post.getPostName());
                    var isMy = getIsMy(toPost);
                    return toPost.toPostRes(true, isMy, false);
                })
                .orElseThrow(() -> new SubredditNotFoundException("Subreddit " + subRedditId + " not found"));
    }

    private Post maptToPost(PostRequest request) {
        return Post.builder()
                .postName(request.getPostName())
                .postId(0L)
                .user(authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("User not logged in", HttpStatus.BAD_REQUEST)))
                .description(request.getDescription())
                .url(request.getUrl())
                .build();
    }

    public Page<PostResponse> getPostByUserAndSubredditId(String userName, Long subredditId, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<Post> posts = postRepository.findByIsDeletedFalseAndSubredditIdAndUserUsernameOrderByCreatedDateDesc(subredditId, userName, pageRequest);
        return posts.map(p -> p.toPostRes(false, getIsMy(p), true));
    }

    public Page<PostResponse> getPostByUser(String userName, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<Post> posts = postRepository.findByIsDeletedFalseAndSubredditIsDeletedFalseAndAndUserUsernameOrderByCreatedDateDesc(userName, pageRequest);
        return posts.map(p -> p.toPostRes(true, getIsMy(p), true));
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id).map(e -> {
            if (e.isDeleted()) throw new SpringRedditException("Post not found ", HttpStatus.NOT_FOUND);
            return e;
        });
    }

    public Page<PostResponse> getPostForHome(Integer page, Integer pageSize) {
        if (page < 1) page = 1;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        return postRepository.findByIsDeletedFalseAndSubredditIsDeletedFalseOrderByCreatedDateDesc(pageRequest).map(e -> e.toPostRes(true, getIsMy(e), true));
    }


    public PostResponse updatePost(Long postId, PostRequest r) {
        if (!postId.equals(r.getPostId())) throw new SpringRedditException("Invalid request", HttpStatus.BAD_REQUEST);

        return postRepository.findById(postId).map(e -> {
            if (e.isDeleted()) throw new PostNotFoundException("Post with id " + postId + " not found");
            var user = authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("Login first", HttpStatus.BAD_REQUEST));

            if (!e.getUser().getUserId().equals(user.getUserId())) {
                throw new SpringRedditException("You are not owner of this post", HttpStatus.BAD_REQUEST);
            }
            e.setDescription(r.getDescription());
            return postRepository.save(e).toPostRes(true, getIsMy(e), false);
        }).orElseThrow(() -> new PostNotFoundException("Post with id " + postId + " not found"));
    }

    public void save(Post post) {
        postRepository.save(post);
    }
}
