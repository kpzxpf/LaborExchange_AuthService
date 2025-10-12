package com.vlz.laborexchange_authservice.service;


import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public String register(RegisterRequest request) {
        //TODO сделать проверку и сохронение юзера

        return null;
    }

    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        //TODO сделать проверку и генирацию токена
        return null;
    }
}