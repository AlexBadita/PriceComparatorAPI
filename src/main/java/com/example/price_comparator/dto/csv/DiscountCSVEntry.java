package com.example.price_comparator.dto.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DiscountCSVEntry extends BaseCSVEntry {
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
    @CsvDate("yyyy-MM-dd")
    private LocalDate fromDate;

    @CsvBindByName(column = "to_date")
    @CsvDate("yyyy-MM-dd")
    private LocalDate toDate;

    @CsvBindByName(column = "percentage_of_discount")
    private BigDecimal percentage;
}
