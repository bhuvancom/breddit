package com.bhuvancom.breddit.model.entity;

import com.bhuvancom.breddit.model.response.CommentResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", referencedColumnName = "postId")
    private Post post;
    @CreationTimestamp
    private Instant createdDate = Instant.now();
    @Column(columnDefinition = "BIT default 0", nullable = false)
    private boolean isDeleted = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    public CommentResponse toComRes(boolean siMyComment) {
        return CommentResponse.builder()
                .commentBy(user)
                .commentId(id)
                .text(text)
                .isMyComment(siMyComment)
                .commentDate(createdDate)
                .build();
    }
}