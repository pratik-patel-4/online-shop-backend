You're on fire! 🔥 Phase 5 was enterprise-grade stuff — token-secured service-to-service talk — and now it’s time to finish the full shopping workflow. Welcome to:

---

# 💸 **Phase 6: Payment Integration + Inventory Management**

This phase connects the final pieces of your backend:
- 💳 Payment validation logic (mock/real)
- 📦 Stock management after order placement
- Ensuring **consistency** between services

---

## 🎯 **Goals**
- Create `payment-service` and `inventory-service`
- Integrate them with `order-service`
- Deduct product stock after successful payment
- Mark order as `COMPLETED`, `FAILED`, or `OUT_OF_STOCK`
- Keep all communication **secured via JWT + Feign**

---

## 🏗️ Step-by-Step Implementation

---

# 🧾 **Part A: payment-service**

---

### 🔹 Step 1: Create Project & Setup Structure

Folder structure inside `payment-service/src/main/java/com/shop/payment`:

```
payment/
├── controller/
├── entity/
├── repository/
├── service/
```

---

### 🔹 Step 2: Add Payment Entity

📄 `entity/Payment.java`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String paymentStatus; // e.g., SUCCESS, FAILED
    private Double amount;
}
```

---

### 🔹 Step 3: Repository & Service

📄 `repository/PaymentRepository.java`

```java
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
```

📄 `service/PaymentService.java`

```java
@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public Payment makePayment(Long orderId, Double amount) {
        // Mock logic
        String status = (amount > 0) ? "SUCCESS" : "FAILED";

        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentStatus(status)
                .build();

        return repo.save(payment);
    }
}
```

---

### 🔹 Step 4: Payment Controller

📄 `controller/PaymentController.java`

```java
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<Payment> pay(@PathVariable Long orderId, @RequestParam Double amount) {
        return ResponseEntity.ok(service.makePayment(orderId, amount));
    }
}
```

---

### 🔹 Step 5: Secure & Add Configs

✅ Secure endpoints using JWT (like previous services)  
✅ Add config in config repo `payment-service.yml`  
✅ Register with Eureka and test via gateway

---

# 📦 **Part B: inventory-service**

---

### 🔹 Step 1: Entity – ProductStock

📄 `entity/ProductStock.java`

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_stock")
public class ProductStock {

    @Id
    private Long productId;

    private Integer stock;
}
```

---

### 🔹 Step 2: Repository + Service

📄 `repository/ProductStockRepository.java`

```java
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
}
```

📄 `service/InventoryService.java`

```java
@Service
public class InventoryService {

    private final ProductStockRepository repo;

    public InventoryService(ProductStockRepository repo) {
        this.repo = repo;
    }

    public boolean reduceStock(Long productId, Integer qty) {
        ProductStock stock = repo.findById(productId).orElseThrow();
        if (stock.getStock() >= qty) {
            stock.setStock(stock.getStock() - qty);
            repo.save(stock);
            return true;
        }
        return false;
    }
}
```

---

### 🔹 Step 3: Controller

📄 `controller/InventoryController.java`

```java
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PutMapping("/reduce")
    public ResponseEntity<Boolean> reduce(@RequestParam Long productId, @RequestParam Integer qty) {
        return ResponseEntity.ok(service.reduceStock(productId, qty));
    }
}
```

---

### 🔹 Step 4: Secure & Configure

✅ Register with Eureka  
✅ Secure with JWT  
✅ Add `inventory-service.yml` in config repo

---

# 🔁 **Part C: Connect All in `order-service`**

---

### 🔹 Step 1: Create Feign Clients

📄 `client/PaymentClient.java`

```java
@FeignClient(name = "payment-service", configuration = FeignClientInterceptor.class)
public interface PaymentClient {
    @PostMapping("/payments/{orderId}")
    Payment makePayment(@PathVariable Long orderId, @RequestParam("amount") Double amount);
}
```

📄 `client/InventoryClient.java`

```java
@FeignClient(name = "inventory-service", configuration = FeignClientInterceptor.class)
public interface InventoryClient {
    @PutMapping("/inventory/reduce")
    Boolean reduceStock(@RequestParam("productId") Long productId, @RequestParam("qty") Integer qty);
}
```

---

### 🔹 Step 2: Enhance Order Logic

📄 `service/OrderService.java`

```java
public Order placeOrder(Order order) {
    ProductDTO product = productClient.getProductById(order.getProductId());
    order.setTotalPrice(product.getPrice() * order.getQuantity());

    boolean stockAvailable = inventoryClient.reduceStock(order.getProductId(), order.getQuantity());

    if (!stockAvailable) {
        order.setStatus("OUT_OF_STOCK");
        return repo.save(order);
    }

    order.setStatus("PROCESSING");
    Order savedOrder = repo.save(order);

    Payment payment = paymentClient.makePayment(savedOrder.getId(), savedOrder.getTotalPrice());

    if (payment.getPaymentStatus().equals("SUCCESS")) {
        savedOrder.setStatus("COMPLETED");
    } else {
        savedOrder.setStatus("FAILED");
    }

    return repo.save(savedOrder);
}
```

---

### 🔹 Step 3: Test the Full Flow

1. Register & login → get JWT
2. Make sure stock is available in inventory
3. `POST /orders` → order should complete if:
    - Stock is available
    - Payment succeeds

✅ Order lifecycle:
- OUT_OF_STOCK
- PROCESSING
- COMPLETED
- FAILED

---

## ✅ Phase 6: Summary

| Feature                                    | ✅ Completed |
|-------------------------------------------|--------------|
| Payment service with mock logic           | ✅            |
| Inventory service for stock management    | ✅            |
| Integrated with order service via Feign   | ✅            |
| Full secure inter-service flow            | ✅            |
| Order lifecycle status tracking           | ✅            |

---

🎉 Your backend is now **90% production-grade ready**. One final phase remains: **Deployment!**

Let me know when you’re ready and I’ll guide you through:

# 🚀 **Phase 7: Dockerization + Deployment (Heroku / AWS / EC2 / Render)**

This will include:
- Docker for each service
- Docker Compose to run locally
- Deploying all services (with configs, Eureka, API Gateway)

Ready to go big? 😎