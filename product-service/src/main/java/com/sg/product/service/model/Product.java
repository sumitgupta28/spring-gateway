package com.sg.product.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private String id;
    private String name;
    private BigDecimal price;
    private int availableQuantity;
}

