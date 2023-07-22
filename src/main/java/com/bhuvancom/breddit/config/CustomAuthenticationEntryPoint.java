package com.bhuvancom.breddit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Set the response status code to 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        log.error("Login failed {}", request.getPathInfo());
        // Set the response body with a custom message
        response.getWriter().write("{\"message\":\"Authentication failed: " + authException.getMessage() + "\"}");
    }
}