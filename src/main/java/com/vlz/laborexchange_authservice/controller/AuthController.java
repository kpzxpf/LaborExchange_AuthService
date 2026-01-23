package com.vlz.laborexchange_authservice.controller;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import com.vlz.laborexchange_authservice.dto.ResponseAuth;
import com.vlz.laborexchange_authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseAuth> register(@RequestBody RegisterRequest request) {
        String token = authService.register(request);

        return ResponseEntity.ok(ResponseAuth.builder()
                .token(token)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseAuth> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        return ResponseEntity.ok(ResponseAuth.builder()
                .token(token)
                .build());
    }
}