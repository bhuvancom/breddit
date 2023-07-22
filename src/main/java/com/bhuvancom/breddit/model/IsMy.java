package com.bhuvancom.breddit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsMy {
    private boolean upVote;
    private boolean post;
    private boolean subreddit;
    private boolean downVote;
}
