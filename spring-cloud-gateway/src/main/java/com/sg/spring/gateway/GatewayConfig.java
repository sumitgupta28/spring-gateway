package com.sg.spring.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator shoppingCartAndProductRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("shopping-cart-route", r -> r
                        .path("/api/carts/**")
                        .uri("http://localhost:8081")
                )
                .route("product-service-route", r -> r
                        .path("/api/products/**")
                        .uri("http://localhost:8082")
                )
                .build();
    }
}

