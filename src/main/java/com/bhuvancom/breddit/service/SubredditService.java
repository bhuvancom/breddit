package com.bhuvancom.breddit.service;

import com.bhuvancom.breddit.dto.SubredditDto;
import com.bhuvancom.breddit.exception.SpringRedditException;
import com.bhuvancom.breddit.exception.SubredditNotFoundException;
import com.bhuvancom.breddit.model.entity.Subreddit;
import com.bhuvancom.breddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;


    @Transactional
    public Subreddit save(Subreddit subreddit) {
        return subredditRepository.save(subreddit);
    }

    @Transactional(readOnly = true)
    public Page<SubredditDto> getAll(Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        Page<SubredditDto> subReddits = subredditRepository.getSubReddits(pageRequest);
        subReddits = subReddits.map(e -> {
            var user = authService.getCurrentUser().map(u -> u.getUserId().equals(e.getCreator().getUserId())).orElse(false);
            e.setMySubreddit(user);
            return e;
        });
        return subReddits;
    }

    public Optional<Subreddit> getById(Long id) {
        return subredditRepository.findById(id).map(e -> {
            if (e.isDeleted()) throw new SubredditNotFoundException("Subreddit with id " + id + " not found");
            return e;
        });
    }

    public void deleteById(Long id) {
        log.info("delete call for subreddit id {}", id);
        subredditRepository.findById(id).map(e -> {
            var user = authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("Login first",HttpStatus.BAD_REQUEST));
            if (!e.getUser().getUserId().equals(user.getUserId())) {
                throw new SpringRedditException("You are not owner of this subreddit", HttpStatus.BAD_REQUEST);
            }
            e.setDeleted(true);
            subredditRepository.save(e);
            return 1;
        }).orElseThrow(() -> new SubredditNotFoundException("Subreddit " + id + " not found"));
    }

    public SubredditDto update(SubredditDto dto, Long id) {
        if (dto == null) throw new SpringRedditException("Incorrect request", HttpStatus.BAD_REQUEST);
        if (!dto.getId().equals(id))
            throw new SpringRedditException(String.format("ID mismatch expected %d, received %d", id, dto.getId()), HttpStatus.BAD_REQUEST);

        return subredditRepository.findById(id)
                .map(e -> {
                    if (e.isDeleted()) throw new SubredditNotFoundException("Subreddit with id " + id +
                            "not found");
                    var user = authService.getCurrentUser().orElseThrow(() -> new SpringRedditException("Login first", HttpStatus.BAD_REQUEST));

                    if (!e.getUser().getUserId().equals(user.getUserId())){
                        throw new SpringRedditException("You are not owner of this post", HttpStatus.BAD_REQUEST);
                    }
                    e.setName(dto.getName());
                    e.setDescription(dto.getDescription());
                    e = subredditRepository.save(e);
                    return e.toSubDto(isMySubreddit(e));
                })
                .orElseThrow(() -> new SubredditNotFoundException("Subreddit " + id + " not found"));
    }

    public Page<SubredditDto> getByUsername(String username, Integer page, Integer pageSize) {
        if(page < 1) page = 1;
        PageRequest pageRequest = PageRequest.of(page-1, pageSize);
        return subredditRepository.findByIsDeletedFalseAndUserUsernameOrderByCreatedDateDesc(username, pageRequest).map(e -> {
            var user = authService.getCurrentUser().map(u -> u.getUserId().equals(e.getUser().getUserId())).orElse(false);
            return e.toSubDto(user);
        });
    }

    private boolean isMySubreddit(Subreddit subreddit) {
        return false;
    }
}
