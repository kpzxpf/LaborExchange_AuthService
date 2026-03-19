package com.vlz.laborexchange_authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "JWT token response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    @Schema(description = "Signed JWT token (HS256). Expires in 1 hour.", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
