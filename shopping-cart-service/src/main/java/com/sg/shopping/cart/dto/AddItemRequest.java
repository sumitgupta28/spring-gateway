package com.sg.shopping.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
    private String productId;
    private String name;
    private BigDecimal price;
    private int quantity;
}
