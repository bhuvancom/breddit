package com.bhuvancom.breddit.model.response;

import com.bhuvancom.breddit.model.entity.User;

public record AuthTokenResponse(String authToken, User user){}
