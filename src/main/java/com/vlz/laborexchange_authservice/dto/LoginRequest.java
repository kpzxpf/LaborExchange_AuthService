package com.vlz.laborexchange_authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid email address.")
    private String email;

    @NotBlank(message = "Password required.")
    private String password;
}