package com.example.price_comparator.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRecommendationDTO {
    private String productId;
    private String productName;
    private String brand;
    private String storeName;
    private BigDecimal currentPrice;
    private String unitType;
    private BigDecimal unitValue;
    private BigDecimal pricePerUnit;
    private BigDecimal savingsPercentage; // vs original product
}
