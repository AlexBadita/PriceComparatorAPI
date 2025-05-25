package com.example.price_comparator.dto.basket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreBasketDTO {
    private Long storeId;
    private String storeName;
    private List<BasketItemDTO> items;
    private BigDecimal totalPrice;
}
