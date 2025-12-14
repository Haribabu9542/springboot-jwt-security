# springboot-jwt-security

[![Build Status](https://img.shields.io/badge/build-pending-lightgrey)](https://github.com/Haribabu9542/springboot-jwt-security/actions)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.x%20%7C%203.x-green)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-11%2B-important)](https://adoptium.net/)

A simple, opinionated Spring Boot example demonstrating authentication and authorization using JSON Web Tokens (JWT). The project shows how to:

- Authenticate users and issue JWT access tokens (and optionally refresh tokens)
- Secure REST endpoints with role-based access control
- Store user credentials with BCrypt hashing
- Validate and parse JWTs on every request using a security filter
- Provide sample controllers for public and protected resources

This README contains quickstart instructions, configuration samples, API examples, and recommended production notes.

Table of contents
- Quickstart
- Features
- Prerequisites
- Build & Run
- Example configuration (application.yml)
- API endpoints & examples
- Key implementation notes
  - JWT generation & validation
  - Security configuration
  - Password hashing
- Docker
- Tests & CI
- Production considerations
- Contributing
- License

Quickstart
1. Clone the repository:
   ```
   git clone https://github.com/Haribabu9542/springboot-jwt-security.git
   cd springboot-jwt-security
   ```

2. Build the application (Maven):
   ```
   mvn clean package
   ```

3. Run:
   ```
   java -jar target/springboot-jwt-security-*.jar
   ```

4. Use the example API endpoints (see below) to authenticate and access protected routes.

Features
- Login endpoint that returns a JWT access token (and optionally refresh token)
- Filter-based JWT validation for incoming requests
- Role-based authorization (e.g., ROLE_USER, ROLE_ADMIN)
- Example in-memory user store (easy to replace with JPA or external user service)
- Clear configuration (application.yml) with token properties
- Example controllers demonstrating public & protected resources

Prerequisites
- Java 11 or newer (Java 17 recommended)
- Maven (or your IDE's build support)
- (Optional) Docker & Docker Compose for container runs

Build & Run
- Build:
  ```
  mvn -DskipTests package
  ```
- Run locally:
  ```
  java -jar target/springboot-jwt-security-0.0.1-SNAPSHOT.jar
  ```
- By default the app runs on port 8080. Change with `server.port` in application.yml or env vars.

Example configuration (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: springboot-jwt-security

security:
  jwt:
    secret: replace_this_with_a_secure_random_value_of_sufficient_length
    access-token-expiration-ms: 900000    # 15 minutes
    refresh-token-expiration-ms: 2592000000 # 30 days (optional)
    issuer: demo-app
```

Maven dependencies (important ones)
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

API endpoints & examples
- POST /api/auth/login
  - Request:
    ```json
    {
      "username": "user",
      "password": "password"
    }
    ```
  - Response:
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 900000
    }
    ```

- (Optional) POST /api/auth/refresh
  - Accepts a refresh token and issues a new access token.

- GET /api/public
  - Public resource; no token required.

- GET /api/user
  - Requires ROLE_USER or ROLE_ADMIN; send Authorization header:
    Authorization: Bearer <accessToken>

- GET /api/admin
  - Requires ROLE_ADMIN.

cURL examples
1. Login and get token:
   ```
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"user","password":"password"}'
   ```

2. Access protected route:
   ```
   curl http://localhost:8080/api/user \
     -H "Authorization: Bearer eyJhbGciOi..."
   ```

Key implementation notes

JWT generation & validation
- Tokens are signed using an HMAC secret (HS256) or can be configured to use asymmetric keys (RS256) for production.
- Recommended fields inside token:
  - sub (subject) — username or user id
  - roles (claim) — roles/authorities
  - iat, exp, iss
- Use a secure randomly generated secret (or better: use RSA key pair and keep private key secret).
- Sample JwtUtil responsibilities:
  - createAccessToken(username, roles)
  - validateToken(token)
  - getUsernameFromToken(token)
  - getRolesFromToken(token)

Security configuration
- A typical WebSecurityConfigurerAdapter / SecurityFilterChain config:
  - Permit anonymous access to /api/auth/** and /api/public
  - Require authentication for /api/user/** and /api/admin/**
  - Add a JwtAuthenticationFilter (once-per-request) that:
    - Extracts the Bearer token from Authorization header
    - Validates the token and loads Authentication into SecurityContext

Password hashing
- Store passwords hashed with BCryptPasswordEncoder:
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  ```
- For demo users, use encoder.encode("password") when seeding.

Example SecurityFilter skeleton
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res);
    }
}
```

Docker (optional)
- Example Dockerfile:
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/springboot-jwt-security-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

- Build and run:
  ```
  docker build -t springboot-jwt-security .
  docker run -p 8080:8080 -e JWT_SECRET=super-secret springboot-jwt-security
  ```

Tests & CI
- Include unit tests for:
  - JwtUtil (create, validate, parse claims)
  - Authentication flow (login controller)
  - Security config (protected endpoints)
- Use mock UserDetailsService or in-memory test users for fast tests.
- CI pipeline should run mvn -B -DskipTests=false test and build.

Production considerations
- Use asymmetric signing (RS256) with secure key management (KMS / Vault) in production.
- Set short-lived access tokens (minutes) and use refresh tokens with rotation and revocation.
- Secure the secret/key and never commit it to VCS.
- Consider token blacklisting or persistence for refresh token revocation on logout.
- Add rate limiting on auth endpoints to prevent brute-force attacks.
- Use HTTPS everywhere (TLS termination).

Contributing
Contributions, issues and feature requests are welcome. Suggested workflow:
1. Fork the repository
2. Create a branch (feature/your-feature)
3. Add tests for new functionality
4. Open a pull request describing the changes

License
This project is licensed under the MIT License — see the LICENSE file for details.

Contact
Maintainer: Haribabu9542
Repo: https://github.com/Haribabu9542/springboot-jwt-security
