package com.example.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceEntryDTO {
    private BigDecimal price;
    private String currency;
    private LocalDate entryDate;
}
