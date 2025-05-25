package com.example.price_comparator.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceAlertDTO {
    private String id;
    private String productId;
    private Long storeId;
    private BigDecimal targetPrice;
    private boolean isActive;
    private LocalDateTime createdAt;
}
