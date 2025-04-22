🔥 Let’s gooo! You’ve nailed the core services — now it’s time to **lock them down** and make your app **secure and production-ready**.

---

# 🔐 **Phase 4: Authentication & Authorization with Spring Security + JWT**

In this phase, we’ll implement **secure login, registration**, and **JWT-based authentication** using `user-service`.

---

## 🎯 **Goals**
- Create secure login & signup endpoints
- Hash passwords using **BCrypt**
- Generate and validate **JWT tokens**
- Add **Spring Security config** & filters
- Protect endpoints with JWT

---

## 🧱 Step-by-Step Setup in `user-service`

---

### 🔹 Step 1: Add Dependencies (in `user-service/pom.xml`)

```xml
<!-- Spring Security -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>0.9.1</version>
</dependency>

<!-- BCrypt -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter</artifactId>
</dependency>
```

---

### 🔹 Step 2: Update User Entity

📄 `User.java` – Add a **role** field:

```java
private String role; // e.g., ROLE_USER or ROLE_ADMIN
```

---

### 🔹 Step 3: Create DTOs

📄 `dto/UserRequest.java`

```java
@Data
public class UserRequest {
    private String name;
    private String email;
    private String password;
}
```

📄 `dto/AuthRequest.java`

```java
@Data
public class AuthRequest {
    private String email;
    private String password;
}
```

📄 `dto/AuthResponse.java`

```java
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
```

---

### 🔹 Step 4: Create JWT Utility

📄 `util/JwtUtil.java`

```java
package com.shop.user.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "jwt_secret";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, String email) {
        return extractUsername(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
```

---

### 🔹 Step 5: Create AuthController

📄 `controller/AuthController.java`

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder encoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request) {
        if (repo.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("User already exists");

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        repo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword()))
            return ResponseEntity.badRequest().body("Invalid credentials");

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
```

---

### 🔹 Step 6: Add Custom Filter

📄 `config/JwtFilter.java`

```java
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(token);
            } catch (Exception ignored) {}
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && jwtUtil.validateToken(token, email)) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority(user.getRole()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

### 🔹 Step 7: Security Config

📄 `config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .authorizeHttpRequests()
          .requestMatchers("/auth/**").permitAll()
          .anyRequest().authenticated()
          .and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### 🔹 Step 8: Add to `UserRepository`

📄 `UserRepository.java`

```java
Optional<User> findByEmail(String email);
```

---

### 🔹 Step 9: Test Everything

✅ Register: `POST /auth/register`

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "123456"
}
```

✅ Login: `POST /auth/login` → returns JWT

✅ Access protected endpoint like:  
`GET /users` with header:

```
Authorization: Bearer <token>
```

---

### ✅ Final Result

Your `user-service` now has:

✅ Registration  
✅ Secure login  
✅ Password hashing  
✅ Token generation  
✅ Global token validation  
✅ Role-based auth (optional, extend later)

---

### 🔒 Bonus (if you want to take it further):
- Add `@PreAuthorize("hasRole('ROLE_ADMIN')")` for admin-only APIs
- Integrate **JWT verification** in other services using **Auth Feign Client**
- Centralize Auth in a separate `auth-service` (optional, pro-level)

---

You're now ready for Phase 5:  
📦 **Inter-Service Communication with Feign Clients + JWT Propagation**

Let me know when you're ready to start Phase 5!