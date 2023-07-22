package com.bhuvancom.breddit.cotroller;

import com.bhuvancom.breddit.model.response.AuthTokenResponse;
import com.bhuvancom.breddit.model.request.LoginReq;
import com.bhuvancom.breddit.model.request.RegisterRequest;
import com.bhuvancom.breddit.model.entity.User;
import com.bhuvancom.breddit.repository.VerificationTokenRepository;
import com.bhuvancom.breddit.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final VerificationTokenRepository verificationTokenRepository;

    public AuthController(AuthService authService, VerificationTokenRepository verificationTokenRepository) {
        this.authService = authService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @PostMapping({"/signup", "/register"})
    public ResponseEntity<User> saveUser(@RequestBody RegisterRequest registerRequest) {
        log.info("Entering register path");
        User signup = authService.signup(registerRequest);
        return ResponseEntity.ok(signup);
    }

    @GetMapping("/verify-account/{token}")
    public ResponseEntity<User> activateAccount(@PathVariable("token") String token) {
        log.info("Entering verify account path");
        return ResponseEntity.accepted().body(authService.verifyAccount(token));
    }

    @PostMapping({"/login", "/signin"})
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginReq req) {
        log.info("entering login path {}", req);
        return ResponseEntity.ok(authService.login(req));
    }
}
