package com.example.price_comparator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PriceEntry {
    private String productId;
    private String productName;
    private String productCategory;
    private String brand;
    private BigDecimal packageQuantity;
    private String packageUnit;
    private BigDecimal price;
    private String currency;
    private LocalDate entryDate;
    private String store;
}
