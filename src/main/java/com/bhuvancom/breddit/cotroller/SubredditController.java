package com.bhuvancom.breddit.cotroller;

import com.bhuvancom.breddit.dto.SubredditDto;
import com.bhuvancom.breddit.exception.SpringRedditException;
import com.bhuvancom.breddit.exception.SubredditNotFoundException;
import com.bhuvancom.breddit.model.entity.Subreddit;
import com.bhuvancom.breddit.service.AuthService;
import com.bhuvancom.breddit.service.SubredditService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/subreddit")
public class SubredditController {

    private final SubredditService subredditService;
    private final AuthService authService;

    public SubredditController(SubredditService subredditService, AuthService authService) {
        this.subredditService = subredditService;
        this.authService = authService;
    }

    @GetMapping()
    public Page<SubredditDto> getSubreddits(@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                            @RequestParam(name = "page", defaultValue = "1") Integer page) {
        log.info("Get subreddits called page {}, page size {}", page, pageSize);
        Page<SubredditDto> subredditPage = subredditService
                .getAll(page, pageSize);
//        Page<SubredditDto> map = subredditPage.map(this::mapToSubDto);
        return subredditPage;
    }

    @GetMapping("/by-username/{username}")
    public Page<SubredditDto> getSubredditsOfUser(@PathVariable String username, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  @RequestParam(name = "page", defaultValue = "1") Integer page) {
        log.info("Get subreddits called for user {}, page {}, page size {}", username, page, pageSize);
        Page<SubredditDto> subredditPage = subredditService
                .getByUsername(username, page, pageSize);
        return subredditPage;
    }


    @PostMapping
    public ResponseEntity<Subreddit> createSubReddit(@RequestBody @Valid SubredditDto subredditDto) {
        Subreddit save = subredditService.save(mapSubRedditDto(subredditDto));
        return ResponseEntity.status(201).body(save);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubredditById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(subredditService.getById(id)
                .map(e -> {
                    var currentUser = authService.getCurrentUser().map(u -> u.getUserId().equals(e.getUser().getUserId())).orElse(false);
                    return e.toSubDto(currentUser);
                })
                .orElseThrow(() -> new SubredditNotFoundException(" Subreddit with id " + id + " not found."))
        );
    }

    private Subreddit mapSubRedditDto(SubredditDto dto) {
        return Subreddit.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .user(authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("USer not logged in", HttpStatus.BAD_REQUEST)))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubreddit(@PathVariable(name = "id") Long id) {
        log.info("going to delete {} id subreddit ", id);
        subredditService.getById(id)
                .map(e -> {
                    log.info("got the subreddit of id {}", id);
                    var user = authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("Please login", HttpStatus.BAD_REQUEST));
                    boolean sameUser = e.getUser().getUserId().equals(user.getUserId());
                    if (!sameUser) {
                        log.info("subreddit creator is not same as current for id {}", id);
                        throw new SpringRedditException("This subreddit does not belong to you", HttpStatus.BAD_REQUEST);
                    } else {
                        subredditService.deleteById(id);
                    }
                    return 1;
                })
                .orElseThrow(() ->
                        new SubredditNotFoundException(" Subreddit with id " + id + " not found.")
                );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubredditDto> updateSubReddit(@PathVariable(name = "id") Long id, @Valid @RequestBody SubredditDto dto) {
        log.info("put api call to subreddit id {}", id);
        SubredditDto res = subredditService.update(dto, id);
        return ResponseEntity.ok(res);
    }
}
