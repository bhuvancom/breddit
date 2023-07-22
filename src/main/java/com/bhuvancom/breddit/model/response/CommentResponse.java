package com.bhuvancom.breddit.model.response;

import com.bhuvancom.breddit.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Long commentId;
    private String text;
    private User commentBy;
    private Instant commentDate;
    private boolean isMyComment;
}
