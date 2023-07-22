package com.bhuvancom.breddit.model.response;

import com.bhuvancom.breddit.dto.SubredditDto;
import com.bhuvancom.breddit.model.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private Long postId;
    private SubredditDto subreddit;
    private User user;
    private boolean isMyPost;
    private String title;
    private String description;
    private Integer voteCount = 0;
    private Integer commentCount = 0;
    private Instant creationDate;
    private boolean isMyUpVote;
    private boolean isMyDownVote;
}
