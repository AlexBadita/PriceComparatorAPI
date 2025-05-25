package com.example.price_comparator.dto;

import com.example.price_comparator.model.enums.Currency;
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
    private Currency currency;
    private LocalDate entryDate;
}
