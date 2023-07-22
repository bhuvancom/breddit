package com.bhuvancom.breddit.repository;

import com.bhuvancom.breddit.dto.SubredditDto;
import com.bhuvancom.breddit.model.entity.Subreddit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
    @Query("SELECT new com.bhuvancom.breddit.dto.SubredditDto(c.id, c.name,c.description, COUNT(b.id),c.user,c.createdDate, false) " +
            " FROM Subreddit c " +
            " JOIN c.user u " +
            " LEFT JOIN c.posts b " +
            " where  c.isDeleted = false" +
            " GROUP BY c"
    )
    Page<SubredditDto> getSubReddits(Pageable pageable);

    Page<Subreddit> findByIsDeletedFalseOrderByCreatedDate(Pageable pageable);

    Page<Subreddit> findByIsDeletedFalseAndUserUsernameOrderByCreatedDateDesc(String username, PageRequest pageRequest);
}
