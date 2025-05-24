package com.example.price_comparator.dto;

import com.example.price_comparator.model.Store;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductPrice {
    private Store store;
    private BigDecimal originalPrice;
    private BigDecimal currentPrice;
    private boolean hasDiscount;
}
