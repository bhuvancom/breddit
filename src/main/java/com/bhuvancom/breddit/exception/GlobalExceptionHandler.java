package com.bhuvancom.breddit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<MainException> handlePostNotFoundException(PostNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        log.error("Error as post not found msg {}, error {}", message, ex.toString());
        return ResponseEntity.status(status).body(new MainException(message, status));
    }

    @ExceptionHandler(SubredditNotFoundException.class)
    public ResponseEntity<MainException> handleSubredditNotFoundException(SubredditNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        log.error("Error as subreddit not found msg {}, error {}", message, ex.toString());
        return ResponseEntity.status(status).body(new MainException(message, status));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<MainException> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        log.error("Error as user not found msg {}, error {}", message, ex.toString());
        return ResponseEntity.status(status).body(new MainException(message, status));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<MainException> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();
        log.error("Error as user already exist msg {}, error {}", message, ex.toString());
        return ResponseEntity.status(status).body(new MainException(message, status));
    }


    @ExceptionHandler(SpringRedditException.class)
    public ResponseEntity<MainException> handleSpringRedditException(SpringRedditException ex) {
        HttpStatus status = ex.status;
        String message = ex.getMessage();
        log.error("Error as error {} msg {}", ex, message);
        return ResponseEntity.status(status).body(new MainException(message, status));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MainException> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Error as user credentials not matching");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MainException("Please check your username/password", HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MainException> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        // Customize the response for MethodArgumentTypeMismatchException
        String message = "Invalid argument type. Expected: " + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() +
                ", but received: " + ex.getValue();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MainException(message, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MainException> handleException(Exception ex) {
        ex.printStackTrace();
        log.error("Error generic exception {}, msg {}", ex.toString(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MainException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}