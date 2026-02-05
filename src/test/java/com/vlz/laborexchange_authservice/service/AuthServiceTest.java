package com.vlz.laborexchange_authservice.service;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import com.vlz.laborexchange_authservice.producer.UserRegistrationProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private JwtService jwtService;
    @Mock private UserRegistrationProducer userRegistrationProducer;
    @Mock private UserRetryClient userRetryClient;
    @Mock private RoleRetryClient roleRetryClient;

    @InjectMocks
    private AuthService authService;

    private final String EMAIL = "test@example.com";
    private final String TOKEN = "generated-jwt-token";
    private final Long USER_ID = 1L;
    private final String ROLE = "ROLE_USER";

    @Nested
    @DisplayName("Тесты регистрации (Register)")
    class RegisterTests {

        @Test
        @DisplayName("Успех: создание через Builder и отправка в Producer")
        void register_Success() {
            // Используем Builder для создания запроса
            RegisterRequest request = RegisterRequest.builder()
                    .email(EMAIL)
                    .password("secure_pass")
                    .userRole(ROLE)
                    .build();

            when(userRetryClient.existsUserByEmail(EMAIL)).thenReturn(false);
            when(userRetryClient.getUserIdByEmail(EMAIL)).thenReturn(USER_ID);
            when(jwtService.generateToken(EMAIL, USER_ID, ROLE)).thenReturn(TOKEN);

            String result = authService.register(request);

            assertEquals(TOKEN, result);
            verify(userRegistrationProducer).send(request);
        }

        @Test
        @DisplayName("Ошибка: пользователь уже существует")
        void register_UserAlreadyExists() {
            RegisterRequest request = RegisterRequest.builder()
                    .email(EMAIL)
                    .build();

            when(userRetryClient.existsUserByEmail(EMAIL)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> authService.register(request));
        }
    }

    @Nested
    @DisplayName("Тесты входа (Login)")
    class LoginTests {

        @Test
        @DisplayName("Успех: вход через Builder")
        void login_Success() {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password("password123")
                    .build();

            // По логике вашего кода: false означает, что ошибок нет
            when(userRetryClient.checkLogin(request)).thenReturn(false);
            when(userRetryClient.getUserIdByEmail(EMAIL)).thenReturn(USER_ID);
            when(roleRetryClient.getUserRoleByEmail(EMAIL)).thenReturn(ROLE);
            when(jwtService.generateToken(EMAIL, USER_ID, ROLE)).thenReturn(TOKEN);

            String result = authService.login(request);

            assertEquals(TOKEN, result);
            verify(userRetryClient).checkLogin(any(LoginRequest.class));
        }

        @Test
        @DisplayName("Ошибка: checkLogin вернул true (неверные данные)")
        void login_InvalidCredentials() {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password("wrong_pass")
                    .build();

            when(userRetryClient.checkLogin(request)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> authService.login(request));
        }
    }

    @Nested
    @DisplayName("Тесты валидации (Validate)")
    class ValidateTokenTests {

        @Test
        @DisplayName("Успешная очистка Bearer префикса")
        void validateToken_CleansPrefix() {
            String rawToken = "Bearer secret_jwt";
            when(jwtService.validateToken("secret_jwt")).thenReturn(true);

            boolean result = authService.validateToken(rawToken);

            assertTrue(result);
            verify(jwtService).validateToken("secret_jwt");
        }
    }
}
