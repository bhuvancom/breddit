package com.bhuvancom.breddit.dto;

import com.bhuvancom.breddit.model.entity.Subreddit;
import com.bhuvancom.breddit.model.entity.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubredditDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    private Long numberOfPosts;
    private User creator;
    private Instant creationDate;
    private boolean isMySubreddit;

    public Subreddit toSub() {
        return Subreddit.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}
