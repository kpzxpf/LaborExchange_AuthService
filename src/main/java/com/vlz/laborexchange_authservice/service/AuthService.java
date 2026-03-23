package com.vlz.laborexchange_authservice.service;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRetryClient userRetryClient;
    private final RoleRetryClient roleRetryClient;

    public String register(RegisterRequest request) {
        String email = request.getEmail();

        if (userRetryClient.existsUserByEmail(email)) {
            log.warn("Registration attempt with already existing email: {}", email);
            throw new IllegalStateException("User with email " + email + " already exists");
        }

        Long userId = userRetryClient.registerUser(request);
        log.info("User registered: id={} email={}", userId, email);

        return jwtService.generateToken(email, userId, request.getUserRole());
    }

    public String login(LoginRequest request) {
        String email = request.getEmail();

        if (!userRetryClient.checkLogin(request)) {
            log.warn("Failed login attempt for email: {}", email);
            throw new IllegalStateException("Invalid login request");
        }

        log.info("User logged in successfully: {}", email);

        return jwtService.generateToken(
                email, userRetryClient.getUserIdByEmail(email), roleRetryClient.getUserRoleByEmail(email));
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        return jwtService.validateToken(cleanToken);
    }
}
