package com.bhuvancom.breddit.service;

import com.bhuvancom.breddit.model.response.AuthTokenResponse;
import com.bhuvancom.breddit.model.request.LoginReq;
import com.bhuvancom.breddit.model.request.RegisterRequest;
import com.bhuvancom.breddit.exception.SpringRedditException;
import com.bhuvancom.breddit.exception.UserAlreadyExistException;
import com.bhuvancom.breddit.model.entity.User;
import com.bhuvancom.breddit.model.entity.VerificationToken;
import com.bhuvancom.breddit.repository.UserRepository;
import com.bhuvancom.breddit.repository.VerificationTokenRepository;
import com.bhuvancom.breddit.security.JWTProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager manager;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JWTProvider jwtProvider;

    public AuthService(PasswordEncoder passwordEncoder, AuthenticationManager manager, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, JWTProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.manager = manager;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public User signup(RegisterRequest req) {
        log.info("adding user");
        userRepository.findByUsername(req.getUserName()).ifPresent(u -> {
            throw new UserAlreadyExistException(req.getUserName() + " Username already in use");
        });

        userRepository.findByEmail(req.getEmail()).ifPresent((u) -> {
            throw new UserAlreadyExistException(req.getEmail() + " Email already in use");
        });

        var u = new User();
        u.setUsername(req.getUserName());
        u.setEmail(req.getEmail());
        var encodedPass = passwordEncoder.encode(req.getPassword());
        u.setPassword(encodedPass);
        u.setCreated(Instant.now());
        u.setEnabled(false);
        u = userRepository.save(u);
        generateVerificationToken(u);
        return u;
    }

    private VerificationToken generateVerificationToken(User u) {
        var token = UUID.randomUUID().toString();
        var verificationToken = new VerificationToken();
        verificationToken.setUser(u);
        verificationToken.setToken(token);
        // TODO: send email for verification
        return verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public User verifyAccount(String token) {
        var byToken = verificationTokenRepository
                .findByToken(token);
        byToken.orElseThrow(() -> new SpringRedditException("Token is not valid", HttpStatus.BAD_REQUEST));
        User user = byToken.get().getUser();
        return fetchUserAndEnable(user);
    }

    @Transactional
    private User fetchUserAndEnable(User t) {
        t.setEnabled(true);
        t = userRepository.save(t);
        return t;
    }

    public AuthTokenResponse login(LoginReq req) {
        Authentication authenticate = manager.authenticate(new UsernamePasswordAuthenticationToken(req.userName(),
                req.password()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthTokenResponse(token, getCurrentUser().orElseThrow(() -> new UsernameNotFoundException("User not found.")));
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        var principalAuth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = null;
        if (principalAuth != null) {
            log.info("auth is here");
            principal = principalAuth.getPrincipal();
        }
        String userName;
        if (principal instanceof String s) {
            log.info("user nm is string {} ", s);

            userName = s;
        } else if (principal instanceof org.springframework.security.core.userdetails.User e) {
            userName = e.getUsername();
            log.info("user nm us user obj {}", userName);
        } else {
            log.info("nothing matched {}", principalAuth);
            userName = "";
        }

        log.info("User is {}", userName);
        return userRepository.findByUsername(userName);
    }
}
