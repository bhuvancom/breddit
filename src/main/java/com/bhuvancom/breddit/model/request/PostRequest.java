package com.bhuvancom.breddit.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private Long postId;
    @NotBlank
    private String postName;
    private String url;
    @NotBlank
    private String description;
}