package com.bhuvancom.breddit.service;

import com.bhuvancom.breddit.exception.PostNotFoundException;
import com.bhuvancom.breddit.exception.SpringRedditException;
import com.bhuvancom.breddit.model.entity.Comment;
import com.bhuvancom.breddit.model.entity.User;
import com.bhuvancom.breddit.model.request.CommentReq;
import com.bhuvancom.breddit.model.response.CommentResponse;
import com.bhuvancom.breddit.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentsService {
    private final AuthService authService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    public CommentsService(AuthService authService, PostService postService, CommentRepository commentRepository) {
        this.authService = authService;
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsOfPost(Long postId, Integer page, Integer pageSize) {
        if (page < 1) page = 1;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<CommentResponse> commentResponsePage = commentRepository.findByIsDeletedFalseAndPostPostIdOrderByCreatedDateDesc(postId, pageRequest)
                .map(e -> {
                    var isMyComment = authService.getCurrentUser().map(u -> u.getUserId().equals(e.getUser().getUserId()))
                            .orElse(false);
                    return e.toComRes(isMyComment);
                });
        return commentResponsePage;

    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId).map(e -> {
            if (e.isDeleted())
                throw new SpringRedditException("Comment not found " + commentId, HttpStatus.NOT_FOUND);
            if (!e.getUser().getUserId().equals(authService.getCurrentUser().map(User::getUserId).orElse(-1L))) {
                throw new SpringRedditException("You are not owner of this comment", HttpStatus.BAD_REQUEST);
            }
            e.setDeleted(true);
            commentRepository.save(e);
            return 1;
        }).orElseThrow(() -> new SpringRedditException("Comment not found " + commentId, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentReq r) {
        return postService.getPostById(postId).map(e -> {
            if (e.isDeleted()) {
                throw new PostNotFoundException("Post not found to add comment " + postId);
            }
            var comment = Comment.builder().text(r.getCommentText())
                    .post(e)
                    .user(authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("User not logged in", HttpStatus.BAD_REQUEST)))
                    .isDeleted(false)
                    .build();
            comment = commentRepository.save(comment);
            Comment finalComment = comment;
            var isMyComment = authService.getCurrentUser().map(u -> u.getUserId().equals(finalComment.getUser().getUserId()))
                    .orElse(false);
            return comment.toComRes(isMyComment);
        }).orElseThrow(() -> new PostNotFoundException("Post not found to add comment " + postId));
    }
}
