package com.example.price_comparator.dto.price_history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistoryDTO {
    private String productId;
    private String productName;
    private String brand;
    private String category;
    private List<PriceHistoryStoreDTO> stores;
}
