Nice work! 🔥 Now that the project structure and basic services are in place, let’s move on to:

---

# ✅ **Phase 2: Core Microservice Infrastructure Setup**

This phase is about building the **spine** of your microservices architecture:

1. 🔎 **Eureka Server** (Service Discovery)
2. 🛠️ **Config Server** (Centralized configuration)
3. 🌐 **API Gateway** (Single entry point)
4. 🔁 **Register services with Eureka**
5. 🔗 **Test inter-service communication with OpenFeign**

---

## ✅ Step 1: Set Up **Eureka Server**

### 📁 Navigate to your `eureka-server` module and update `pom.xml`:

```xml
<parent>
    <groupId>com.shop</groupId>
    <artifactId>shopping-backend</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

---

### 🧠 Add `application.yml` for Eureka:

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

### 🧠 Create Main Class `EurekaServerApplication.java`:

```java
package com.shop.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

---

### ▶️ Run Eureka Server:

```bash
cd eureka-server
mvn spring-boot:run
```

📍 Open [http://localhost:8761](http://localhost:8761) — you’ll see the Eureka Dashboard!

---

## ✅ Step 2: Set Up **Config Server**

### 📁 Inside `config-server`, update `pom.xml`:

```xml
<parent>
    <groupId>com.shop</groupId>
    <artifactId>shopping-backend</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

---

### 🧠 Create a Git-backed configuration:

1. Create a **new Git repo** (e.g. `online-shopping-config`)
2. Inside it, create a config file like:

📄 `user-service.yml`

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: your_pg_user
    password: your_pg_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Push this config repo to GitHub (public or private with token access).

---

### 🧠 Now configure your `application.yml` in `config-server`:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-username/online-shopping-config
          default-label: main
          clone-on-start: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

### 🧠 Enable Config Server in Main Class:

```java
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

---

### ▶️ Run the Config Server:

```bash
cd config-server
mvn spring-boot:run
```

🧪 Test:  
Visit [http://localhost:8888/user-service/default](http://localhost:8888/user-service/default) — it should return the configuration.

---

## ✅ Step 3: Register Services with Eureka & Use Config Server

### In each service (`user-service`, `product-service`, etc.), do the following:

🔧 Update dependencies in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

---

🔧 Change `application.yml` to `bootstrap.yml` (for config loading priority):

```yaml
spring:
  application:
    name: user-service
  config:
    import: optional:configserver:http://localhost:8888

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

▶️ Run your service again:

```bash
cd user-service
mvn spring-boot:run
```

📍 Go back to [Eureka Dashboard](http://localhost:8761) — you should now see `user-service` registered!

---

## ✅ Step 4: Setup API Gateway

### 📁 In `api-gateway`, update `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

---

### 🧠 `application.yml` for API Gateway:

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/users/**

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

📝 `lb://` means **load-balanced URI from Eureka**

---

### ▶️ Run the API Gateway:

```bash
cd api-gateway
mvn spring-boot:run
```

🔗 Test your user-service through the API Gateway:  
Visit → [http://localhost:8080/users/test](http://localhost:8080/users/test)

---

## ✅ Step 5: Test Inter-Service Communication with OpenFeign

Let’s say `order-service` needs to call `user-service`.

### In `order-service`:

📦 Add Feign dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

🔧 Enable Feign Client in main class:

```java
@EnableFeignClients
@SpringBootApplication
public class OrderServiceApplication { ... }
```

🔧 Create a Feign client:

```java
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/test")
    String getUserTest();
}
```

Use it in a controller or service:

```java
@RestController
public class OrderController {

    @Autowired
    private UserClient userClient;

    @GetMapping("/orders/test-user")
    public String getFromUser() {
        return userClient.getUserTest();
    }
}
```

▶️ Run `order-service`, hit:  
[http://localhost:8080/orders/test-user](http://localhost:8080/orders/test-user)

🎉 You should see the response from `user-service`.

---

## 🎯 Summary of Phase 2:

✅ Eureka Server running and services registered  
✅ Centralized config with Spring Cloud Config  
✅ API Gateway routes requests to proper services  
✅ Services talk to each other with OpenFeign

---

Ready to move to **Phase 3: Core Domain Design & Database Integration**?

We'll build:
- Domain Models (User, Product, Order, etc.)
- Repositories
- Services
- Actual data persistence with PostgreSQL

Let me know and I’ll guide you right into it. 🚀