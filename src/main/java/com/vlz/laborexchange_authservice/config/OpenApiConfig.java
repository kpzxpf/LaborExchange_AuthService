package com.vlz.laborexchange_authservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0.0")
                        .description("""
                                Handles authentication and JWT token lifecycle.

                                **Public endpoints** (no JWT required):
                                - `POST /api/auth/register` — create account
                                - `POST /api/auth/login` — obtain JWT token
                                - `GET /api/auth/validate` — validate token (called internally by API Gateway)

                                **JWT claims issued:** `userId`, `role`, `userRole`, `sub` (email)

                                Token algorithm: **HS256**, expiry: **1 hour** (configurable).
                                """)
                        .license(new License().name("MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained via POST /api/auth/login")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Auth").description("Registration, login, and token validation")));
    }
}
