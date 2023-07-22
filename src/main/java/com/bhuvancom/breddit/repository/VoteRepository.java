package com.bhuvancom.breddit.repository;

import com.bhuvancom.breddit.model.entity.Post;
import com.bhuvancom.breddit.model.entity.User;
import com.bhuvancom.breddit.model.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
