package com.sg.shopping.cart.controller;

import com.sg.shopping.cart.dto.AddItemRequest;
import com.sg.shopping.cart.dto.CheckoutResponse;
import com.sg.shopping.cart.model.Cart;
import com.sg.shopping.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/carts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Validated
public class CartController {
    private final CartService service;

    @GetMapping
    public Mono<ResponseEntity<Cart>> createCart() {
        log.info("Received request: createCart");
        return service.createCart()
                .doOnSuccess(c -> log.info("Created cart id={}", c.getCartId()))
                .map(c -> new ResponseEntity<>(c, HttpStatus.CREATED));
    }

    @GetMapping("/{cartId}")
    public Mono<ResponseEntity<Cart>> getCart(@PathVariable String cartId) {
        log.info("Received request: getCart id={}", cartId);
        return service.getCart(cartId)
                .doOnSuccess(c -> {
                    if (c != null) log.debug("Returning cart id={} items={}", cartId, c.getItems().size());
                    else log.debug("Cart id={} not found", cartId);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/{cartId}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Cart>> addItem(@PathVariable String cartId, @Valid @RequestBody AddItemRequest req) {
        log.info("Received request: addItem cartId={} productId={} qty={}", cartId, req.getProductId(), req.getQuantity());
        return service.addItem(cartId, req)
                .doOnSuccess(c -> log.debug("Added item productId={} to cartId={} totalItems={}", req.getProductId(), cartId, c.getItems().size()))
                .map(c -> new ResponseEntity<>(c, HttpStatus.CREATED));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public Mono<ResponseEntity<Cart>> removeItem(@PathVariable String cartId, @PathVariable String productId) {
        log.info("Received request: removeItem cartId={} productId={}", cartId, productId);
        return service.removeItem(cartId, productId)
                .doOnSuccess(c -> log.debug("Removed productId={} from cartId={} itemsNow={}", productId, cartId, c.getItems().size()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cartId}/items")
    public Mono<ResponseEntity<Cart>> clearCart(@PathVariable String cartId) {
        log.info("Received request: clearCart id={}", cartId);
        return service.clearCart(cartId)
                .doOnSuccess(c -> log.debug("Cleared cart id={} itemsNow={}", cartId, c.getItems().size()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{cartId}/checkout")
    public Mono<ResponseEntity<CheckoutResponse>> checkout(@PathVariable String cartId) {
        log.info("Received request: checkout id={}", cartId);
        return service.checkout(cartId)
                .doOnSuccess(r -> log.info("Checkout completed cartId={} total={} items={}", r.getCartId(), r.getTotal(), r.getItems().size()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
