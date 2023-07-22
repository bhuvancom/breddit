package com.bhuvancom.breddit.model.entity;

import com.bhuvancom.breddit.dto.SubredditDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Subreddit {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String description;
    @OneToMany(fetch = LAZY, mappedBy = "subreddit")
    private List<Post> posts;
    @Column(columnDefinition = "bit DEFAULT 0")
    private boolean isDeleted;
    @CreationTimestamp
    private Instant createdDate;
    @UpdateTimestamp
    private Instant updateDate;
    @ManyToOne(fetch = EAGER)
    private User user;

    public List<Post> getPosts() {
        if(posts ==null) return new ArrayList<>();
        return posts;
    }

    public SubredditDto toSubDto(boolean isMySubreddit) {
        int noOfPost = 0;
        Hibernate.initialize(getUser());
        if (Objects.nonNull(getPosts())) noOfPost = getPosts().size();
        return SubredditDto.builder()
                .id(getId())
                .creator(getUser())
                .creationDate(getCreatedDate())
                .description(getDescription())
                .isMySubreddit(isMySubreddit)
                .name(getName())
                .numberOfPosts((long) noOfPost)
                .build();
    }
}