package com.vlz.laborexchange_authservice.service;

import com.vlz.laborexchange_authservice.client.UserServiceClient;
import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRetryClient {

    private final UserServiceClient userServiceClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "registerUserFallback")
    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public Long registerUser(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        return userServiceClient.registerUser(request);
    }

    public Long registerUserFallback(RegisterRequest request, Exception e) {
        log.warn("UserService circuit breaker open for registerUser, email={}: {}", request.getEmail(), e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @Recover
    public Long recoverRegisterUser(Exception e, RegisterRequest request) {
        log.error("All retry attempts failed for registering user: {}. Error: {}", request.getEmail(), e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserIdByEmailFallback")
    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public Long getUserIdByEmail(String email) {
        log.info("Attempting to get ID for email: {}", email);
        return userServiceClient.getUserIdByEmail(email);
    }

    public Long getUserIdByEmailFallback(String email, Exception e) {
        log.warn("UserService circuit breaker open for getUserIdByEmail, email={}: {}", email, e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "existsUserByEmailFallback")
    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public boolean existsUserByEmail(String email) {
        return userServiceClient.existsUserByEmail(email);
    }

    public boolean existsUserByEmailFallback(String email, Exception e) {
        log.warn("UserService circuit breaker open for existsUserByEmail, email={}: {}", email, e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "checkLoginFallback")
    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:2}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public boolean checkLogin(LoginRequest loginRequest) {
        return userServiceClient.checkLogin(loginRequest);
    }

    public boolean checkLoginFallback(LoginRequest loginRequest, Exception e) {
        log.warn("UserService circuit breaker open for checkLogin: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @Recover
    public Long recoverUserId(Exception e, String email) {
        log.error("All retry attempts failed for email: {}. Error: {}", email, e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @Recover
    public boolean recoverExistsUserByEmail(Exception e, String email) {
        log.error("All retry attempts failed for existsUserByEmail: {}. Error: {}", email, e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }

    @Recover
    public boolean recoverCheckLogin(Exception e, LoginRequest loginRequest) {
        log.error("All retry attempts failed for checkLogin. Error: {}", e.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service is currently unavailable. Please try again later.");
    }
}