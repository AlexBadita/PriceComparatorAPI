package com.example.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountProductDTO {
    private String id;
    private String name;
    private CategoryDTO category;
    private BrandDTO brand;
    private BigDecimal packageQuantity;
    private String packageUnit;
    private PriceDTO price;
}
