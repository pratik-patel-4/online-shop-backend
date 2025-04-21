That’s fantastic! 🎉 Since Phase 2 is done and everything’s wired up beautifully — Eureka, Config, Gateway, Feign — now we’re ready for the **real business logic**. This is where your app starts coming to life!

---

# ✅ **Phase 3: Core Domain Design & Database Integration**

In this phase, we’ll work **microservice by microservice** and focus on:

### 🎯 Goals:
- Defining **domain models** (User, Product, Order, etc.)
- Setting up **PostgreSQL persistence**
- Implementing **service layer & business logic**
- Exposing **REST APIs**
- Testing each service via **Postman or Swagger**

---

## 🧱 Service #1: `user-service` – User Management

---

### 🔹 Step 1: Create Packages

Inside `user-service/src/main/java/com/shop/user/`:

```
user/
├── controller/
├── entity/
├── repository/
├── service/
└── dto/ (optional)
```

---

### 🔹 Step 2: Create User Entity

📄 `entity/User.java`

```java
package com.shop.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
}
```

---

### 🔹 Step 3: Create User Repository

📄 `repository/UserRepository.java`

```java
package com.shop.user.repository;

import com.shop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
```

---

### 🔹 Step 4: Create Service Layer

📄 `service/UserService.java`

```java
package com.shop.user.service;

import com.shop.user.entity.User;
import com.shop.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User saveUser(User user) {
        return repo.save(user);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        repo.deleteById(id);
    }
}
```

---

### 🔹 Step 5: Create Controller

📄 `controller/UserController.java`

```java
package com.shop.user.controller;

import com.shop.user.entity.User;
import com.shop.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(service.saveUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### 🔹 Step 6: Test the Endpoints

Run your `user-service`, and test via:

```
GET     /users
POST    /users
GET     /users/{id}
DELETE  /users/{id}
```

Also test through **API Gateway**:
```
GET http://localhost:8080/users
```

---

## 🧱 Repeat the Same for Other Services

---

## 🔁 `product-service`

### 🔹 Entity: `Product.java`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
```

Create:
- `ProductRepository`
- `ProductService`
- `ProductController`

---

## 🔁 `order-service`

### 🔹 Entity: `Order.java`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;

    private Integer quantity;
    private Double totalPrice;

    private String status;
}
```

Create:
- `OrderRepository`
- `OrderService`
- `OrderController`

💡 Later we will use **Feign** here to fetch `User` and `Product` info for enrichment.

---

## 🧱 PostgreSQL Integration for All Services

Each service should have its own database:

### Example `bootstrap.yml` (in `product-service`):

```yaml
spring:
  application:
    name: product-service
  config:
    import: optional:configserver:http://localhost:8888

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

🔁 Configure DB properties in **your config repo** (e.g., `product-service.yml`)

---

## ✅ What's Next?

Now that you’ve:

✅ Built out domain models  
✅ Hooked up PostgreSQL  
✅ Created REST APIs for all core services  
✅ Connected each service with Config + Eureka

We’re ready for **Phase 4**:

---

# 🔐 **Phase 4: JWT Authentication with Spring Security**

We’ll implement:
- Secure login & registration
- JWT token generation
- Global security filter
- Protecting endpoints
- Role-based access control (optional)

---

Let me know if you're ready, and I’ll guide you through Phase 4: **JWT Auth with Spring Security** step by step 💪