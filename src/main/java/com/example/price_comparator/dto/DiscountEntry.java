package com.example.price_comparator.dto;

import java.time.LocalDate;

public class DiscountEntry {
    private String productId;
    private String productName;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private String productCategory;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int percentage;
    private LocalDate entryDate;
    private String store;
}
