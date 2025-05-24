package com.example.price_comparator.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DiscountEntry {
    @CsvBindByName(column = "product_id")
    private String productId;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private BigDecimal packageQuantity;

    @CsvBindByName(column = "package_unit")
    private String packageUnit;

    @CsvBindByName(column = "product_category")
    private String productCategory;

    @CsvBindByName(column = "from_date")
    private LocalDate fromDate;

    @CsvBindByName(column = "to_date")
    private LocalDate toDate;

    @CsvBindByName(column = "percentage_of_discount")
    private BigDecimal percentage;

    private LocalDate entryDate;
    private String store;
}
