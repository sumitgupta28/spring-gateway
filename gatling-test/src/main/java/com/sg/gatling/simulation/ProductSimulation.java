package com.sg.gatling.simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ProductSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080").acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Product Scenario")
            .exec(http("List Products").get("/api/products").check(status().is(200)))
            .pause(1)
            .exec(http("Get Product prod-1").get("/api/products/prod-1").check(status().is(200)));

    {
        setUp(scn.injectOpen(constantUsersPerSec(5).during(30))).protocols(httpProtocol);
    }
}