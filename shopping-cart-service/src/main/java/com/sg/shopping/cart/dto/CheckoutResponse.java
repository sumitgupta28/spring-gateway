package com.sg.shopping.cart.dto;

import com.sg.shopping.cart.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {
    private String cartId;
    private BigDecimal total;
    private List<CartItem> items;
}
