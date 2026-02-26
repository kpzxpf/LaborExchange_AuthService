package com.vlz.laborexchange_authservice.service;

import com.vlz.laborexchange_authservice.client.UserServiceClient;
import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRetryClient {

    private final UserServiceClient userServiceClient;

    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public Long registerUser(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        return userServiceClient.registerUser(request);
    }

    @Recover
    public Long recoverRegisterUser(Exception e, RegisterRequest request) {
        log.error("All retry attempts failed for registering user: {}. Error: {}", request.getEmail(), e.getMessage());
        throw new RuntimeException("User Service is currently unavailable. Please try again later.");
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public Long getUserIdByEmail(String email) {
        log.info("Attempting to get ID for email: {}", email);
        return userServiceClient.getUserIdByEmail(email);
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public boolean existsUserByEmail(String email) {
        return userServiceClient.existsUserByEmail(email);
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:2}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public boolean checkLogin(LoginRequest loginRequest) {
        return userServiceClient.checkLogin(loginRequest);
    }

    @Recover
    public Long recoverUserId(Exception e, String email) {
        log.error("All retry attempts failed for email: {}. Error: {}", email, e.getMessage());
        throw new RuntimeException("User Service is currently unavailable. Please try again later.");
    }
}