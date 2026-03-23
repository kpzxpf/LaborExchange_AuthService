package com.vlz.laborexchange_authservice.controller;

import com.vlz.laborexchange_authservice.dto.ForgotPasswordRequest;
import com.vlz.laborexchange_authservice.dto.LoginRequest;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import com.vlz.laborexchange_authservice.dto.AuthResponse;
import com.vlz.laborexchange_authservice.dto.ResetPasswordRequest;
import com.vlz.laborexchange_authservice.service.AuthService;
import com.vlz.laborexchange_authservice.service.UserRetryClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Registration, login, and token validation")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRetryClient userRetryClient;

    @Operation(
            summary = "Register a new user",
            description = "Creates a user account in UserService and returns a signed JWT token. Role must be `JOB_SEEKER` or `EMPLOYER`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered, JWT returned",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already registered"),
            @ApiResponse(responseCode = "503", description = "UserService unavailable")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }

    @Operation(
            summary = "Login",
            description = "Validates credentials via UserService and returns a signed JWT token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Invalid credentials"),
            @ApiResponse(responseCode = "503", description = "UserService unavailable")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(AuthResponse.builder().token(token).build());
    }

    @Operation(
            summary = "Validate JWT token",
            description = "Returns `true` if the token is valid and not expired. Used internally by the API Gateway. Accepts token with or without `Bearer ` prefix."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation result (true / false)")
    })
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(
            @Parameter(description = "JWT token (with or without 'Bearer ' prefix)", required = true)
            @RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @Operation(summary = "Verify email by token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email verified"),
            @ApiResponse(responseCode = "400", description = "Token invalid"),
            @ApiResponse(responseCode = "410", description = "Token expired")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @Parameter(description = "Verification token", required = true)
            @RequestParam String token) {
        userRetryClient.verifyEmail(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Request password reset email")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reset email sent if account exists"),
            @ApiResponse(responseCode = "404", description = "No account with that email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        userRetryClient.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset password using token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Token invalid"),
            @ApiResponse(responseCode = "410", description = "Token expired")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userRetryClient.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
