package com.vlz.laborexchange_authservice.service;

import com.vlz.laborexchange_authservice.client.RoleServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleRetryClient {
    private final RoleServiceClient roleServiceClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserRoleByEmailFallback")
    @Retryable(
            retryFor = { Exception.class },
            maxAttemptsExpression = "${spring.retry.maxAttempts:3}",
            backoff = @Backoff(delayExpression = "${spring.retry.backoff-delay:2000}")
    )
    public String getUserRoleByEmail(String email) {
        return roleServiceClient.getUserRoleByEmail(email);
    }

    public String getUserRoleByEmailFallback(String email, Exception e) {
        log.warn("UserService circuit breaker open for getUserRoleByEmail, email={}: {}", email, e.getMessage());
        throw new RuntimeException("User Service is currently unavailable. Please try again later.");
    }

    @Recover
    public Long recoverUserId(Exception e, String email) {
        log.error("All retry attempts failed for email: {}. Error: {}", email, e.getMessage());
        throw new RuntimeException("User Service is currently unavailable. Please try again later.");
    }
}
