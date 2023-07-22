package com.bhuvancom.breddit.exception;

import org.springframework.http.HttpStatus;

public class SpringRedditException extends RuntimeException {
    public HttpStatus status;

    public SpringRedditException(String exMessage, HttpStatus status, Exception exception) {
        super(exMessage, exception);
        this.status = status
        ;
    }

    public SpringRedditException(String exMessage, HttpStatus status) {
        super(exMessage);
        this.status = status
        ;
    }
}