Awesome! Let's dive straight into **Phase 1: Project Setup** for your **Online Shopping Microservices Application** with Java 17, Spring Boot, PostgreSQL, and Microservices Architecture.

---

## 🧱 **Phase 1: Project Setup (Detailed Step-by-Step)**

This phase sets the foundation of your entire application — folder structure, basic configurations, and setting up the key components you'll build on in later phases.

---

## ✅ **Step 1: Create a New GitHub Repository**

> Keep all your code version-controlled and clean.

- Create a new private/public GitHub repository:  
  **`online-shopping-microservices`**
- Structure each microservice as a separate sub-module or sub-directory.
- Initialize with a `.gitignore` for **Java**, **Maven**, and **IntelliJ IDEA**.

```bash
echo ".idea/
*.iml
target/
logs/
.env
" >> .gitignore
git init
git remote add origin <your-repo-url>
```

---

## ✅ **Step 2: Create a Parent Project Using Maven**

We'll use **multi-module Maven project** for microservices.

```bash
mkdir online-shopping-microservices
cd online-shopping-microservices
mvn archetype:generate -DgroupId=com.shop -DartifactId=shopping-backend -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
cd shopping-backend
```

### Now modify `pom.xml` to make it a **parent POM**:

```xml
<packaging>pom</packaging>

<modules>
    <module>user-service</module>
    <module>product-service</module>
    <module>order-service</module>
    <module>payment-service</module>
    <module>inventory-service</module>
    <module>api-gateway</module>
    <module>eureka-server</module>
    <module>config-server</module>
    <module>notification-service</module>
</modules>
```

Add common dependencies & plugin management (Spring Boot version, etc.)

---

## ✅ **Step 3: Setup Common Properties in Parent POM**

Add Spring Boot dependencies and plugin management:

```xml
<properties>
    <java.version>17</java.version>
    <spring.boot.version>3.1.0</spring.boot.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring.boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

---

## ✅ **Step 4: Create Microservice Modules**

Run these commands inside your main project directory:

```bash
mvn archetype:generate -DgroupId=com.shop.user -DartifactId=user-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn archetype:generate -DgroupId=com.shop.product -DartifactId=product-service -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
# Repeat for other services (order-service, payment-service, etc.)
```

Each service should have its own:
- `pom.xml`
- `src/main/java/com/shop/<service>/...`
- `application.yml` or `application.properties`

---

## ✅ **Step 5: Setup Spring Boot in Each Service**

Go into one module (e.g., `user-service`) and update `pom.xml` like:

```xml
<parent>
    <groupId>com.shop</groupId>
    <artifactId>shopping-backend</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>

<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

---

## ✅ **Step 6: Basic Application Bootstrap**

In `user-service/src/main/java/com/shop/user/UserServiceApplication.java`:

```java
package com.shop.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

Add `application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: your_pg_user
    password: your_pg_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Create a simple `UserController` and test with a basic REST endpoint:

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/test")
    public String test() {
        return "User Service is up!";
    }
}
```

---

## ✅ **Step 7: Run the Service**

```bash
cd user-service
mvn spring-boot:run
```

Visit: [http://localhost:8081/users/test](http://localhost:8081/users/test)

You should see:
```
User Service is up!
```

---

## ✅ **Step 8: Repeat Setup for Other Services**

Repeat steps 5–7 for:
- `product-service` (port 8082)
- `order-service` (port 8083)
- `payment-service` (8084)
- `inventory-service` (8085)
- And so on...

---

## ✅ **Step 9: Prepare for Config Server & Eureka Server**

Just create the folders for now:
- `config-server`
- `eureka-server`
- `api-gateway`

We’ll set them up in **Phase 2**.

---

## ✅ **Step 10: Commit & Push**

```bash
git add .
git commit -m "Initial multi-module microservices setup"
git push origin main
```

---

## 🎯 What We’ve Achieved in Phase 1:

- Set up a **clean Maven multi-module project**
- Initialized **multiple Spring Boot microservices**
- Configured PostgreSQL connection (for one service)
- Tested REST endpoint successfully
- Ready for adding discovery, config, and API gateway in Phase 2!

---

Ready to move on to **Phase 2: Microservice Core Setup** where we:
- Setup **Eureka Discovery**
- Build the **Config Server**
- Connect services via **OpenFeign**
- Setup **API Gateway** and **route all services**

Let me know and we’ll continue from here. 🚀