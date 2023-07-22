package com.bhuvancom.breddit.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentReq {
    private Long commentId;
    @Length(min = 2, message = "Comment should be at-least 2 characters long")
    private String commentText;
}
