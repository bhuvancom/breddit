package com.bhuvancom.breddit.model.entity;

import com.bhuvancom.breddit.model.IsMy;
import com.bhuvancom.breddit.model.response.PostResponse;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.jsoup.Jsoup;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @NotBlank(message = "Post name can not be empty or null")
    private String postName;
    @Nullable
    private String url;
    @Nullable
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, columnDefinition = "DEFAULT 0")
    @Builder.Default
    private Integer voteCount = 0;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    @CreationTimestamp
    private Instant createdDate;
    @Column(columnDefinition = "bit DEFAULT 0")
    private boolean isDeleted;
    @UpdateTimestamp
    private Instant updateDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subreddit subreddit;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "is_deleted = false")
    private List<Comment> comments = new ArrayList<>();

    public List<Comment> getComments() {
        if (comments == null) comments = new ArrayList<>();
        return comments;
    }

    public PostResponse toPostRes(boolean addSubReddit, IsMy isMy, boolean isList) {
        Post p = this;
        Hibernate.initialize(p.getUser());
        PostResponse.PostResponseBuilder postResponseBuilder = PostResponse.builder()
                .title(p.getPostName())
                .user(p.getUser())
                .postId(p.getPostId())
                .voteCount(p.getVoteCount())
                .description(isList ? getShortDesc() : p.getDescription())
                .isMyPost(isMy.isPost())
                .isMyUpVote(isMy.isUpVote())
                .isMyDownVote(isMy.isDownVote())
                .creationDate(p.getCreatedDate())
                .commentCount(p.getComments().size());
        if (addSubReddit)
            postResponseBuilder.subreddit(p.getSubreddit().toSubDto(isMy.isSubreddit()));
        return postResponseBuilder.build();
    }

    private String getShortDesc() {
        // If the current view is for the list, send the shortened description
        String truncatedDescription = StringUtils.abbreviate(Jsoup.parse(getDescription()).text(), 200);
        return truncatedDescription;
    }
}