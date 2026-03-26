package com.sg.shopping.cart.service;

import com.sg.shopping.cart.dto.AddItemRequest;
import com.sg.shopping.cart.dto.CheckoutResponse;
import com.sg.shopping.cart.model.Cart;
import com.sg.shopping.cart.model.CartItem;
import com.sg.shopping.cart.repository.CartRepository;
import com.sg.shopping.cart.repository.InMemoryCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CartService {
    private final CartRepository repository;

    public CartService() {
        this.repository = new InMemoryCartRepository();
        log.info("CartService initialized with InMemoryCartRepository");
    }

    // constructor for testing or custom repository
    public CartService(CartRepository repository) {
        this.repository = repository;
        log.info("CartService initialized with custom repository: {}", repository.getClass().getSimpleName());
    }

    public Mono<Cart> createCart() {
        String id = UUID.randomUUID().toString();
        Cart cart = new Cart(id);
        log.info("Creating cart with id={}", id);
        return repository.save(cart)
                .doOnSuccess(c -> log.debug("Cart created: {}", c.getCartId()));
    }

    public Mono<Cart> getCart(String cartId) {
        log.debug("Retrieving cart id={}", cartId);
        return repository.findById(cartId)
                .doOnSuccess(c -> {
                    if (c != null) log.debug("Found cart id={} items={} ", cartId, c.getItems().size());
                    else log.debug("Cart id={} not found", cartId);
                });
    }

    public Mono<Cart> addItem(String cartId, AddItemRequest req) {
        log.info("Add item to cartId={} productId={} qty={}", cartId, req.getProductId(), req.getQuantity());
        return repository.findById(cartId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cart id={} not found, creating new cart", cartId);
                    return repository.save(new Cart(cartId));
                }))
                .flatMap(cart -> {
                    CartItem item = new CartItem(req.getProductId(), req.getName(), req.getPrice(), req.getQuantity());
                    cart.addItem(item);
                    log.debug("Cart id={} now has {} items", cartId, cart.getItems().size());
                    return repository.save(cart);
                })
                .doOnError(e -> log.error("Failed to add item to cart {}: {}", cartId, e.getMessage(), e));
    }

    public Mono<Cart> removeItem(String cartId, String productId) {
        log.info("Remove item productId={} from cartId={}", productId, cartId);
        return repository.findById(cartId)
                .flatMap(cart -> {
                    cart.removeItem(productId);
                    log.debug("Removed productId={} from cartId={}", productId, cartId);
                    return repository.save(cart);
                })
                .doOnError(e -> log.error("Failed to remove item {} from cart {}: {}", productId, cartId, e.getMessage(), e));
    }

    public Mono<Cart> clearCart(String cartId) {
        log.info("Clear cart id={}", cartId);
        return repository.findById(cartId)
                .flatMap(cart -> {
                    cart.clear();
                    log.debug("Cleared cart id={}", cartId);
                    return repository.save(cart);
                })
                .doOnError(e -> log.error("Failed to clear cart {}: {}", cartId, e.getMessage(), e));
    }

    public Mono<CheckoutResponse> checkout(String cartId) {
        log.info("Checkout cart id={}", cartId);
        return repository.findById(cartId)
                .flatMap(cart -> {
                    List<CartItem> items = new ArrayList<>(cart.getItems());
                    CheckoutResponse resp = new CheckoutResponse(cartId, cart.getTotal(), items);
                    cart.clear();
                    log.debug("Checkout completed for cartId={} total={} items={}", cartId, resp.getTotal(), resp.getItems().size());
                    return repository.save(cart).then(Mono.just(resp));
                })
                .doOnError(e -> log.error("Failed to checkout cart {}: {}", cartId, e.getMessage(), e));
    }
}
