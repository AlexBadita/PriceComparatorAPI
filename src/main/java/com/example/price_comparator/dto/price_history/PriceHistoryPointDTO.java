package com.example.price_comparator.dto.price_history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistoryPointDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private BigDecimal discountedPercentage;
}
