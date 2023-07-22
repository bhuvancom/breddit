package com.bhuvancom.breddit.cotroller;

import com.bhuvancom.breddit.model.entity.Vote;
import com.bhuvancom.breddit.model.request.VoteReq;
import com.bhuvancom.breddit.service.VoteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/vote")
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/{postId}")
    public ResponseEntity<Void> vote(@Valid @RequestBody VoteReq voteReq, @PathVariable Long postId) {
        voteService.vote(voteReq, postId);
        return ResponseEntity.ok(null);
    }
}
