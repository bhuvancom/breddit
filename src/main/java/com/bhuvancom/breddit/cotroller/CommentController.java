package com.bhuvancom.breddit.cotroller;

import com.bhuvancom.breddit.model.request.CommentReq;
import com.bhuvancom.breddit.model.response.CommentResponse;
import com.bhuvancom.breddit.service.CommentsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentsService commentsService;

    public CommentController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @GetMapping("/{postId}")
    public Page<CommentResponse> getCommentsOfPost(@PathVariable("postId") Long postId, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<CommentResponse> data = commentsService.getCommentsOfPost(postId, page, pageSize);
        return data;
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentById(@PathVariable Long commentId) {
        commentsService.deleteComment(commentId);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> postComment(@Valid @RequestBody CommentReq req, @PathVariable Long postId) {
        CommentResponse commentResponse = commentsService.addComment(postId, req);
        return ResponseEntity.status(201).body(commentResponse);
    }
}
