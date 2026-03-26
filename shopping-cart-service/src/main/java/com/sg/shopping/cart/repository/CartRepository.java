package com.sg.shopping.cart.repository;

import com.sg.shopping.cart.model.Cart;
import reactor.core.publisher.Mono;

public interface CartRepository {
    Mono<Cart> findById(String cartId);
    Mono<Cart> save(Cart cart);
    Mono<Void> delete(String cartId);
}

