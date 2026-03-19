package com.vlz.laborexchange_authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Login credentials")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @Schema(description = "User email address", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid email address.")
    private String email;

    @Schema(description = "User password", example = "SecurePass123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password required.")
    private String password;
}
