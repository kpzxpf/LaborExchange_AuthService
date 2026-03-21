# Auth Service

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Port](https://img.shields.io/badge/port-8081-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Stateless microservice responsible for user authentication and JWT token lifecycle management.

## Table of Contents

- [Overview](#overview)
- [API Endpoints](#api-endpoints)
- [JWT Token](#jwt-token)
- [Registration Flow](#registration-flow)
- [Configuration](#configuration)
- [Resilience](#resilience)
- [Error Responses](#error-responses)
- [Running Locally](#running-locally)

## Overview

| Property | Value |
|---|---|
| Port | **8081** |
| Base path | `/api/auth` |
| Database | None — stateless, delegates to UserService |
| Dependencies | UserService (8082) |
| Swagger UI | `http://localhost:8081/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8081/v3/api-docs` |
| Prometheus | `http://localhost:8081/actuator/prometheus` |

## API Endpoints

All endpoints are public — no JWT required.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user account, returns JWT |
| `POST` | `/api/auth/login` | Login with credentials, returns JWT |
| `GET` | `/api/auth/validate?token=` | Validate a JWT token (used by Gateway) |

### POST /api/auth/register

**Request body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "phone": "+79001234567",
  "password": "secret1234",
  "userRole": "JOB_SEEKER"
}
```

| Field | Constraints |
|---|---|
| `username` | 3–32 chars, required, unique |
| `email` | Valid email, required |
| `phone` | 10–15 digits, `+` prefix supported |
| `password` | 8–64 chars |
| `userRole` | `JOB_SEEKER` or `EMPLOYER` |

**Response `200 OK`:**
```json
{ "token": "<JWT>" }
```

### POST /api/auth/login

**Request body:**
```json
{ "email": "john@example.com", "password": "secret1234" }
```

**Response `200 OK`:**
```json
{ "token": "<JWT>" }
```

### GET /api/auth/validate

**Query parameter:** `token` — raw token or `Bearer <token>`

**Response `200 OK`:** `true` or `false`

## JWT Token

Tokens are signed with **HS256**. Payload claims:

| Claim | Type | Description |
|---|---|---|
| `sub` | String | User email |
| `userId` | Long | User ID |
| `role` | String | `JOB_SEEKER` or `EMPLOYER` |
| `userRole` | String | Duplicate of `role` (legacy) |

Expiry: **1 hour** (configurable via `spring.jwt.expiration`, in milliseconds).

## Registration Flow

```
Client → POST /api/auth/register
  → AuthService → POST /api/users/register (Feign, synchronous)
  → UserService saves user, returns userId
  → AuthService generates JWT {userId, role}
  → Returns JWT to client
```

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8081` | HTTP port |
| `spring.jwt.secret` | env var | HS256 signing key (min 32 chars) |
| `spring.jwt.expiration` | `3600000` | Token TTL in milliseconds |
| `spring.clients.user-service.url` | `http://localhost:8082` | UserService base URL |

## Resilience

Calls to UserService use **Resilience4j circuit breaker + Spring Retry**:

- Max 3 retry attempts, 2 s backoff
- Circuit opens at 50% failure rate (10-call sliding window)
- Fallback: `503 Service Unavailable`

## Error Responses

All error responses follow a unified format:

```json
{
  "error": "Email already registered",
  "code": 409,
  "timestamp": "2026-03-20T12:00:00"
}
```

| Status | Meaning |
|---|---|
| `400` | Validation error |
| `409` | Email already exists / invalid credentials |
| `503` | UserService unavailable |

## Running Locally

Ensure UserService is running on port `8082`, then:

```bash
./gradlew bootRun
```
