package com.vlz.laborexchange_authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    private final String SECRET = "myVerySecretKeyForJwtGenerationShouldBeLongEnough";
    private final long EXPIRATION = 3600000; // 1 час

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("Генерация токена: должен возвращать непустую строку")
    void generateToken_ShouldReturnValidString() {
        String token = jwtService.generateToken("test@mail.com", 1L, "ROLE_USER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Валидация: корректный токен должен проходить проверку")
    void validateToken_Success() {
        String token = jwtService.generateToken("test@mail.com", 1L, "ROLE_USER");

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Валидация: неверный формат токена должен возвращать false")
    void validateToken_Malformed_ReturnsFalse() {
        String invalidToken = "not.a.real.token";

        boolean isValid = jwtService.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Валидация: просроченный токен должен возвращать false")
    void validateToken_Expired_ReturnsFalse() {
        ReflectionTestUtils.setField(jwtService, "expiration", 0L);

        String token = jwtService.generateToken("test@mail.com", 1L, "ROLE_USER");

        boolean isValid = jwtService.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Валидация: токен, подписанный другим ключом, должен возвращать false")
    void validateToken_WrongSecret_ReturnsFalse() {
        String token = jwtService.generateToken("test@mail.com", 1L, "ROLE_USER");

        ReflectionTestUtils.setField(jwtService, "secret", "AnotherSecretKeyAnotherSecretKeyAnotherSecretKey");

        boolean isValid = jwtService.validateToken(token);

        assertFalse(isValid);
    }
}