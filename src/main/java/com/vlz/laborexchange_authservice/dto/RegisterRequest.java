package com.vlz.laborexchange_authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(description = "New user registration request")
@Data
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @Schema(description = "Unique username (3–32 characters)", example = "ivan_petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters.")
    private String username;

    @Schema(description = "Email address — used as JWT subject", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid email address.")
    private String email;

    @Schema(description = "Phone number (10–15 digits, optional + prefix)", example = "+79161234567", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid and contain 10 to 15 digits.")
    private String phone;

    @Schema(description = "Password (8–64 characters)", example = "SecurePass123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters.")
    private String password;

    @Schema(description = "User role", example = "JOB_SEEKER", allowableValues = {"JOB_SEEKER", "EMPLOYER"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Role is required.")
    @Pattern(regexp = "^(JOB_SEEKER|EMPLOYER)$", message = "The role must be JOB_SEEKER or EMPLOYER.")
    private String userRole;
}
