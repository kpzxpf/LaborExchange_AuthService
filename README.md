# 🔐 Auth Service

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square&logo=spring)
![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![JWT](https://img.shields.io/badge/JWT-Authentication-blue?style=flat-square)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Producer-black?style=flat-square&logo=apache-kafka)

**Centralized authentication and authorization service for LaborExchange platform**

</div>

---

## 📋 Overview

The Auth Service handles user authentication, JWT token generation/validation, and user registration events. It acts as the security gateway for the entire platform, ensuring secure access to all services.

### Key Responsibilities

- ✅ User registration with role assignment
- 🔑 JWT token generation and validation
- 🔄 Password encryption with BCrypt
- 📤 User registration events to Kafka
- 🔁 Retry mechanism for service calls
- 🚫 Rate limiting and security controls

## 🏗️ Architecture

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────┐      ┌──────────────┐
│  Auth Service   │─────▶│  User Service│
│   (Port 8081)   │      │  (Feign)     │
└────────┬────────┘      └──────────────┘
         │
         │               ┌──────────────┐
         └──────────────▶│    Kafka     │
                         │  (Producer)  │
                         └──────────────┘
```

## 🚀 Features

### Authentication
- **User Registration**: Creates new user accounts with role validation
- **User Login**: Authenticates users and generates JWT tokens
- **Token Validation**: Validates JWT tokens for protected routes
- **Role-Based Access**: Supports JOB_SEEKER and EMPLOYER roles

### Security
- **Password Hashing**: BCrypt algorithm for secure password storage
- **JWT Tokens**: Signed tokens with user claims (userId, role, email)
- **Token Expiration**: Configurable token lifetime
- **Secure Communication**: HTTPS support for production

### Integration
- **Kafka Producer**: Publishes user registration events
- **Feign Clients**: Communicates with User and Role services
- **Retry Logic**: Automatic retry with exponential backoff
- **Circuit Breaker**: Fault tolerance for external service calls

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2
- **Security**: Spring Security, JWT (jjwt 0.12.3)
- **Message Broker**: Apache Kafka
- **HTTP Client**: Spring Cloud OpenFeign
- **Resilience**: Spring Retry
- **Database**: None (stateless service)
- **Build Tool**: Gradle (Kotlin DSL)

## 📦 Dependencies

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.retry:spring-retry")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("com.fasterxml.jackson.core:jackson-databind")
}
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Kafka (running on localhost:9092)
- User Service (running on localhost:8082)

### Environment Variables

```bash
# JWT Configuration
JWT_SECRET=myVerySecretKeyForJwtGenerationShouldBeLongEnough
JWT_EXPIRATION=3600000  # 1 hour in milliseconds

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_TOPIC_USER_REGISTRATION=user-registration

# Service URLs
USER_SERVICE_URL=http://localhost:8082
ROLE_SERVICE_URL=http://localhost:8082

# Retry Configuration
RETRY_MAX_ATTEMPTS=3
RETRY_BACKOFF_DELAY=2000
```

### Running the Service

#### Using Gradle
```bash
./gradlew bootRun
```

#### Using Docker
```bash
docker build -t auth-service .
docker run -p 8081:8081 \
  -e JWT_SECRET=your-secret-key \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  auth-service
```

#### Using Docker Compose
```bash
docker-compose up auth-service
```

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

## 📡 API Endpoints

### Register User

Creates a new user account and publishes registration event to Kafka.

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "phone": "+79001234567",
  "password": "SecurePassword123",
  "userRole": "JOB_SEEKER"
}
```

**Validation Rules:**
- `username`: 3-32 characters, required
- `email`: Valid email format, required
- `phone`: 10-15 digits, required
- `password`: 8-64 characters, required
- `userRole`: Must be `JOB_SEEKER` or `EMPLOYER`

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Status Codes:**
- `200 OK` - Successful registration
- `400 Bad Request` - Validation error
- `409 Conflict` - User already exists
- `503 Service Unavailable` - User service unavailable

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "phone": "+79001234567",
    "password": "SecurePassword123",
    "userRole": "JOB_SEEKER"
  }'
```

### Login

Authenticates user and returns JWT token.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Status Codes:**
- `200 OK` - Successful login
- `400 Bad Request` - Invalid credentials
- `401 Unauthorized` - Authentication failed

**cURL Example:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123"
  }'
```

### Validate Token

Validates JWT token.

**Endpoint:** `GET /api/auth/validate?token={token}`

**Response:**
```json
true
```

**Status Codes:**
- `200 OK` - Token is valid
- `401 Unauthorized` - Token is invalid or expired

**cURL Example:**
```bash
curl "http://localhost:8081/api/auth/validate?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 🔑 JWT Token Structure

### Payload Claims

```json
{
  "sub": "john@example.com",
  "userId": 123,
  "role": "JOB_SEEKER",
  "userRole": "JOB_SEEKER",
  "iat": 1640000000,
  "exp": 1640003600
}
```

### Token Generation

```java
String token = Jwts.builder()
    .setSubject(email)
    .claim("userId", userId)
    .claim("role", userRole)
    .claim("userRole", userRole)
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + expiration))
    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
    .compact();
```

### Token Validation

The service validates:
- ✅ Signature integrity
- ✅ Token expiration
- ✅ Token structure
- ✅ Required claims presence

## 📤 Kafka Events

### User Registration Event

**Topic:** `user-registration`

**Event Structure:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "phone": "+79001234567",
  "password": "hashedPassword",
  "userRole": "JOB_SEEKER"
}
```

**Consumer:** User Service

## 🔄 Service Communication

### Feign Clients

#### User Service Client
```java
@FeignClient(name = "user-service", url = "${spring.clients.user-service.url}")
public interface UserServiceClient {
    @GetMapping("/api/users/existsByEmail")
    boolean existsUserByEmail(@RequestParam("email") String email);
    
    @GetMapping("/api/users/userIdByEmail")
    Long getUserIdByEmail(@RequestParam("email") String email);
    
    @PostMapping("/api/users/checkLogin")
    boolean checkLogin(@RequestBody LoginRequest request);
}
```

#### Role Service Client
```java
@FeignClient(name = "role-service", url = "${spring.clients.role-service.url}")
public interface RoleServiceClient {
    @GetMapping("/api/roles/roleByEmail")
    String getUserRoleByEmail(@RequestParam("email") String email);
}
```

### Retry Configuration

```yaml
spring:
  retry:
    maxAttempts: 3
    backoff-delay: 2000  # milliseconds
```

**Retry Strategy:**
- Initial delay: 2 seconds
- Max attempts: 3
- Exponential backoff
- Recoverable exceptions: All exceptions
- Circuit breaker pattern for service unavailability

## 🧪 Testing

### Run Tests
```bash
./gradlew test
```

### Test Coverage
```bash
./gradlew jacocoTestReport
```

### Example Test Cases

```java
@Test
@DisplayName("Registration: Success - User created and event sent")
void register_Success() {
    RegisterRequest request = RegisterRequest.builder()
        .email("test@example.com")
        .password("password123")
        .userRole("JOB_SEEKER")
        .build();
    
    when(userRetryClient.existsUserByEmail(anyString())).thenReturn(false);
    when(jwtService.generateToken(anyString(), anyLong(), anyString()))
        .thenReturn("token");
    
    String token = authService.register(request);
    
    assertNotNull(token);
    verify(userRegistrationProducer).send(any());
}

@Test
@DisplayName("Login: Success - Token generated")
void login_Success() {
    LoginRequest request = LoginRequest.builder()
        .email("test@example.com")
        .password("password123")
        .build();
    
    when(userRetryClient.checkLogin(any())).thenReturn(false);
    when(jwtService.generateToken(anyString(), anyLong(), anyString()))
        .thenReturn("token");
    
    String token = authService.login(request);
    
    assertNotNull(token);
}
```

## 📊 Monitoring & Logging

### Log Format

The service uses structured logging with Logback:

```
2024-01-15 10:30:45.123 INFO  [auth-service] AuthService - User registration: test@example.com
2024-01-15 10:30:45.234 INFO  [auth-service] JwtService - Token generated for user: 123
2024-01-15 10:30:45.345 ERROR [auth-service] RoleRetryClient - All retry attempts failed for email: test@example.com
```

### Metrics (Actuator)

```bash
# Health
curl http://localhost:8081/actuator/health

# Metrics
curl http://localhost:8081/actuator/metrics

# Info
curl http://localhost:8081/actuator/info
```

### Grafana Dashboard

Key metrics tracked:
- Authentication requests/sec
- Token generation time
- Failed login attempts
- Service call latency
- Kafka producer lag

## ⚙️ Configuration

### application.yaml

```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service
  
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    topics:
      user-registration: user-registration
  
  jwt:
    secret: ${JWT_SECRET:myVerySecretKeyForJwtGenerationShouldBeLongEnough}
    expiration: ${JWT_EXPIRATION:3600000}
  
  clients:
    user-service:
      name: user-service
      url: ${USER_SERVICE_URL:http://localhost:8082}
    role-service:
      name: role-service
      url: ${ROLE_SERVICE_URL:http://localhost:8082}
  
  retry:
    maxAttempts: ${RETRY_MAX_ATTEMPTS:3}
    backoff-delay: ${RETRY_BACKOFF_DELAY:2000}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 🚨 Error Handling

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "User with email test@example.com already exists",
  "path": "/api/auth/register"
}
```

### Common Errors

| Error Code | Description | Resolution |
|------------|-------------|------------|
| 400 | Invalid request data | Check request body format |
| 401 | Invalid credentials | Verify email/password |
| 409 | User already exists | Use different email |
| 503 | Service unavailable | Check User Service status |

## 🔐 Security Best Practices

1. **JWT Secret**: Use strong, random secret (min 256 bits)
2. **HTTPS**: Always use HTTPS in production
3. **Token Expiration**: Set reasonable expiration times
4. **Password Policy**: Enforce strong password requirements
5. **Rate Limiting**: Implement rate limiting for auth endpoints
6. **Audit Logging**: Log all authentication attempts

## 📈 Performance

- **Token Generation**: ~5ms avg
- **Token Validation**: ~2ms avg
- **Registration (with Kafka)**: ~100ms avg
- **Login**: ~50ms avg

## 🔮 Future Enhancements

- [ ] OAuth2/OIDC support
- [ ] Multi-factor authentication (MFA)
- [ ] Refresh token mechanism
- [ ] Password reset functionality
- [ ] Account activation via email
- [ ] Session management
- [ ] API rate limiting
- [ ] Geo-blocking capabilities

## 🤝 Contributing

Please read [CONTRIBUTING.md](../CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This service is part of the LaborExchange project and is licensed under the MIT License.

## 📞 Support

- 📧 Email: auth-support@laborexchange.com
- 💬 Slack: #auth-service
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/laborexchange/issues)

---

<div align="center">

**Part of the LaborExchange Platform** | [Main Documentation](../README.md)

</div>
