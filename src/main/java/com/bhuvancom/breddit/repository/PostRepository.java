package com.bhuvancom.breddit.repository;

import com.bhuvancom.breddit.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsDeletedFalseAndSubredditIdAndSubredditIsDeletedFalseOrderByCreatedDateDesc(Long subredditId, Pageable pageable);

    Page<Post> findByIsDeletedFalseAndSubredditIdAndUserUsernameOrderByCreatedDateDesc(Long subredditId, String userName, Pageable pageRequest);

    Page<Post> findByIsDeletedFalseAndSubredditIsDeletedFalseOrderByCreatedDateDesc(Pageable pageRequest);

    Page<Post> findByIsDeletedFalseAndSubredditIsDeletedFalseAndAndUserUsernameOrderByCreatedDateDesc(String username, Pageable pageable);
}
