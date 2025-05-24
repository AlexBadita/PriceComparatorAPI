package com.example.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private Long id;
    private StoreDTO store;
    private DiscountProductDTO product;
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal percentage;
}
