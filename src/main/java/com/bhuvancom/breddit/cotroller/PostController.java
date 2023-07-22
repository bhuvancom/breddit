package com.bhuvancom.breddit.cotroller;

import com.bhuvancom.breddit.exception.PostNotFoundException;
import com.bhuvancom.breddit.model.IsMy;
import com.bhuvancom.breddit.model.request.PostRequest;
import com.bhuvancom.breddit.model.response.PostResponse;
import com.bhuvancom.breddit.service.AuthService;
import com.bhuvancom.breddit.service.PostService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final AuthService authService;

    public PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @PostMapping("/{subredditId}")
    public ResponseEntity<PostResponse> createPost(@PathVariable(name = "subredditId") Long subRedditId, @RequestBody @Valid PostRequest postRequest) throws URISyntaxException {
        PostResponse saved = postService.save(postRequest, subRedditId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping("/{subredditId}/posts")
    public Page<PostResponse> getPostsOfSubreddit(@PathVariable(name = "subredditId") Long id,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return postService.getPostsBySubredditId(id, page, pageSize);
    }

    @DeleteMapping("/{subredditId}/posts/{postId}")
    public void deletePost(@PathVariable(name = "subredditId") Long id, @PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId, id);
    }

    @GetMapping("/{postId}")
    public PostResponse getPostById(@PathVariable(name = "postId") Long postId) {
        return postService.getPostById(postId).map(e -> {
                    IsMy isMy = postService.getIsMy(e);
                    return e.toPostRes(true, isMy, false);
                })
                .orElseThrow(() -> new PostNotFoundException("Post not found for id " + postId));
    }


    @GetMapping("/{subredditId}/post/by-user/{username}")
    public Page<PostResponse> getPostByUsernameAndSubredditId(@PathVariable(name = "subredditId") Long subredditId,
                                                              @PathVariable(name = "username") String username,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                              @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return postService.getPostByUserAndSubredditId(username, subredditId, page, pageSize);
    }

    @GetMapping("/by-user/{username}")
    public Page<PostResponse> getPostByUsername(
            @PathVariable(name = "username") String username,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return postService.getPostByUser(username, page, pageSize);
    }

    @PutMapping("/{postId}")
    public PostResponse updatePostById(@PathVariable(name = "postId") Long postId, @Valid @RequestBody PostRequest r) {
        return postService.updatePost(postId, r);
    }

}
