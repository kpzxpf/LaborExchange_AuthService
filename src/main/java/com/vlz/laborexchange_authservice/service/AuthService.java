package com.vlz.laborexchange_authservice.service;


import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import com.vlz.laborexchange_authservice.producer.UserRegistrationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRegistrationProducer userRegistrationProducer;
    private final UserRetryClient userRetryClient;
    private final RoleRetryClient roleRetryClient;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String register(RegisterRequest request) {
        String email = request.getEmail();

        if (userRetryClient.existsUserByEmail(email)) {
            log.error("User with email {} already exists", email);
            throw new IllegalStateException("User with email " + email + " already exists");
        }

        request.setPassword(encoder.encode(request.getPassword()));
        log.info("encoded password: {}", request.getPassword());

        userRegistrationProducer.send(request);
        return jwtService.generateToken(email, userRetryClient.getUserIdByEmail(email), request.getUserRole());
    }

    public String login(LoginRequest request) {
        request.setPassword(encoder.encode(request.getPassword()));
        String email = request.getEmail();

        if (userRetryClient.checkLogin(request)) {
            log.error("Invalid login request");
            throw new IllegalStateException("Invalid login request");
        }

        log.info("login request: {}", email);
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