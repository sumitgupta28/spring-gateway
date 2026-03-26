# Gatling Tests for Spring Cloud Gateway

This module contains Gatling Java DSL simulations that exercise the `spring-cloud-gateway` and proxied services:

- `ProductSimulation` — exercises `/api/products` endpoints via the gateway
- `CartSimulation` — creates a cart via `/api/carts`, adds items, and checks out via the gateway

Prerequisites

- The gateway and backend services must be running (see main README):
    - `spring-cloud-gateway` on `http://localhost:8080`
    - `shopping-cart-service` on `http://localhost:8081`
    - `product-service` on `http://localhost:8082`

Run

To run a simulation with the Gradle task:

```bash
# run the default simulation (ProductSimulation)
./gradlew :gatling-test:runGatling --no-daemon

# run a specific simulation by passing a system property
./gradlew :gatling-test:runGatling --no-daemon -Dgatling.simulation=com.sg.gatling.simulation.CartSimulation
```

Notes

- The `runGatling` task executes `com.sg.gatling.GatlingRunner` which delegates to Gatling's main class and accepts
  the `gatling.simulation` system property to choose which simulation to run.
- Results will be written to the Gatling results directory in the project (default under `build/reports/gatling` or as
  configured by Gatling).
  package com.sg.gatling.simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class CartSimulation extends Simulation {
HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
.acceptHeader("application/json")
.contentTypeHeader("application/json");

    ScenarioBuilder scn = scenario("Cart Scenario")
            .exec(http("Create Cart").post("/api/carts")
                    .check(status().is(200), jsonPath("$.cartId").saveAs("cartId")))
            .pause(1)
            .exec(session -> {
                String cartId = session.getString("cartId");
                return session;
            })
            .exec(http("Add Item").post(session -> "/api/carts/" + session.getString("cartId") + "/items")
                    .body(StringBody("{\"productId\":\"prod-1\",\"name\":\"Product 1\",\"price\":11.0,\"quantity\":1}"))
                    .asJson()
                    .check(status().is(201)))
            .pause(1)
            .exec(http("Checkout").post(session -> "/api/carts/" + session.getString("cartId") + "/checkout")
                    .check(status().is(200)));

    {
        setUp(scn.injectOpen(constantUsersPerSec(3).during(30))).protocols(httpProtocol);
    }

}
package com.sg.gatling.simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ProductSimulation extends Simulation {
HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
.acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Product Scenario")
            .exec(http("List Products").get("/api/products").check(status().is(200)))
            .pause(1)
            .exec(http("Get Product prod-1").get("/api/products/prod-1").check(status().is(200)));

    {
        setUp(scn.injectOpen(constantUsersPerSec(5).during(30))).protocols(httpProtocol);
    }

}
package com.sg.gatling;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

public class GatlingRunner {
public static void main(String[] args) {
// Set system property 'gatling.simulation' to run a specific simulation
String simulation = System.getProperty("gatling.simulation", "com.sg.gatling.simulation.ProductSimulation");

        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder();
        props.simulationClass(simulation);

        Gatling.fromMap(props.build());
    }

}

