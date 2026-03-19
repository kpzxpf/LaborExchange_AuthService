package com.vlz.laborexchange_authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                                Handles authentication and JWT token lifecycle for LaborExchange.

                                **Public endpoints** (no JWT required):
                                - `POST /api/auth/register` — create account
                                - `POST /api/auth/login` — get JWT token
                                - `GET /api/auth/validate` — validate token (used internally by Gateway)

                                **JWT claims issued:** `userId`, `role`, `userRole`, `sub` (email)
                                """)
                        .contact(new Contact().name("LaborExchange Team"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Auth").description("Registration, login, and token validation")));
    }
}
