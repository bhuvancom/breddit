package com.bhuvancom.breddit.model.request;

import com.bhuvancom.breddit.model.entity.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteReq {

    private VoteType voteType;

}
