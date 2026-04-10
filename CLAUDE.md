# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build all modules
./gradlew clean build --no-daemon

# Build a single module
./gradlew :product-service:build --no-daemon

# Run individual services (each in a separate terminal)
./gradlew :spring-cloud-gateway:bootRun --no-daemon   # port 8080
./gradlew :shopping-cart-service:bootRun --no-daemon  # port 8081
./gradlew :product-service:bootRun --no-daemon        # port 8082

# Run all services in background (logs written to logs/)
./start-all.sh
./stop-all.sh

# Run Gatling load tests (default: ProductSimulation)
./gradlew :gatling-test:runGatling --no-daemon
./gradlew :gatling-test:runGatling --no-daemon -Dgatling.simulation=com.sg.gatling.simulation.CartSimulation
```

## Architecture

This is a **Gradle multi-module Spring Boot microservices project** using Java 21 and Spring Boot 3.5.x with Spring Cloud 2025.x.

```
Client → spring-cloud-gateway (8080)
           ├─ /api/products/** → product-service (8082)
           └─ /api/carts/**   → shopping-cart-service (8081)
```

**Modules** (`settings.gradle`):
- `spring-cloud-gateway` — Routes requests; global error handling via `GlobalErrorHandler`; routing is config-only (no code-based route beans)
- `product-service` — CRUD for products; seeds 20 in-memory products on startup
- `shopping-cart-service` — Cart lifecycle (create, add/remove items, checkout); in-memory storage
- `gatling-test` — Load test simulations (`ProductSimulation`, `CartSimulation`)

**Gateway routing** is defined entirely in `spring-cloud-gateway/src/main/resources/application.yml` — no Java `RouteLocator` beans.

**Data layer**: Both services use `ConcurrentHashMap`-based in-memory repositories — there is no database. Replace with R2DBC/Redis/MongoDB for persistence.

**Reactive stack**: All services use Spring WebFlux (non-blocking). Controllers return `Mono`/`Flux`. Each service has its own `GlobalErrorHandler` for validation and application errors.

## Module Structure Pattern

Each service follows this package layout under `com.sg.<module>`:
```
controller/   — WebFlux @RestController
service/      — Business logic
repository/   — In-memory store (ConcurrentHashMap)
model/        — Domain entities
dto/          — Request/response objects + ApiError
exception/    — GlobalErrorHandler (@ControllerAdvice)
```

## Key Config Files

| File | Purpose |
|------|---------|
| `spring-cloud-gateway/src/main/resources/application.yml` | Route definitions (the single source of routing truth) |
| `product-service/src/main/resources/application.yml` | Port 8082, logging |
| `shopping-cart-service/src/main/resources/application.yml` | Port 8081, logging |
| `build.gradle` (root) | Spring Boot 3.5.12 + Spring Cloud 2025.0.1 dependency management |
| `gatling-test/build.gradle` | Gatling 3.9.5 plugin + fat-JAR task |
