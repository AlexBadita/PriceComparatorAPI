package com.example.price_comparator.dto.price_history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistoryStoreDTO {
    private String storeName;
    private List<PriceHistoryPointDTO> prices;
}
