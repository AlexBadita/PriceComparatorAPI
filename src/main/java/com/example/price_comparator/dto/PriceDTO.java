package com.example.price_comparator.dto;

import com.example.price_comparator.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDTO {
    private BigDecimal price;
    private Currency currency;
}
