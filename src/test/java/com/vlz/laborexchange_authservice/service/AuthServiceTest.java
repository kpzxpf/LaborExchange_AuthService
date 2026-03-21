package com.vlz.laborexchange_authservice.service;

import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private JwtService jwtService;
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
        @DisplayName("Успех: регистрация через REST и получение токена")
        void register_Success() {
            RegisterRequest request = RegisterRequest.builder()
                    .email(EMAIL)
                    .password("secure_pass")
                    .userRole(ROLE)
                    .build();

            when(userRetryClient.existsUserByEmail(EMAIL)).thenReturn(false);
            when(userRetryClient.registerUser(request)).thenReturn(USER_ID);
            when(jwtService.generateToken(EMAIL, USER_ID, ROLE)).thenReturn(TOKEN);

            String result = authService.register(request);

            assertEquals(TOKEN, result);
            verify(userRetryClient).registerUser(request);
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
        @DisplayName("Успех: вход с корректными данными")
        void login_Success() {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password("password123")
                    .build();

            // checkLogin возвращает true когда данные корректны
            when(userRetryClient.checkLogin(request)).thenReturn(true);
            when(userRetryClient.getUserIdByEmail(EMAIL)).thenReturn(USER_ID);
            when(roleRetryClient.getUserRoleByEmail(EMAIL)).thenReturn(ROLE);
            when(jwtService.generateToken(EMAIL, USER_ID, ROLE)).thenReturn(TOKEN);

            String result = authService.login(request);

            assertEquals(TOKEN, result);
            verify(userRetryClient).checkLogin(any(LoginRequest.class));
        }

        @Test
        @DisplayName("Ошибка: checkLogin вернул false (неверные данные)")
        void login_InvalidCredentials() {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password("wrong_pass")
                    .build();

            // checkLogin возвращает false когда данные неверны
            when(userRetryClient.checkLogin(request)).thenReturn(false);

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
