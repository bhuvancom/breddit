package com.bhuvancom.breddit.model.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String userName;
    private String password;
}
