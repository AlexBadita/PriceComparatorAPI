package com.example.price_comparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorePriceHistoryDTO {
    private StoreDTO store;
    private List<PriceEntryDTO> prices;
}
