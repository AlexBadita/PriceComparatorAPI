package com.example.price_comparator.dto.basket;

import com.example.price_comparator.model.Product;
import com.example.price_comparator.model.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithBestOffer {
    private Product product;
    private Store store;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private BigDecimal discountPercentage;
}
