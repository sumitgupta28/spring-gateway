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