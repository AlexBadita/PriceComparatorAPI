package com.example.price_comparator.dto.price_history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Filter used to query price history data for a product.
 * Enables filtering by product ID, store, category, brand, and a custom date range.
 * This DTO supports use cases like building store-separated price histograms
 * with periods reflecting entry dates and discount windows.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistoryFilter {
    /** The name of the product whose price history is requested. */
    private String productName;
    /** Optional: ID of the store to filter results. If null, include all stores. */
    private String storeName;
    /** Optional: ID of the category to filter results. */
    private String categoryName;
    /** Optional: ID of the brand to filter results. */
    private String brandName;
    /** Optional: Start date of the range to include in the price history. */
    private LocalDate startDate;
    /** Optional: End date of the range to include in the price history. */
    private LocalDate endDate;
}
