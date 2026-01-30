package com.vlz.laborexchange_authservice.controller;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import com.vlz.laborexchange_authservice.dto.AuthResponse;
import com.vlz.laborexchange_authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        String token = authService.register(request);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .build());
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}