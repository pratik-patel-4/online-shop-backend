🔥 That’s huge! Phase 4 is one of the toughest and you’ve crushed it. Now, it’s time to make your microservices **talk to each other securely**. This is where your architecture starts to **feel real-world enterprise level**.

---

# 🔁 **Phase 5: Inter-Service Communication via Feign Clients + JWT Propagation**

---

## 🎯 **Goals**
- Secure communication **between microservices**
- Use **Feign Clients** with **JWT tokens**
- Ensure only authenticated requests are allowed internally
- Avoid code duplication by **centralizing token forwarding**

---

## 🧠 Real-Life Use Case:
Let’s make `order-service` talk to:
- `user-service` to fetch user info
- `product-service` to fetch product info

And both of those services are **protected with JWT**, so we need to **pass the token** along with the request.

---

# 🔧 Step-by-Step Guide

---

## 🔹 Step 1: Create Feign Clients in `order-service`

📁 `order-service/client/UserClient.java`

```java
@FeignClient(name = "user-service", configuration = FeignClientInterceptor.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
```

📁 `order-service/client/ProductClient.java`

```java
@FeignClient(name = "product-service", configuration = FeignClientInterceptor.class)
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
}
```

---

## 🔹 Step 2: Create DTOs for Response Mapping

📁 `dto/UserDTO.java`

```java
@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
}
```

📁 `dto/ProductDTO.java`

```java
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
}
```

---

## 🔹 Step 3: Add Token Forwarding Logic

📁 `config/FeignClientInterceptor.java`

```java
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Get JWT token from current security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() != null) {
            String token = auth.getCredentials().toString();
            template.header("Authorization", "Bearer " + token);
        }
    }
}
```

> 🧠 This **automatically attaches the current JWT** to any outgoing Feign request. Beautifully decoupled!

---

## 🔹 Step 4: Inject Clients in `OrderService.java`

```java
@Service
public class OrderService {

    private final OrderRepository repo;
    private final UserClient userClient;
    private final ProductClient productClient;

    public OrderService(OrderRepository repo, UserClient userClient, ProductClient productClient) {
        this.repo = repo;
        this.userClient = userClient;
        this.productClient = productClient;
    }

    public Order placeOrder(Order order) {
        // Validate user
        UserDTO user = userClient.getUserById(order.getUserId());

        // Validate product
        ProductDTO product = productClient.getProductById(order.getProductId());

        // Calculate total price
        order.setTotalPrice(product.getPrice() * order.getQuantity());
        order.setStatus("PLACED");

        return repo.save(order);
    }

    // Other methods...
}
```

---

## 🔹 Step 5: Make Order Endpoint Secure

📁 In `SecurityConfig.java` of `order-service`, protect the endpoints:

```java
http
  .csrf().disable()
  .authorizeHttpRequests()
  .requestMatchers("/orders/**").authenticated()
  .anyRequest().permitAll()
  .and()
  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
```

---

## 🔹 Step 6: Test Everything!

1. Use `/auth/login` in `user-service` to get JWT
2. Use that token to call:
    - `POST /orders` with body:

```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

✅ Token flows to user/product services automatically  
✅ Order service gets enriched data and stores order  
✅ Secure. Seamless. Sweet.

---

## 🧰 Optional Enhancements (Advanced):

- ✅ Implement **inventory check** in `inventory-service`
- ✅ Reduce product stock after order
- ✅ Create a **shared auth module** for token utilities
- ✅ Cache token validation for performance (using Redis)

---

# ✅ Phase 5 Summary

| What You Achieved                              | ✅ |
|------------------------------------------------|----|
| Secure inter-service communication             | ✅ |
| Feign clients setup for user/product services  | ✅ |
| JWT token propagation across microservices     | ✅ |
| Token forwarding decoupled using interceptor   | ✅ |
| Order creation with real-time validation       | ✅ |

---

Ready for Phase 6?

## 🧾 **Phase 6: Payment Integration + Inventory Management**

Let me know and I’ll take you into integrating:
- A mock **payment service**
- Inventory sync after successful order
- Use **event-based** (optional) or **direct service call**

This is where the app **feels complete** 💯