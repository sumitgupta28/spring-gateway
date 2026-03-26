package com.sg.shopping.cart.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private String cartId;

    public Cart(String cartId) {
        this.cartId = cartId;
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, CartItem> items = new ConcurrentHashMap<>();

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public Map<String, CartItem> getItemsMap() {
        return items;
    }

    public void addItem(CartItem item) {
        items.merge(item.getProductId(), item, (existing, incoming) -> {
            existing.setQuantity(existing.getQuantity() + incoming.getQuantity());
            return existing;
        });
    }

    public void removeItem(String productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(i -> i.getPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
