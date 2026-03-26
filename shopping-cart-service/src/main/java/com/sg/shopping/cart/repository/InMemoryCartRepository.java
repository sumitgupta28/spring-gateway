package com.sg.shopping.cart.repository;

import com.sg.shopping.cart.model.Cart;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemoryCartRepository implements CartRepository {
    private final Map<String, Cart> store = new ConcurrentHashMap<>();

    @Override
    public Mono<Cart> findById(String cartId) {
        log.debug("Repository findById: {}", cartId);
        Cart cart = store.get(cartId);
        return Mono.justOrEmpty(cart)
                .doOnNext(c -> log.trace("Repository returned cart id={} items={}", cartId, c.getItems().size()));
    }

    @Override
    public Mono<Cart> save(Cart cart) {
        log.debug("Repository save cart id={}", cart.getCartId());
        store.put(cart.getCartId(), cart);
        return Mono.just(cart)
                .doOnSuccess(c -> log.trace("Repository saved cart id={} items={}", c.getCartId(), c.getItems().size()));
    }

    @Override
    public Mono<Void> delete(String cartId) {
        log.debug("Repository delete cart id={}", cartId);
        return Mono.fromRunnable(() -> {
            store.remove(cartId);
            log.trace("Repository removed cart id={}", cartId);
        }).then();
    }
}
