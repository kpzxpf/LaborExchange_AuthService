# Auth Service

Microservice responsible for user authentication and JWT token management in the LaborExchange platform.

## Overview

| Property | Value |
|---|---|
| Port | **8081** |
| Base path | `/api/auth` |
| Database | — (stateless, delegates to UserService) |
| External services | UserService (8082) |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Prometheus metrics | http://localhost:8081/actuator/prometheus |

## API Endpoints

| Method | Path | Auth required | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | No | Register a new user account |
| `POST` | `/api/auth/login` | No | Login and receive JWT token |
| `GET` | `/api/auth/validate?token=` | No | Validate a JWT token |

## JWT Token

All tokens are signed with **HS256**. Claims included:

| Claim | Type | Description |
|---|---|---|
| `sub` | String | User email |
| `userId` | Long | User ID from UserService |
| `role` | String | User role (`JOB_SEEKER` or `EMPLOYER`) |
| `userRole` | String | Duplicate of `role` (legacy) |

Token expiration: **1 hour** (configurable via `spring.jwt.expiration`).

## Registration Flow

```
Client → POST /api/auth/register
  → AuthService calls UserService POST /api/users/register (sync Feign)
  → UserService saves user and returns userId
  → AuthService generates JWT with userId + role
  → Returns JWT to client
```

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8081` | HTTP port |
| `spring.jwt.secret` | via env | HS256 signing key (min 32 chars) |
| `spring.jwt.expiration` | `3600000` | Token TTL in ms |
| `spring.clients.user-service.url` | `http://localhost:8082` | UserService base URL |
| `spring.clients.role-service.url` | `http://localhost:8082` | RoleService base URL |

### Running locally

```bash
./gradlew bootRun
```

Make sure UserService is running on port 8082 before starting AuthService.

## Resilience

All calls to UserService go through **Resilience4j circuit breaker + Spring Retry**:
- 3 max retry attempts with 2s backoff
- Circuit breaker opens at 50% failure rate (10-call sliding window)
- Returns `503 Service Unavailable` on fallback

## Error Responses

All errors follow the format:

```json
{
  "error": "Error message",
  "code": 409,
  "timestamp": "2026-03-20T12:00:00"
}
```

| HTTP Code | Meaning |
|---|---|
| `400` | Validation error |
| `409` | Email already exists / invalid credentials |
| `503` | UserService unavailable |
