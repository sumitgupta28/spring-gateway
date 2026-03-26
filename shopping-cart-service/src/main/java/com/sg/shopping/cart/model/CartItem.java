package com.sg.shopping.cart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItem {
    @EqualsAndHashCode.Include
    private String productId;
    private String name;
    private BigDecimal price;
    private int quantity;
}
