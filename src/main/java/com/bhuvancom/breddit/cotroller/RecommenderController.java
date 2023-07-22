package com.bhuvancom.breddit.cotroller;

import com.bhuvancom.breddit.model.response.PostResponse;
import com.bhuvancom.breddit.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class RecommenderController {
    private final PostService postService;

    public RecommenderController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Page<PostResponse> getAutoData(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<PostResponse> data = postService.getPostForHome(page, pageSize);
        return data;
    }
}
