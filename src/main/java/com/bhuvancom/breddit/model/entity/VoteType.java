package com.bhuvancom.breddit.model.entity;

import com.bhuvancom.breddit.exception.SpringRedditException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1),
    ;

    private int direction;

    VoteType(int direction) {
    }

    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found", HttpStatus.NOT_FOUND));
    }

    public Integer getDirection() {
        return direction;
    }
}