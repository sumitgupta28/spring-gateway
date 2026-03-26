# Spring Gateway Example (multi-module)

This repository contains a multi-module Spring Boot project with three modules:

- `spring-cloud-gateway` — Spring Cloud Gateway (routes requests to services)
- `shopping-cart-service` — Reactive shopping cart service (WebFlux)
- `product-service` — Reactive product service (WebFlux)

This README explains how to build, run, and validate the application locally.

---

## Prerequisites

- Java 21 (or the JDK specified by the Gradle toolchain in the project)
- Gradle wrapper is included (`./gradlew`)
- Recommended: `jq` for pretty-printing JSON in curl examples (optional)
- For IDE: install Lombok plugin and enable annotation processing (IntelliJ/Eclipse)

If `./gradlew` is not executable, make it executable:

```bash
chmod +x gradlew
```

---

## Build

From the repository root, build all modules using the Gradle wrapper:

```bash
./gradlew clean build --no-daemon
```

This will compile and package the following modules:
- `spring-cloud-gateway` (runs on port 8080)
- `shopping-cart-service` (runs on port 8081)
- `product-service` (runs on port 8082)

If you prefer to build a single module, run e.g.:

```bash
./gradlew :product-service:build --no-daemon
```

---

## Run (development)

Start each module in its own terminal so they run concurrently.

1) Start `product-service` (port 8082):

```bash
./gradlew :product-service:bootRun --no-daemon
```

2) Start `shopping-cart-service` (port 8081):

```bash
./gradlew :shopping-cart-service:bootRun --no-daemon
```

3) Start the gateway module (port 8080):

```bash
./gradlew :spring-cloud-gateway:bootRun --no-daemon
```

Notes:
- Module `product-service` seeds 20 dummy products on startup.
- The gateway forwards requests to backend services:
  - `/api/products/**` -> `http://localhost:8082`
  - `/api/carts/**` -> `http://localhost:8081`

---

## Run as jars

You can build and run each module's jar.

1) Build the module jars:

```bash
./gradlew :product-service:bootJar :shopping-cart-service:bootJar :spring-cloud-gateway:bootJar --no-daemon
```

2) Run jars (in separate terminals):

```bash
java -jar product-service/build/libs/product-service-0.0.1-SNAPSHOT.jar
java -jar shopping-cart-service/build/libs/shopping-cart-service-0.0.1-SNAPSHOT.jar
java -jar spring-cloud-gateway/build/libs/spring-cloud-gateway-0.0.1-SNAPSHOT.jar
```

---

## API Endpoints (quick reference)

Product service (port 8082):
- GET  /api/products                -> list all products
- GET  /api/products/{id}          -> get product
- POST /api/products               -> create product (JSON ProductRequest)
- PUT  /api/products/{id}          -> update product
- DELETE /api/products/{id}        -> delete product

Shopping cart service (port 8081):
- POST /api/carts                  -> create cart (returns cartId)
- GET  /api/carts/{cartId}         -> get cart
- POST /api/carts/{cartId}/items   -> add item to cart (Product payload)
- DELETE /api/carts/{cartId}/items/{productId} -> remove item
- DELETE /api/carts/{cartId}/items -> clear cart
- POST /api/carts/{cartId}/checkout -> checkout

Gateway (port 8080):
- proxies the above endpoints under the same paths

---

## Example validation steps (using the gateway)

1) List seeded products via gateway:

```bash
curl -s http://localhost:8080/api/products | jq
```

2) Create a cart:

```bash
CART_JSON=$(curl -s -X POST http://localhost:8080/api/carts)
echo "$CART_JSON" | jq
CART_ID=$(echo "$CART_JSON" | jq -r '.cartId')
```

3) Add a product to the cart (use a product id from step 1, e.g. `prod-1`):

```bash
curl -s -X POST http://localhost:8080/api/carts/${CART_ID}/items \
  -H "Content-Type: application/json" \
  -d '{"productId":"prod-1","name":"Product 1","price":11.0,"quantity":2}' | jq
```

4) View the cart:

```bash
curl -s http://localhost:8080/api/carts/${CART_ID} | jq
```

5) Checkout the cart:

```bash
curl -s -X POST http://localhost:8080/api/carts/${CART_ID}/checkout | jq
```

---

## Troubleshooting

- If you see Lombok-related compilation issues in your IDE, install the Lombok plugin and enable annotation processing.
- If the gateway routes are not forwarding, ensure the gateway is running (port 8080) and the target services are reachable on their ports.
- If `./gradlew` permissions error occurs, run `chmod +x gradlew`.

---

## Notes & next steps

- The in-memory repositories are intended for demo/dev only. For production, replace them with a reactive persistent store (Redis, MongoDB, R2DBC).
- Consider adding validation (`jakarta.validation`) and global error handling for nicer API responses.

If you'd like, I can:
- add automated integration tests using `WebTestClient`, or
- wire a reactive persistence backend (Redis/Mongo), or
- add request validation and a ControllerAdvice for consistent error payloads.

---

Happy to help extend or harden this project — tell me which improvement you'd like next.
