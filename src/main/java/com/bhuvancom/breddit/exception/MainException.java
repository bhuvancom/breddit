package com.bhuvancom.breddit.exception;

import org.springframework.http.HttpStatus;

public record MainException(
        String message,
        HttpStatus status
) {
}
