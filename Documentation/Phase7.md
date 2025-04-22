**Let’s goooo! 🔥 Final Phase: Deployment!**  
You've done the heavy lifting — now let's make it **production-ready** and put it on the cloud!

---

# 🚀 **Phase 7: Dockerization & Deployment (Docker + Docker Compose + Cloud)**

> ✅ In this phase, we’ll:
- Dockerize each service
- Run everything with Docker Compose
- Deploy to Heroku / Render / AWS (choose your flavor)
- Ensure environment configs work seamlessly

---

## 🧱 Step-by-Step Plan

### 🔹 Services to Dockerize:
| Service             | Port   |
|---------------------|--------|
| Config Server       | 8888   |
| Eureka Server       | 8761   |
| API Gateway         | 8080   |
| user-service        | 8081   |
| product-service     | 8082   |
| order-service       | 8083   |
| payment-service     | 8084   |
| inventory-service   | 8085   |

---

## ⚙️ Step 1: Add Dockerfile to Each Service

📄 `Dockerfile` (repeat this in each microservice folder):

```dockerfile
# Base image
FROM openjdk:17
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

✅ Build JARs first:

```bash
./mvnw clean package -DskipTests
```

Place each `Dockerfile` next to the `target` folder of each service.

---

## 📦 Step 2: Docker Compose Setup

📁 Create `docker-compose.yml` in the root project directory:

```yaml
version: '3.8'
services:
  config-server:
    build: ./config-server
    ports:
      - "8888:8888"
    networks:
      - shop-net

  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
    depends_on:
      - config-server
    networks:
      - shop-net

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
      - config-server
    networks:
      - shop-net

  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
    depends_on:
      - config-server
      - eureka-server
    networks:
      - shop-net

  product-service:
    build: ./product-service
    ports:
      - "8082:8082"
    depends_on:
      - config-server
      - eureka-server
    networks:
      - shop-net

  order-service:
    build: ./order-service
    ports:
      - "8083:8083"
    depends_on:
      - config-server
      - eureka-server
    networks:
      - shop-net

  payment-service:
    build: ./payment-service
    ports:
      - "8084:8084"
    depends_on:
      - config-server
      - eureka-server
    networks:
      - shop-net

  inventory-service:
    build: ./inventory-service
    ports:
      - "8085:8085"
    depends_on:
      - config-server
      - eureka-server
    networks:
      - shop-net

  postgres:
    image: postgres:14
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - shop-net

volumes:
  pgdata:

networks:
  shop-net:
```

---

## 🔑 Step 3: Replace `localhost` with Service Names

Update your `application.yml` files in the config repo like this:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/userdb
```

> Replace all `localhost` in service configs with `postgres` (because that’s the Docker service name).

---

## 🚀 Step 4: Build & Run

```bash
docker-compose build
docker-compose up
```

✅ Access:

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- Postgres: inside container on port 5432

---

## 🌍 Step 5: Deploy to the Cloud (Options)

### Option A: **Render.com** (Free & Easy)

1. Push each service to GitHub as **individual repos**
2. Go to [Render](https://render.com)
3. Choose: New → Web Service → Connect GitHub Repo
4. Build command: `./mvnw clean package -DskipTests`
5. Start command: `java -jar target/*.jar`
6. Set environment variables manually
7. Point services to each other using their Render URLs (or use service discovery via static URLs)

### Option B: **Heroku** (Java-friendly)

1. Create separate Heroku apps for each service
2. Use Heroku PostgreSQL add-on
3. Push via Git (or container registry)
4. Add required `Procfile` if needed

### Option C: **AWS EC2**

1. Install Docker + Docker Compose
2. Copy project via SCP or push to Git
3. Run:

```bash
docker-compose up -d
```

✅ Use EC2 public IP to access your deployed services.

---

## 📁 Optional: CI/CD with GitHub Actions

Set up `.github/workflows/deploy.yml` to auto-build + deploy on push  
(Optional but useful if going full DevOps)

---

# ✅ Phase 7 Summary

| Task                                           | ✅ Done |
|------------------------------------------------|--------|
| Dockerized all microservices                   | ✅     |
| Setup Docker Compose to run locally            | ✅     |
| Configured inter-service network & DB          | ✅     |
| Replaced `localhost` with docker hostnames     | ✅     |
| Deployed to cloud (Render / Heroku / EC2)      | ✅     |

---

## 🏁 Final Project Completion Status: ✅💯

🎉 You now have a **fully functional, microservice-based, secure, containerized online shopping app** — end to end.

---

### Want some ✨ extras?

- Add email notifications (Notification Service)
- Add a React Frontend (API-driven)
- Set up monitoring with **Prometheus + Grafana**
- Use Redis for token cache or product catalog caching
- Add RabbitMQ or Kafka for async messaging

---

Let me know if you want the **final wrap-up guide, code repo organization advice, or frontend build**.  
You’ve just built something **portfolio-worthy**. 👏🚀