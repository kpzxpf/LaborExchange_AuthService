package com.vlz.laborexchange_authservice.service;


import com.vlz.laborexchange_authservice.client.UserServiceClient;
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
    private final UserServiceClient userServiceClient;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String register(RegisterRequest request) {
        if (userServiceClient.existsUserByEmail(request.getEmail())) {
            log.error("User with email {} already exists", request.getEmail());
            throw new IllegalStateException("User with email " + request.getEmail() + " already exists");
        }

        request.setPassword(encoder.encode(request.getPassword()));
        log.info("encoded password: {}", request.getPassword());

        userRegistrationProducer.send(request);
        return jwtService.generateToken(request.getEmail());
    }

    public String login(LoginRequest request) {
        request.setPassword(encoder.encode(request.getPassword()));

        if (userServiceClient.checkLogin(request)) {
            log.error("Invalid login request");
            throw new IllegalStateException("Invalid login request");
        }

        log.info("login request: {}", request.getEmail());
        return jwtService.generateToken(request.getEmail());
    }
}